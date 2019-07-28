package at.searles.fractviewlib.entries;

import at.searles.fractviewlib.data.FractalData;

import java.util.LinkedHashMap;

public class FavoriteEntry {

	// XXX no title here because this is part of a map.

	public final FractalData fractal;
	public final byte[] icon; // optional
	public final String description;  // optional

	public FavoriteEntry(byte[] icon, FractalData fractal, String description) {
		this.fractal = fractal;
		this.icon = icon;
		this.description = description;
	}

    public static class Collection extends LinkedHashMap<String, FavoriteEntry> {}
}
