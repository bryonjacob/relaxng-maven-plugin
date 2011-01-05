package us.bryon.relaxng.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.List;

public class TrangMojoTest extends AbstractMojoTestCase {

    private File barRnc;
    private File barXsd;
    private File fooRng;
    private File fooXsd;

    protected void setUp() throws Exception {
        super.setUp();
        barRnc = new File(TrangMojoTest.class.getResource("bar.rnc").toURI());
        barXsd = new File(TrangMojoTest.class.getResource("bar.xsd").toURI());
        fooRng = new File(TrangMojoTest.class.getResource("foo.rng").toURI());
        fooXsd = new File(TrangMojoTest.class.getResource("foo.xsd").toURI());
    }

    public void testDirectoryExecution() throws Exception {
        TrangMojo trangMojo = (TrangMojo) lookupMojo("trang",
                new File(getClass().getResource("directory-config.xml").toURI()));
        File src = (File) getVariableValueFromObject(trangMojo, "src");
        File dest = (File) getVariableValueFromObject(trangMojo, "dest");
        List<Translation> translations =
                (List<Translation>) getVariableValueFromObject(trangMojo, "translations");

        assertTrue(src.isDirectory());
        if (dest.exists()) {
            FileUtils.deleteDirectory(dest);
        }
        assertFalse(dest.exists());
        assertNull(translations);

        trangMojo.execute();

        assertTrue(dest.isDirectory());

        assertTrue(FileUtils.contentEquals(barRnc, new File(dest, "bar.rnc")));
        assertTrue(FileUtils.contentEquals(barXsd, new File(dest, "bar.xsd")));
        assertTrue(FileUtils.contentEquals(fooRng, new File(dest, "foo.rng")));
        assertTrue(FileUtils.contentEquals(fooXsd, new File(dest, "foo.xsd")));
        assertFalse(new File(dest, "foo.rnc").isFile());
        assertFalse(new File(dest, "bar.rng").isFile());
    }

    public void testExplicitExecution() throws Exception {
        TrangMojo trangMojo = (TrangMojo) lookupMojo("trang",
                new File(getClass().getResource("explicit-config.xml").toURI()));
        File src = (File) getVariableValueFromObject(trangMojo, "src");
        File dest = (File) getVariableValueFromObject(trangMojo, "dest");
        List<Translation> translations =
                (List<Translation>) getVariableValueFromObject(trangMojo, "translations");

        assertFalse(src.isDirectory());
        assertNotNull(translations);
        assertEquals(1, translations.size());
        for (Translation translation : translations) {
            File in = (File) getVariableValueFromObject(translation, "in");
            File out = (File) getVariableValueFromObject(translation, "out");
            assertTrue(in.isFile());
            if (out.exists()) {
                out.delete();
            }
            assertFalse(out.exists());
        }

        trangMojo.execute();

        for (Translation translation : translations) {
            File in = (File) getVariableValueFromObject(translation, "in");
            File out = (File) getVariableValueFromObject(translation, "out");
            assertTrue(in.isFile());
            assertTrue(FileUtils.contentEquals(fooXsd, out));
        }
        assertTrue(!dest.exists() || dest.listFiles().length == 0);
    }
}
