package com.homeaway;

import com.thaiopensource.relaxng.translate.Driver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal trang
 * @phase generate-sources
 */
public class TrangMojo extends AbstractMojo {

    /**
     * @parameter
     */
    private List<Translation> translations;

    /**
     * @parameter expression="${basedir}/src/main/schema"
     * @required
     */
    private File src;

    /**
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
                    src.getAbsolutePath(), new String[]{"rnc", "rng", "xsd"});
            for (String in : files) {
                String out = in.replace(src.getAbsolutePath(), dest.getAbsolutePath());
                if (in.endsWith("rnc")) {
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rnc$", ".rng"))));
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.rnc$", ".xsd"))));
                } else if (in.endsWith("xsd")) {
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.xsd$", ".rng"))));
                    translations.add(new Translation(
                            new File(in),
                            new File(out.replaceAll("\\.xsd$", ".rnc"))));
                } else if (in.endsWith("rng")) {
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
