package at.searles.fractviewlib;

import at.searles.meelan.optree.inlined.ExternDeclaration;

import java.util.*;

/**
 * Complicated algorithm to create a list of parameter entries
 * in an intuitive order for all fractals, additionally filtering
 * out redundant parameters (double entries of shared parameters)
 * and effectively keeping order of all other fractals.
 *
 * To access concrete values, use FractalProvider.
 */
public class ParameterTable {

    private ParameterTable() {} // do not create instance.

    public static List<Entry> create(int selectedId, FractalCollection collection, Set<String> exclusiveParameterIds) {
        ArrayList<Entry> parameters = orderByExternDefinitions(selectedId, collection);
        groupParameters(exclusiveParameterIds, parameters);

        return parameters;
    }

    /**
     * Returns all required (=value is actually used and not just declared in
     * the source code) parameters that should be shown in order
     * of fractals and traversal.
     */
    private static ArrayList<Entry> requiredParameterEntries(ArrayList<Integer> order, FractalCollection collection) {
        ArrayList<Entry> parameters = new ArrayList<>();

        for(Integer id : order) {
            for(Fractal.Parameter p : collection.get(id).requiredParameters()) { // those that are actually in use; including source/scale
                Entry entry = new Entry(p.id, id);
                parameters.add(entry);
            }
        }

        return parameters;
    }

    private static LinkedHashSet<String> createExternsOrder(ArrayList<Integer> order, FractalCollection collection) {
        ArrayList<String> allExterns = new ArrayList<>();
        ArrayList<String> localExterns = new ArrayList<>();

        allExterns.add(Fractal.SOURCE_LABEL);
        allExterns.add(Fractal.SCALE_LABEL);

        for(Integer id : order) {
            Fractal fractal = collection.get(id);
            for(ExternDeclaration extern: fractal.externDeclarations()) {
                int pos = allExterns.indexOf(extern.id);

                if(pos != -1) {
                    // add before extern.
                    allExterns.addAll(pos, localExterns);
                    localExterns.clear();
                } else {
                    // does not exist yet.
                    localExterns.add(extern.id);
                }
            }

            allExterns.addAll(localExterns);
            localExterns.clear();
        }

        return new LinkedHashSet<>(allExterns);
    }

    private static ArrayList<Entry> orderByExternDefinitions(int selectedId, FractalCollection collection) {
        // Step 1: Build up externs-order
        ArrayList<Integer> order = new ArrayList<>(collection.ids());

        if(order.remove((Integer) selectedId)) {
            order.add(0, selectedId);
        }

        ArrayList<Entry> parameters = requiredParameterEntries(order, collection);

        Set<String> externsOrder = createExternsOrder(order, collection);

        int pos = 0;

        for(String key : externsOrder) {
            for(Integer id : order) {
                int startRange = pos; // things ahead were already sorted.

                // find parameter
                while(startRange < parameters.size()) {
                    Entry p = parameters.get(startRange);
                    if(p.key.equals(key) && id.equals(p.id)) {
                        break;
                    }

                    startRange++;
                }

                if (startRange >= parameters.size()) {
                    // not found.
                    continue;
                }

                // yes found. Find all dependants.

                int endRange = startRange + 1;

                while(endRange < parameters.size() &&
                        !externsOrder.contains(parameters.get(endRange).key)) {
                    endRange++;
                }

                moveRangeTo(startRange, endRange, pos, parameters);
                pos += endRange - startRange;
            }
        }
        return parameters;
    }

    /**
     * Moves entries in visibleParameters from startRange/endRange(excl)
     * to pos.
     * eg [a,b,c,d,e].moveRangeTo(2,4,1) results in
     * [a,c,d,b,e]. pos is assumed to be ahead of startRange.
     */
    private static <A> void moveRangeTo(int startRange, int endRange, int pos, List<A> list) {
        if(startRange == pos) {
            // nothing to do.
            return;
        }

        // pos is always ahead of startRange
        reverse(pos, endRange, list);
        reverse(pos, pos + endRange - startRange, list);
        reverse(pos + endRange - startRange, endRange, list);
        // this is just beautiful...
    }

    /**
     * Reverses range start (incl) to end (excl) in visibleParameters
     */
    private static <A> void reverse(int start, int end, List<A> list) {
        for(int l = start, r = end - 1; l < r; ++l, r--) {
            A tmp = list.get(l);
            list.set(l, list.get(r));
            list.set(r, tmp);
        }
    }

    /**
     * Adds the first entry for shared parameters.
     */
    private static void groupParameters(Set<String> exclusiveParameterIds, ArrayList<Entry> parameters) {
        // use only one entry for shared parameters
        Set<String> markSharedParameters = new HashSet<>();

        for (int i = 0; i < parameters.size(); ++i) {
            String key = parameters.get(i).key;
            if (markSharedParameters.contains(key)) {
                // XXX should I check the parameter type?
                parameters.remove(i);
                i--;
            } else if (!exclusiveParameterIds.contains(key)) {
                markSharedParameters.add(key);
            }
        }
    }

    public static class Entry {
        public final String key;
        public final int id;

        Entry(String key, int id) {
            this.key = key;
            this.id = id;
        }
    }
}