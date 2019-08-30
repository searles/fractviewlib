package at.searles.fractviewlib;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Basically a list with custom indices=ids.
 */
public class FractalCollection {

    private ArrayList<Integer> order;
    private TreeMap<Integer, Fractal> fractals;

    private int nextId; // counter

    public FractalCollection() {
        nextId = 1; // start with 1.
        this.order = new ArrayList<>(16);
        this.fractals = new TreeMap<>();
    }

    public Fractal get(int id) {
        return fractals.get(id);
    }

    /**
     * Treat this as immutable list.
     */
    public List<Integer> ids() {
        return order;
    }

    public int add(Fractal fractal) {
        int id = nextId++;

        order.add(id);
        fractals.put(id, fractal);

        return id;
    }

    public boolean remove(int id) {
        order.remove((Integer) id);
        return fractals.remove(id) != null;
    }

    public int size() {
        return fractals.size();
    }
}
