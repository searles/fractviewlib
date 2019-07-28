package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import at.searles.fractviewlib.data.FractalData;
import at.searles.fractviewlib.data.ParameterType;
import at.searles.commons.math.Scale;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the central fractal class
 */
public class FractalTest {
    static Fractal fromSource(String source) {
        return Fractal.fromData(new FractalData.Builder().setSource(source).commit());
    }

    static Fractal fromSource(String source, String id, ParameterType type, Object value) {
        FractalData.Builder b = new FractalData.Builder().setSource(source);
        assert b.queryType(id) == type;
        b.addParameter(id, value);
        return Fractal.fromData(b.commit());
    }

    @Test
    public void testFractalDefaultParameter() {
        Fractal fractal = fromSource("extern a int = 1; var x = a;");
        Assert.assertTrue(fractal.getParameter("a").isDefault);
    }

    @Test
    public void testFractalNonDefaultParameter() {
        Fractal fractal = fromSource("extern a int = 1; var x = a;", "a", ParameterType.Int, 2);
        Assert.assertFalse(fractal.getParameter("a").isDefault);
    }

    @Test
    public void testFractalIntCodeWithExtern() {
        Fractal fractal = fromSource("extern a int = 1; var x = a;", "a", ParameterType.Int, 2);
        Assert.assertEquals(2, fractal.code()[1]);
    }

    @Test
    public void testResetParameter() {
        Fractal fractal = fromSource("extern a int = 1; var x = a;", "a", ParameterType.Int, 2);

        fractal.updateValue("a", null);

        Assert.assertEquals(1, fractal.code()[1]);
    }

    @Test
    public void testAddExternParameter() {
        Fractal fractal = fromSource("extern a expr = \"1\"; var x = a;");

        fractal.updateValue("a", "b"); // b is now a new extern parameter.

        Assert.assertNotNull(fractal.getParameter("b"));
    }

    @Test
    public void testKeepExternParameter() {
        Fractal fractal = fromSource("extern a expr = \"1\"; var x = a;");

        fractal.updateValue("a", "b"); // b is now a new extern parameter.

        fractal.updateValue("b", "13");

        fractal.updateValue("a", "0");

        Assert.assertNull(fractal.getParameter("b"));

        fractal.updateValue("a", "b");

        Assert.assertNotNull(fractal.getParameter("b"));
        Assert.assertEquals("13", fractal.getParameter("b").value);
    }

    @Test
    public void testFractalHasScale() {
        Fractal fractal = fromSource("var x = 0;");

        Assert.assertNotNull(fractal.getParameter(Fractal.SCALE_LABEL));
    }

    @Test
    public void testOverrideDefaultScale() {
        Fractal fractal = fromSource("extern Scale scale = [5, 0, 0, 5, 0, 0]; var x = 0;");

        Scale scale = fractal.scale();

        Assert.assertTrue(5. == scale.xx);
    }
}
