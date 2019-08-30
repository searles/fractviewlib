package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import at.searles.fractviewlib.FractalProvider;
import at.searles.fractviewlib.ParameterTable;
import at.searles.fractviewlib.data.FractalData;
import at.searles.commons.math.Scale;
import at.searles.meelan.MeelanException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class FractalProviderTest {

    private FractalData.Builder[] builders;
    private FractalProvider provider;
    private ArrayList<Integer> ids;
    private List<ParameterTable.Entry> table;

    private void withSources(String...sources) {
        builders = new FractalData.Builder[sources.length];

        for(int i = 0; i < sources.length; ++i) {
            builders[i] = new FractalData.Builder().setSource(sources[i]);
        }
    }

    private void withProvider(String...exclusiveParameters) {
        ids = new ArrayList<>();
        this.provider = new FractalProvider();

        for(String p : exclusiveParameters) {
            this.provider.addExclusiveParameter(p);
        }

        for(FractalData.Builder b : builders) {
            ids.add(this.provider.addFractal(b.commit()));
        }
    }

    @Test
    public void testSetExclusiveSources() {
        withSources("var a = 1;", "var a = 1;");

        withProvider(Fractal.SOURCE_LABEL);

        provider.setParameterValue(Fractal.SOURCE_LABEL, ids.get(0), "var b = 2;");

        Assert.assertEquals("var b = 2;", provider.getParameter(Fractal.SOURCE_LABEL, ids.get(0)).value);
        Assert.assertEquals("var a = 1;", provider.getParameter(Fractal.SOURCE_LABEL, ids.get(1)).value);
    }

    @Test
    public void testSetNonExclusiveSources() {
        withSources("var a = 1;", "var a = 1;");

        withProvider();

        provider.setParameterValue(Fractal.SOURCE_LABEL, ids.get(0), "var b = 2;");

        Assert.assertEquals("var b = 2;", provider.getParameter(Fractal.SOURCE_LABEL, ids.get(0)).value);
        Assert.assertEquals("var b = 2;", provider.getParameter(Fractal.SOURCE_LABEL, ids.get(1)).value);
    }

    @Test
    public void testOrderExterns() {
        withSources("extern a expr = \"0\"; extern c expr = \"0\"; var x = a + b + c",
                "extern a expr = \"0\"; extern b expr = \"0\"; extern c expr = \"0\"; extern d expr = \"0\"; var x = a + b + c + d");

        withProvider();

        createTable(0);
        
        StringBuilder s = new StringBuilder();
        for (ParameterTable.Entry entry : table) {
            s.append(entry.key);
        }

        Assert.assertEquals("SourceScaleabcd", s.toString());
    }

    @Test
    public void testOrderWithKeySwitch() {
        withSources("extern a expr = \"0\"; extern b expr = \"0\"; var d = a + b",
                "extern b expr = \"0\"; extern a expr = \"0\"; var d = a + b");

        withProvider();

        createTable(ids.get(1));

        Assert.assertEquals((int) ids.get(1), table.get(3).id);

        Assert.assertEquals("a", table.get(3).key);
        Assert.assertEquals("b", table.get(2).key);

        createTable(ids.get(0));

        Assert.assertEquals("a", table.get(2).key);
        Assert.assertEquals("b", table.get(3).key);
    }

    @Test
    public void testTypeChangeOnImplicitExterns() {
        withSources("extern a expr = \"b\"; var d = a",
                "extern a expr = \"0\"; extern b int = 1; var d = a + b");

        withProvider();

        provider.setParameterValue("b", -1, "10");

        // id is ignored because it is a non-shared parameter
        Assert.assertEquals("10", provider.getParameter("b", ids.get(0)).value);
        Assert.assertEquals(1, provider.getParameter("b", ids.get(1)).value);
    }

    @Test
    public void testTypeToleranceInExprOnImplicitExterns() {
        withSources("extern a expr = \"b\"; var d = a",
                "extern a expr = \"0\"; extern b int = 1; var d = a + b");

        withProvider();

        provider.setParameterValue("b", -1, 2);
        Assert.assertEquals("0", provider.getParameter("b", ids.get(0)).value);
    }

    @Test
    public void testIndividualsWithSingularExtern() {
        withSources("extern a int = 0; extern b int = 1; var d = a + b",
                "extern a int = 0; extern b int = 1; extern c int = 2; var d = a + b + c");

        withProvider("b");

        createTable(-1);

        Assert.assertEquals(4 + 2, table.size()); // + 2 is scale/source

        Assert.assertEquals("Source", table.get(0).key);

        Assert.assertEquals("b", table.get(3).key);
        Assert.assertNotEquals(-1, table.get(3).id);

        Assert.assertEquals("b", table.get(4).key);
        Assert.assertNotEquals(-1, table.get(4).id);

        Assert.assertEquals("c", table.get(5).key);

        Assert.assertEquals("a", table.get(2).key);
    }

    @Test
    public void testParametersChangeOnFractalRemoval() {
        withSources("extern a int = 0; extern b int = 1; var d = a + b",
                "extern c int = 2; var d = c");

        FractalProvider p = new FractalProvider();

        int id0 = p.addFractal(builders[0].commit());

        List<ParameterTable.Entry> t = p.createTable(id0);

        Assert.assertEquals(4, t.size());

        int id1 = p.addFractal(builders[1].commit());

        t = p.createTable(id0);

        Assert.assertEquals(5, t.size());

        p.removeFractal(id0);

        t = p.createTable(id1);

        Assert.assertEquals(1, p.fractalCount());
        Assert.assertEquals(3, t.size());

        int newId = p.addFractal(builders[0].commit());

        t = p.createTable(newId);

        Assert.assertEquals(5, t.size());

        p.removeFractal(newId);

        t = p.createTable(id1);

        Assert.assertEquals(1, p.fractalCount());
        Assert.assertEquals(3, t.size());
    }

    @Test
    public void testSetSource() {
        withSources("extern a expr = \"0\"; var d = a");
        withProvider();

        provider.setParameterValue("Source", -1, "extern b expr = \"1\"; var c = b");
        createTable(-1);
        Assert.assertEquals("b", table.get(2).key);
    }

    @Test
    public void testExternExpr() {
        withSources("extern a expr = \"0\"; var d = a");
        withProvider();
        createTable(-1);

        Assert.assertEquals("a", table.get(2).key);
    }

    @Test
    public void testExternExprNonDefault() {
        withSources("extern a expr = \"0\"; var d = a");

        withProvider();

        provider.setParameterValue("a", -1, "1");

        // 0 is scale
        Assert.assertEquals("1", provider.getParameter("a", ids.get(0)).value);
    }

    @Test
    public void testSetScale() {
        withSources("var a = 0");

        withProvider();

        Fractal fractal = provider.getFractal(ids.get(0));

        provider.setParameterValue("Scale", -1, Scale.createScaled(1));

        Scale sc = fractal.scale();
        Assert.assertEquals(sc.xx, 1., 1e-24);
    }

    @Test
    public void testExternExprParsingError() {
        withSources("extern a expr = \"0\"; var d = a");
        withProvider();

        try {
            provider.setParameterValue("a", -1, "+1");
            Assert.fail();
        } catch(MeelanException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListeners() {
        // Set-up:
        withSources("extern a int = 0; extern b int = 1; var d = a + b",
                "extern a int = 0; extern b int = 1; extern c int = 2; var d = a + b + c");

        withProvider("b");

        int[] listenerCalled = new int[]{0, 0};

        Fractal.Listener listener0 = fractal -> listenerCalled[0]++;

        Fractal.Listener listener1 = fractal -> listenerCalled[1]++;

        provider.getFractal(1).addListener(listener0);
        provider.getFractal(2).addListener(listener1);

        // Act:
        provider.setParameterValue("a", -1, 5);

        // Should have been modified in both
        Assert.assertEquals(1, listenerCalled[0]);
        Assert.assertEquals(1, listenerCalled[1]);

        provider.setParameterValue("b", ids.get(0), 9);

        Assert.assertEquals(2, listenerCalled[0]); // individual in first.
        Assert.assertEquals(1, listenerCalled[1]);

        // c is only defined in fractal (1).
        provider.setParameterValue("c", ids.get(0), 13);

        // d is not defined
        provider.setParameterValue("d", ids.get(0), 404);

        createTable(0);

        Assert.assertEquals(2, listenerCalled[0]); // individual in first.
        Assert.assertEquals(2, listenerCalled[1]);

        Assert.assertEquals("b", table.get(3).key); // b in [0]
        Assert.assertEquals((int) ids.get(0), table.get(3).id);

        Assert.assertEquals("b", table.get(4).key); // b in [1]
        Assert.assertEquals((int) ids.get(1), table.get(4).id);

        Assert.assertEquals("c", table.get(5).key); // c in [1]
        Assert.assertEquals("c", table.get(5).key); // c in [1]

        Assert.assertEquals("a", table.get(2).key); // a in both
    }

    private void createTable(int selectedId) {
        this.table = provider.createTable(selectedId);
    }

}
