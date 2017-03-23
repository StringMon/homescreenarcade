package net.pbdavey.awt;

import and.awt.Color;
import and.awt.Dimension;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


public abstract class AwtView extends View {
	private Color color;

	public AwtView(Context context) {
		super(context);
		init();
	}

	public AwtView(Context context, AttributeSet attribSet) {
		super(context, attribSet);
		init();
	}

	public void setBackground(Color bgColor) {
		this.setBackgroundColor(bgColor.getRGB());
	}

	public void setForeground(Color bgColor) {
		this.color = bgColor;
	}

	public Dimension getSize() {
		return new Dimension(this.getWidth(), this.getHeight());
	}
	
	public void init() {
		
	}
	
	@Override
	protected final void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Graphics2D g2 = new Graphics2D(canvas);
		if (color != null)
			g2.setColor(color);
		paint(g2);
	}
	
	public abstract void paint(Graphics2D g2); 
}
