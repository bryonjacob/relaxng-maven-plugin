package us.bryon;

import java.io.File;

public class Translation {
    private File in;
    private File out;

    public Translation() {
    }

    public Translation(File in, File out) {
        this.in = in;
        this.out = out;
    }

    public File getIn() {
        return in;
    }

    public void setIn(File in) {
        this.in = in;
    }

    public File getOut() {
        return out;
    }

    public void setOut(File out) {
        this.out = out;
    }
}
