package net.pbdavey.awt;


import and.awt.Rectangle;
import net.pbdavey.awt.RenderingHints.Key;
import and.awt.Graphics;
import and.awt.BasicStroke;
import and.awt.Color;
import and.awt.Shape;
import and.awt.Stroke;
import and.awt.geom.AffineTransform;
import and.awt.geom.PathIterator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
/**
 * So far it appears that Graphics2D is roughly equivalent to a Canvas with Paint.
 * The Paint object contains information regarding Fonts and FontMetrics, while
 * the Canvas is a more raw drawing tool.
 * @author pbdavey
 *
 */
public class Graphics2D extends Graphics {
	Paint paint;
	Canvas canvas;
	
	Font font = new Font();
	and.awt.Paint awtPaint;
	Stroke stroke;

	Color color = Color.white;
	Color bgColor;
	AffineTransform transform;
	
	public Graphics2D(Canvas canvas) {
		this.canvas = canvas;
		this.transform = new AffineTransform();
		this.paint = new Paint();
	}
	
	public void setPaint(and.awt.Paint p) {
		this.awtPaint = p;
	}
	
	public and.awt.Paint getPaint() {
		return awtPaint;
	}

	public void setColor(Color color) {
		this.color = color;
		paint.setColor(this.color.getRGB());
	}
	
	public Color getColor() {
		return this.color;
	}

	public void setBackground(Color color) {
		this.bgColor = color;
	}
	
	public Color getBackground() {
		return this.bgColor;
	}
	
	/**
	 * TODO: This shouldn't accept BasicStroke, rather a generic Stroke
	 * The issue here has to do with actually stroking the Shape, which
	 * is delegated to a rendering pipe.  See
	 * {@link and.awt.BasicStroke#createStrokedShape}
	 * @param stroke
	 */
	public void setStroke(Stroke pStroke) {
		this.stroke = pStroke;
		BasicStroke stroke = (BasicStroke) pStroke;
		
		Paint.Cap cap = Paint.Cap.BUTT;
		switch(stroke.getEndCap()) {
		case BasicStroke.CAP_BUTT:
			cap = Paint.Cap.BUTT;
			break;
		case BasicStroke.CAP_ROUND:
			cap = Paint.Cap.ROUND;
			break;
		case BasicStroke.CAP_SQUARE:
			cap = Paint.Cap.SQUARE;
			break;
		}
		this.paint.setStrokeCap(cap);

		Paint.Join join = Paint.Join.BEVEL;
		switch(stroke.getLineJoin()) {
		case BasicStroke.JOIN_BEVEL:
			join = Paint.Join.BEVEL;
			break;
		case BasicStroke.JOIN_MITER:
			join = Paint.Join.MITER;
			break;
		case BasicStroke.JOIN_ROUND:
			join = Paint.Join.ROUND;
			break;
		}
		this.paint.setStrokeJoin(join);
		
		this.paint.setStrokeMiter(stroke.getMiterLimit());
		this.paint.setStrokeWidth(stroke.getLineWidth());
	}
	
	public Stroke getStroke() {
		return this.stroke;
	}

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		and.awt.Paint p = getPaint();
		Color c = getColor();
		Color brighter = c.brighter();
		Color darker = c.darker();

		if (!raised) {
			setColor(darker);
		} else if (p != c) {
			setColor(c);
		}
		fillRect(x + 1, y + 1, width - 2, height - 2);
		setColor(raised ? brighter : darker);
		// drawLine(x, y, x, y + height - 1);
		fillRect(x, y, 1, height);
		// drawLine(x + 1, y, x + width - 2, y);
		fillRect(x + 1, y, width - 2, 1);
		setColor(raised ? darker : brighter);
		// drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1);
		fillRect(x + 1, y + height - 1, width - 1, 1);
		// drawLine(x + width - 1, y, x + width - 1, y + height - 2);
		fillRect(x + width - 1, y, 1, height - 1);
		setPaint(p);
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		and.awt.Paint p = getPaint();
		Color c = getColor();
		Color brighter = c.brighter();
		Color darker = c.darker();

		setColor(raised ? brighter : darker);
		// drawLine(x, y, x, y + height);
		fillRect(x, y, 1, height + 1);
		// drawLine(x + 1, y, x + width - 1, y);
		fillRect(x + 1, y, width - 1, 1);
		setColor(raised ? darker : brighter);
		// drawLine(x + 1, y + height, x + width, y + height);
		fillRect(x + 1, y + height, width, 1);
		// drawLine(x + width, y, x + width, y + height - 1);
		fillRect(x + width, y, 1, height);
		setPaint(p);
	}

	public void setRenderingHint(Key renderingHintKey,
			RenderingHints renderingHint) {
		if (renderingHintKey == RenderingHints.KEY_ANTIALIASING) {
			if (renderingHint == RenderingHints.VALUE_ANTIALIAS_ON)
				paint.setAntiAlias(true);
			else
				paint.setAntiAlias(false);
		}
		else {
			throw new UnsupportedOperationException("Graphics2D only supports a limited set of rendering hints");
		}
	}

	public Font getFont() {
		return this.font;
	}
	
	public FontMetrics getFontMetrics() {
		return new FontMetrics(this.paint);
	}

	public void setFont(Font font) {
		paint.setTypeface(font.typeFace);
		this.font = font;
	}

	public void drawString(String string, int x, int y) {
		paint.setStyle(Style.STROKE);
		canvas.drawText(string, x, y, paint);
	}

	public void drawString(String string, float x, float y) {
		paint.setStyle(Style.STROKE);
		canvas.drawText(string, x, y, paint);
	}
	
	public boolean hit(Rectangle rect,
            Shape s,
            boolean onStroke) {
		return true;
	}
	
	
	
	public void draw(Shape s) {
		PathIterator pi = s.getPathIterator(null);
		Path path = convertAwtPathToAndroid(pi);
		// Draw the outline, don't fill
		paint.setStyle(Style.STROKE);
		canvas.drawPath(path, paint);
	}
	
	public void fill(Shape s) {
		PathIterator pi = s.getPathIterator(null);
		Path path = convertAwtPathToAndroid(pi);
		// Draw the outline and fill
		paint.setStyle(Style.FILL_AND_STROKE);
		canvas.drawPath(path, paint);
	}

	private Path convertAwtPathToAndroid(PathIterator pi) {
		Path path = new Path();
		float [] coords = new float [6];
		while (!pi.isDone()) {
			int windingRule = pi.getWindingRule();
			
			if (windingRule == PathIterator.WIND_EVEN_ODD) {
				path.setFillType(Path.FillType.EVEN_ODD);
			}
			else {
				path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
			}
			
			int pathType = pi.currentSegment(coords);

			switch (pathType) {
			case PathIterator.SEG_CLOSE:
				path.close();
				break;
			case PathIterator.SEG_CUBICTO:
				path.cubicTo(coords [0], coords [1], coords [2], coords [3], coords [4], coords [5]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo(coords [0], coords [1]);
				break;
			case PathIterator.SEG_MOVETO:
				path.moveTo(coords [0], coords [1]);
				break;
			case PathIterator.SEG_QUADTO:
				path.quadTo(coords [0], coords [1], coords [2], coords [3]);
				break;
			}
			
			pi.next();
		}		
		return path;
	}

	public void translate(int x, int y) {
		this.transform.translate(x, y);
	}
	
	public void translate(double tx, double ty) {
		this.transform.translate(tx, ty);
	}
	
	public void rotate(double theta) {
		this.transform.rotate(theta);
	}
	
	public void rotate(double theta, double x, double y) {
		this.transform.rotate(theta, x, y);
	}
	
	public void scale(double sx, double sy) {
		this.transform.scale(sx, sy);
	}
	
	public void shear(double shx, double shy) {
		this.transform.shear(shx, shy);
	}
	
	public void transform(AffineTransform Tx) {
		//TODO: AffineTransform doesn't have a similar call, so leave for now
	}
	
	public void setTransform(AffineTransform Tx) {
		this.transform = Tx;
	}
	
	public AffineTransform getTransform() {
		return this.transform;
	}
	
	@Override
	public void clearRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Graphics create() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		// ?
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		paint.setStyle(Style.STROKE);
		this.canvas.drawArc(new RectF(x, y, x+width, y+height), startAngle, arcAngle, true, paint);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		paint.setStyle(Style.STROKE);
		this.canvas.drawLine(x1, y1, x2, y2, paint);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		paint.setStyle(Style.STROKE);
		this.canvas.drawOval(new RectF(x, y, x+width, y+height), paint);
	}

	@Override
	public void drawPolygon(int[] pointsX, int[] pointsY, int nPoints) {
		paint.setStyle(Style.STROKE);
		for (int i = 0; i < nPoints; i++) {
			drawLine(pointsX[i],pointsY[i],pointsX[i+1],pointsY[i+1]);
		}
		drawLine(pointsX[0],pointsY[0],pointsX[nPoints-1],pointsY[nPoints-1]);
	}

	@Override
	public void drawPolyline(int[] pointsX, int[] pointsY, int nPoints) {
		paint.setStyle(Style.STROKE);
		for (int i = 0; i < nPoints; i++) {
			drawLine(pointsX[i],pointsY[i],pointsX[i+1],pointsY[i+1]);
		}
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		paint.setStyle(Style.STROKE);
		this.canvas.drawRoundRect(new RectF(x, y, x+width, y+height), arcWidth, arcHeight, paint);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		paint.setStyle(Style.FILL);
		this.canvas.drawArc(new RectF(x, y, x+width, y+height), startAngle, arcAngle, true, paint);		
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		paint.setStyle(Style.FILL);
		this.canvas.drawOval(new RectF(x, y, x+width, y+height), paint);		
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		// TODO Auto-generated method stub
		// maybe create a Path-based Poly?
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		paint.setStyle(Style.FILL);
		this.canvas.drawRect(new RectF(x, y, x+width, y+height), paint);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		paint.setStyle(Style.FILL);
		this.canvas.drawRoundRect(new RectF(x, y, x+width, y+height), arcWidth, arcHeight, paint);
	}

	@Override
	public Shape getClip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getClipBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClip(Shape clip) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaintMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setXORMode(Color c1) {
		// TODO Auto-generated method stub
		
	}
}
