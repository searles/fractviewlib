package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import at.searles.fractviewlib.data.FractalData;
import org.junit.Assert;
import org.junit.Test;

public class PaletteTest {
    @Test
    public void paletteInlineTest() {
        String source = "extern lakepalette palette = [" +
                "[#000, #000, #000, #000]," +
                "[#f00, #ff0, #0f8, #00f]," +
                "[#f88, #ff8, #afc, #88f]];" +
                "var x = lakepalette (1:1)";

        Fractal f = Fractal.fromData(new FractalData.Builder().setSource(source).commit());

        int[] code = f.code();

        Assert.assertNotNull(code);
    }
}
