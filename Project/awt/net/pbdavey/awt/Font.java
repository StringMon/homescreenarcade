package net.pbdavey.awt;

import android.graphics.Typeface;

public class Font {
	Typeface typeFace;
	int size;
	
	public Font(String name, int style, int i) {
		// TODO - convert Font style to Typeface style
		typeFace = Typeface.create(name, style);
		size = i;
	}

	public Font() {
		typeFace = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
		size = 8;
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return typeFace.toString();
	}

	public int getStyle() {
		// TODO - convert Typeface style to Font style
		return typeFace.getStyle();
	}

	public Typeface getTypeFace() {
		return this.typeFace;
	}
}
