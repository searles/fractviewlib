package at.searles.fractviewlib.test;

import at.searles.fractviewlib.Fractal;
import at.searles.fractviewlib.FractalCollection;
import at.searles.fractviewlib.ParameterTable;
import at.searles.fractviewlib.data.FractalData;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ParameterTableTest {
    private FractalCollection collection;
    private List<ParameterTable.Entry> table;
    private Map<Integer, Integer> ids;

    private void withFractals(FractalData...dataFields) {
        collection = new FractalCollection();
        ids = new HashMap<>();

        int i = 0;

        for(FractalData data : dataFields) {
            int id = collection.add(Fractal.fromData(data));
            ids.put(i++, id);
        }
    }

    private void withExclusiveParameters(String...keys) {
        Set<String> set = new TreeSet<>(Arrays.asList(keys));

        table = ParameterTable.create(0, collection, set);
    }

    @Test
    public void testIndividualParameters() {
        withFractals(
            new FractalData.Builder()
                    .setSource("extern a int = 0; extern b int = 1; var c = a + b").commit(),
            new FractalData.Builder()
                    .setSource("extern a int = 0; extern b int = 1; var c = a + b").commit()
        );

        withExclusiveParameters("b");

        Assert.assertEquals(3 + 2, table.size()); // + 2 is Scale/Source

        // Individuals first

        Assert.assertEquals("b", table.get(3).key);
        Assert.assertEquals(ids.get(0), (Integer) table.get(3).id);

        Assert.assertEquals("b", table.get(4).key);
        Assert.assertEquals(ids.get(1), (Integer) table.get(4).id);

        Assert.assertEquals("Source", table.get(0).key);

        Assert.assertEquals("Scale", table.get(1).key);

        Assert.assertEquals("a", table.get(2).key);
    }

    @Test
    public void testOrderExterns() {
        withFractals(
                new FractalData.Builder()
                        .setSource("extern a expr = \"0\"; extern c expr = \"0\"; var x = a + b + c").commit(),
                new FractalData.Builder()
                        .setSource("extern a expr = \"0\"; extern b expr = \"0\"; extern c expr = \"0\"; extern d expr = \"0\"; var x = a + b + c + d").commit()
        );

        withExclusiveParameters("b");

        StringBuilder s = new StringBuilder();
        for (ParameterTable.Entry entry : table) {
            s.append(entry.key);
        }

        Assert.assertEquals("SourceScaleabbcd", s.toString());
    }
}
