package at.searles.fractviewlib;

import at.searles.fractviewlib.data.FractalData;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * There is one source file for a fractal provider. It is compiled and that way,
 * the instance of ExternData in the fractalprovider is filled.
 *
 * Short explaination: Every fractal is uniquely addessed
 * by an integer top in the collection. If a fractal
 * is removed, its id is not reassigned. Parameters are addressed by their name/id.
 *
 */
public class FractalProvider {

    private final FractalCollection collection;
    private final TreeSet<String> exclusiveParameters;

    private transient final ArrayList<Listener> listeners;

    public FractalProvider() {
        this.collection = new FractalCollection();
        this.exclusiveParameters = new TreeSet<>();
        listeners = new ArrayList<>(2);
    }

    public List<ParameterTable.Entry> createTable(int selectedId) {
        return ParameterTable.create(selectedId, collection, exclusiveParameters);
    }

    public Fractal.Parameter getParameter(String key, int id) {
        return collection.get(id).getParameter(key);
    }

    // === Handle parameters ===

    private void fireParametersUpdated() {
        for(Listener l : listeners) {
            l.parameterMapUpdated(this);
        }
    }

    private boolean setIndividualParameterValue(String key, int id, Object value) {
        return collection.get(id).setValue(key, value);
    }

    /**
     * Sets the parameter. If parameter is shared, parameter is set in
     * all fractals. The value is set directly in all fractals.
     *
     * If the parameter is non-exclusive, id is ignored.
     */
    public void setParameterValue(String key, int id, Object value) {
        boolean updated = false;

        if(exclusiveParameters.contains(key)) {
            updated = setIndividualParameterValue(key, id, value);
        } else {
            // for all ids
            for(Integer i : collection.ids()) {
                updated |= setIndividualParameterValue(key, i, value);
            }
        }

        if(updated) {
            fireParametersUpdated();
        }
    }

    // === Add/Remove Fractals ===

    /**
     * Adds a new fractal to the end of the list. If there
     * was no fractal before, then the key-index is set to 0.
     * @param fractalData The data of the new fractal
     * @return The id.
     */
    public int addFractal(FractalData fractalData) {
        Fractal fractal = Fractal.fromData(fractalData);
        int id = collection.add(fractal);

        fireParametersUpdated();

        return id;
    }

    /**
     * Removes the key fractal. The key index is set to the
     * fractal ahead if the last fractal was removed.
     * If this was the last fractal, then the key index is invalid (-1)
     */
    public void removeFractal(int id) {
        if(!collection.remove(id)) {
            throw new IllegalArgumentException();
        }
        
        fireParametersUpdated();
    }

    // === Getters ===

    public int fractalCount() {
        return collection.size();
    }

    public Fractal getFractal(int id) {
        return collection.get(id);
    }

    // === Handle listeners ===

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public boolean removeListener(Listener l) {
        return listeners.remove(l);
    }

    // === History ===

    // XXX history should be moved.

    public boolean historyForward(int id) {
        if(getFractal(id).historyForward()) {
            fireParametersUpdated();
            return true;
        }

        return false;
    }

    public boolean historyBack(int id) {
        if(getFractal(id).historyBack()) {
            fireParametersUpdated();
            return true;
        }

        return false;
    }

    // === Key Fractals ===

    /**
     * Sets the data of the current fractal.
     */
    public void setFractal(int id, FractalData data) {
        collection.get(id).setData(data, true, false);
        fireParametersUpdated();
    }

    // === Handle exclusive parameters ===

    public boolean isSharedParameter(String id) {
        return !exclusiveParameters.contains(id);
    }

    public void removeExclusiveParameter(String id) {
        if(exclusiveParameters.remove(id)) {
            fireParametersUpdated();
        }
    }

    public void addExclusiveParameter(String id) {
        if(exclusiveParameters.add(id)) {
            fireParametersUpdated();
        }
    }

    public Iterable<Integer> fractalIds() {
        return collection.ids();
    }

    public Iterable<String> exclusiveParameters() {
        return exclusiveParameters;
    }

    // === Internal data structures ===

    public interface Listener {
        /**
         * Called if parameters were modified.
         * This includes that there are new parameter values
         * or parameters were added. Fractals that are
         * owned by this provider are informed individually
         * via the FractalListener if the modified parameter
         * affects them. Use this for displays of all parameters.
         * To redraw a modified fractal, rather use FractalListener
         */
        void parameterMapUpdated(FractalProvider src);
    }
}
