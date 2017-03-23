package net.pbdavey.awt;

import android.graphics.Paint;

public class FontMetrics {
	Paint paint;
	
	public FontMetrics(Paint paint) {
		this.paint = paint;
	}
	
	public int getHeight() {
		return paint.getFontMetricsInt().ascent + 
			paint.getFontMetricsInt().descent + 
			paint.getFontMetricsInt().leading;
	}

	public int stringWidth(String longString) {
		return (int) paint.measureText(longString);
	}

	public int getDescent() {
		return this.paint.getFontMetricsInt().descent;
	}

	public int getMaxAscent() {
		return this.paint.getFontMetricsInt().ascent;
	}

}
