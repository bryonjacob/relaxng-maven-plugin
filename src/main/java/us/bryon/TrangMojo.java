package us.bryon;

import com.thaiopensource.relaxng.translate.Driver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Use trang to translate one or more schemas from one language to another.
 * @goal trang
 * @phase generate-sources
 */
public class TrangMojo extends AbstractMojo {

    /**
     * rather than putting schema files into src/main/schema, individual translations can be
     * listed out one at a time in nested translation elements.
     * Example:
     * <pre>
     *          &lt;translations&gt;
     *              &lt;translation&gt;
     *                  &lt;in&gt;src/main/xsd/foo.rnc&lt;/in&gt;
     *                  &lt;out&gt;${project.build.directory}/foo.xsd&lt;/out&gt;
     *              &lt;/translation&gt;
     *          &lt;/translations&gt;
     * </pre>
     * @parameter
     */
    private List<Translation> translations;

    /**
     * source directory for schema files - all .rnc, .rng files in this directory are translated
     * by trang (.rnc -> .rng and .xsd, .rng -> .rnc and .xsd).
     * @parameter expression="${basedir}/src/main/schema"
     * @required
     */
    private File src;

    /**
     * output directory for translated schemas.
     * @parameter expression="${project.build.directory}/trang"
     * @required
     */
    private File dest;

    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Translation> translations = new ArrayList<Translation>();
        if (this.translations != null) {
            translations.addAll(this.translations);
        }
        if (src.isDirectory()) {
            String[] files = FileUtils.getFilesFromExtension(
                    src.getAbsolutePath(), new String[]{"rnc", "rng"});
            for (String in : files) {
                String out = in.replace(src.getAbsolutePath(), dest.getAbsolutePath());
                if (in.endsWith("rnc")) {
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rnc$", ".rng"))));
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rnc$", ".xsd"))));
                } else { // in.endsWith("rng")
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rng$", ".rnc"))));
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rng$", ".xsd"))));
                }
            }
        }

        for (Translation translation : translations) {
            System.out.println(
                    String.format("converting %s to %s",
                            translation.getIn(), translation.getOut()));
            translation.getOut().getParentFile().mkdirs();
            new Driver().run(new String[]{
                    translation.getIn().getAbsolutePath(),
                    translation.getOut().getAbsolutePath()
            });
        }
    }
}
