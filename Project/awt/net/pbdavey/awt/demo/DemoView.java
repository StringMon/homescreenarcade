package net.pbdavey.awt.demo;


import net.pbdavey.awt.AwtView;
import net.pbdavey.awt.Font;
import net.pbdavey.awt.FontMetrics;
import net.pbdavey.awt.Graphics2D;
import net.pbdavey.awt.RenderingHints;
import and.awt.BasicStroke;
import and.awt.Color;
import and.awt.Dimension;
import and.awt.geom.Arc2D;
import and.awt.geom.Ellipse2D;
import and.awt.geom.GeneralPath;
import and.awt.geom.Line2D;
import and.awt.geom.Rectangle2D;
import and.awt.geom.RoundRectangle2D;
import android.content.Context;
import android.util.AttributeSet;

public class DemoView extends AwtView {
	public DemoView(Context context) {
		super(context);
	}

	public DemoView(Context context, AttributeSet attribSet) {
		super(context, attribSet);
	}
	
    final static int maxCharHeight = 15;
    final static int minFontSize = 6;

    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;

	final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f, 
                                                      BasicStroke.CAP_BUTT, 
                                                      BasicStroke.JOIN_MITER, 
                                                      10.0f, dash1, 0.0f);
    
    Dimension totalSize;
    FontMetrics fontMetrics;

    public void init() {
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
    }

	FontMetrics pickFont(Graphics2D g2,
            String longString,
            int xSpace) {
    	boolean fontFits = false;
    	Font font = g2.getFont();
    	FontMetrics fontMetrics = g2.getFontMetrics();
    	int size = font.getSize();
    	String name = font.getName();
    	int style = font.getStyle();

    	while ( !fontFits ) {
    		if ( (fontMetrics.getHeight() <= maxCharHeight)
    				&& (fontMetrics.stringWidth(longString) <= xSpace) ) {
    			fontFits = true;
    		}
    		else {
    			if ( size <= minFontSize ) {
    				fontFits = true;
    			}
    			else {
    				g2.setFont(font = new Font(name,
                                  	style,
                                  	--size));
    				fontMetrics = g2.getFontMetrics();
    			}
    		}
    	}

    	return fontMetrics;
    }

	public void paint(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Dimension d = getSize();
        int gridWidth = d.width / 6;
        int gridHeight = d.height / 2;

        fontMetrics = pickFont(g2, "Filled and Stroked GeneralPath",
                               gridWidth);

        Color fg3D = Color.lightGray;

        g2.setPaint(fg3D);
        g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
        g2.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
        g2.setPaint(fg);

        int x = 5;
        int y = 7;
        int rectWidth = gridWidth - 2*x;
        int stringY = gridHeight - 3 - fontMetrics.getDescent();
        int rectHeight = stringY - fontMetrics.getMaxAscent() - y - 2;

        // draw Line2D.Double
        g2.draw(new Line2D.Double(x, y+rectHeight-1, x + rectWidth, y));
        g2.drawString("Line2D", x, stringY);
        x += gridWidth;

        // draw Rectangle2D.Double
        g2.setStroke(stroke);
        g2.draw(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.drawString("Rectangle2D", x, stringY);
        x += gridWidth;      

        // draw  RoundRectangle2D.Double
        g2.setStroke(dashed);
        g2.draw(new RoundRectangle2D.Double(x, y, rectWidth, 
                                            rectHeight, 10, 10));
        g2.drawString("RoundRectangle2D", x, stringY);
        x += gridWidth;
        
        // draw Arc2D.Double       
        g2.setStroke(wideStroke);
        g2.draw(new Arc2D.Double(x, y, rectWidth, rectHeight, 90, 
                                 135, Arc2D.OPEN));
        g2.drawString("Arc2D", x, stringY);
        x += gridWidth;

        // draw Ellipse2D.Double
        g2.setStroke(stroke);
        g2.draw(new Ellipse2D.Double(x, y, rectWidth, rectHeight));
        g2.drawString("Ellipse2D", x, stringY);
        x += gridWidth;

        // draw GeneralPath (polygon)
        int x1Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y1Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                              x1Points.length);
        polygon.moveTo(x1Points[0], y1Points[0]);
        for ( int index = 1; index < x1Points.length; index++ ) {
            polygon.lineTo(x1Points[index], y1Points[index]);
        };
        polygon.closePath();

        g2.draw(polygon);
        g2.drawString("GeneralPath", x, stringY);

        // NEW ROW
        x = 5;
        y += gridHeight;
        stringY += gridHeight;

        // draw GeneralPath (polyline)

        int x2Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y2Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                               x2Points.length);
        polyline.moveTo (x2Points[0], y2Points[0]);
        for ( int index = 1; index < x2Points.length; index++ ) {
            polyline.lineTo(x2Points[index], y2Points[index]);
        };

        g2.draw(polyline);
        g2.drawString("GeneralPath (open)", x, stringY);
        x += gridWidth;

        // fill Rectangle2D.Double (red)
        g2.setPaint(red);
        g2.fill(new Rectangle2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Rectangle2D", x, stringY);
        x += gridWidth;        

        // fill RoundRectangle2D.Double
        // FIXME GradientPaint redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        // FIXME g2.setPaint(redtowhite);
        g2.fill(new RoundRectangle2D.Double(x, y, rectWidth, 
                                            rectHeight, 10, 10));
        g2.setPaint(fg);
        g2.drawString("Filled RoundRectangle2D", x, stringY);
        x += gridWidth;

        // fill Arc2D 
        g2.setPaint(red);
        g2.fill(new Arc2D.Double(x, y, rectWidth, rectHeight, 90, 
                                 135, Arc2D.OPEN));
        g2.setPaint(fg);
        g2.drawString("Filled Arc2D", x, stringY);
        x += gridWidth;

        // fill Ellipse2D.Double
        // FIXME redtowhite = new GradientPaint(x,y,red,x+rectWidth, y,white);
        // FIXME g2.setPaint(redtowhite);
        g2.fill (new Ellipse2D.Double(x, y, rectWidth, rectHeight));
        g2.setPaint(fg);
        g2.drawString("Filled Ellipse2D", x, stringY);
        x += gridWidth;



        // fill and stroke GeneralPath
        int x3Points[] = {x, x+rectWidth, x, x+rectWidth};
        int y3Points[] = {y, y+rectHeight, y+rectHeight, y};
        GeneralPath filledPolygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                                    x3Points.length);
        filledPolygon.moveTo(x3Points[0], y3Points[0]);
        for ( int index = 1; index < x3Points.length; index++ ) {
            filledPolygon.lineTo(x3Points[index], y3Points[index]);
        };
        filledPolygon.closePath();
        g2.setPaint(red);
        g2.fill(filledPolygon);
        g2.setPaint(fg);
        g2.draw(filledPolygon);
        g2.drawString("Filled and Stroked GeneralPath", x, stringY);
	}
}
