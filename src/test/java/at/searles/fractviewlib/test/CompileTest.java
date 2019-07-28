package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import at.searles.fractviewlib.data.FractalData;
import at.searles.meelan.MeelanException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class CompileTest {

    FractalData.Builder builder;
    private Fractal fractal;

    @Before
    public void setUp() {
        this.builder = new FractalData.Builder();
    }

    @Test
    public void testAdditionOfThree() throws IOException {
        withSourceFile("assets/sources/v3/ThreeBug.fv");

        actCompileFractal();

        Assert.assertNotNull(fractal.code());
    }

    @Test
    public void testSimpleBug() throws IOException {
        withSourceFile("assets/sources/v3/Simple.fv");

        actCompileFractal();

        Assert.assertNotNull(fractal.code());
    }

    @Test
    public void testCompileDefault() throws IOException {
        withSourceFile("assets/sources/v3/Default.fv");

        actCompileFractal();

        Assert.assertNotNull(fractal.code());
    }

    @Test
    public void testV3() throws IOException {
        File dir = new File("at/searles/fractviewlib/test/resources/assets/sources/v3");

        File[] files = dir.listFiles();

        for(File file : files) {
            this.builder = new FractalData.Builder();
            this.builder.setSource(Utils.readFile(file));

            try {
                actCompileFractal();
            } catch(MeelanException e) {
                e.printStackTrace();
                Assert.fail(file.toString());
            }
        }
    }

    private void actCompileFractal() {
        fractal = Fractal.fromData(builder.commit());
    }

    private void withSourceFile(String filename) throws IOException {
        String source = Utils.readResourceFile(filename);
        builder.setSource(source);
    }
}
