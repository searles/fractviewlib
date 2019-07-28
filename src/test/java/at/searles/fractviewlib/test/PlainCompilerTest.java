package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import org.junit.Test;

import java.io.IOException;

public class PlainCompilerTest {
    private String source;

    @Test
    public void testNewDefault() throws IOException {
        withFile("NewDefault.fv");
        actCompile();
    }

    @Test
    public void testNewDefaultWith3D() throws IOException {
        withFile("NewDefaultWith3D.fv");
        actCompile();
    }

    private void actCompile() {
        Fractal fractal = Fractal.fromSource(source);
    }

    private void withFile(String filename) throws IOException {
        source = Utils.readResourceFile(filename);
    }
}
