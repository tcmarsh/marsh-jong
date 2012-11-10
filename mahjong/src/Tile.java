
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

abstract public class Tile extends JPanel implements Comparable<Tile> {
	private static final long serialVersionUID = 2078619916134468904L;

	protected static final int WIDTH = 55;
    protected static final int HEIGHT = 70;
    protected static final int OFFSET = 10;
    protected static final int FACE_WIDTH = WIDTH - OFFSET;
    protected static final int FACE_HEIGHT = HEIGHT - OFFSET;
    protected boolean drawRound = true;
    
    protected Integer xPos = null;
    protected Integer yPos = null;
    protected Integer zPos = null;
	
	protected final Color BLUE = Color.BLUE;
	protected final Color BLACK = Color.BLACK;
	protected final Color GREEN = new Color(0, 150, 0);
	protected final Color RED = Color.RED;
	protected final Color WHITE = Color.WHITE;
	protected final Color IVORY = new Color(245, 245, 214);
	protected final Color LIGHT_GRAY = Color.LIGHT_GRAY;
	protected final Color LILAC = new Color(255, 225, 255);
	protected final Color HIGHLIGHT = LILAC;
	protected final Color HINT = GREEN;
	
	private boolean isSelected = false;
	private boolean isHint = false;
	
    public boolean matches(Tile otherTile) {
        return otherTile != null && otherTile.getClass() == this.getClass();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

		setSize(WIDTH + 11, HEIGHT + 6);
		setOpaque(false);
		setPreferredSize(new Dimension(61, 71));
		
		if (drawRound) {
			paintRound(g);
		}
		else {
			paintSquare(g);
		}
	}
    
    protected void highlight(boolean isSelected) {
    	this.isSelected = isSelected;
    	revalidate();
    }
    protected void hint(boolean isEnabled) {
    	this.isHint = isEnabled;
    	revalidate();
    }

    protected void place(int parentWidth, int parentHeight) {
    	if (xPos == null || yPos == null || zPos == null) {
    		setLocation(parentWidth / 2 - WIDTH / 2, parentHeight / 2 - HEIGHT / 2);
    		return;
    	}
    	
    	int baseX = parentWidth / 2 - WIDTH / 2;
    	int baseY = parentHeight / 2 - HEIGHT / 2;
    	int offsetX = (4 - zPos) * OFFSET;
    	int offsetY = offsetX * -1;
    	int tileOffsetX = WIDTH * xPos - (WIDTH / 2 + 1) * (xPos != 0 ? xPos / Math.abs(xPos) : xPos);
    	int tileOffsetY = (HEIGHT - 5) * yPos * -1 + (HEIGHT / 2 - 3) * (yPos != 0 ? yPos / Math.abs(yPos) : yPos);
    	int xLoc = baseX + offsetX + tileOffsetX;
    	int yLoc = baseY + offsetY + tileOffsetY;
    	
    	setLocation(xLoc, yLoc);
    }
    
    @Override
    public int compareTo(Tile otherTile) {
    	if (this == otherTile) {
    		return 0;
    	}
    	
    	// If any of the positions is null, we can't get a reliable comparison,
    	// so we just ignore it and don't make any changes. equals does the
    	// opposite in this case, so it doesn't *exactly* follow the code
    	// convention, but this makes much more sense in this case
    	if (	xPos == null || otherTile.xPos == null) { 
    		return 0;
    	}
    	else if (yPos == null || otherTile.yPos == null) {
    		return 0;
    	}
    	else if (zPos == null || otherTile.zPos == null) {
    		return 0;
    	}
    	
    	// If one tile has a higher zPos than another, it is by definition
    	// greater than that other
    	int currentComparison = zPos.compareTo(otherTile.zPos);
    	if (currentComparison != 0) {
    		return currentComparison;
    	}
    	
    	// On rows where the yPos is equal OR on the 1, -1 and 0 rows together,
    	// we compare the xPos
    	if (yPos.equals(otherTile.yPos) ||
    			(Math.abs(yPos) == 1 && otherTile.yPos == 0) ||
    			(Math.abs(otherTile.yPos) == 1 && yPos == 0)) {
    		currentComparison = xPos.compareTo(otherTile.xPos); 
    	}
    	else {
    		currentComparison = yPos.compareTo(otherTile.yPos);
    	}
    	
    	return currentComparison;
    }
    
    @Override
    public boolean equals(Object otherObject) {
    	if (this == otherObject) {
    		return true;
    	}
    	if (otherObject == null || !getClass().equals(otherObject.getClass())) {
    		return false;
    	}
    	Tile otherTile = (Tile) otherObject;
    	if (xPos == null || otherTile.xPos == null) {
    		return false;
    	}
    	else if (yPos == null || otherTile.yPos == null) {
    		return false;
    	}
    	else if (zPos == null || otherTile.zPos == null) {
    		return false;
    	}
    	
    	return xPos.equals(otherTile.xPos) && yPos.equals(otherTile.yPos) &&
    			zPos.equals(otherTile.zPos);
    }
    
    @Override
    public int hashCode() {
    	int hash = 1;
    	if (xPos == null || yPos == null || zPos == null) {
    		return hash;
    	}
    	hash = hash * 11 + xPos.hashCode();
    	hash = hash * 13 + yPos.hashCode();
    	hash = hash * 17 + zPos.hashCode();
    	
    	return hash;
    }
    
	private void paintSquare(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint paint = new GradientPaint(0, HEIGHT, IVORY, 10, 0, LILAC);
		
		g2.setPaint(paint);
		g2.fillPolygon(new int[] {0, 5, 5, 0}, new int[] {10, 5, HEIGHT, HEIGHT + 5}, 4);
		g2.setColor(LILAC);
		g2.fillPolygon(new int[] {0, 5, WIDTH + 5, WIDTH}, 
				new int[] {HEIGHT + 5, HEIGHT, HEIGHT, HEIGHT + 5}, 4);
		
		g2.setColor(LIGHT_GRAY);
		g2.drawPolygon(new int[] {0, 5, 5, 0}, new int[] {10, 5, HEIGHT, HEIGHT + 5}, 4);
		g2.drawPolygon(new int[] {0, 5, WIDTH + 5, WIDTH}, 
				new int[] {HEIGHT + 5, HEIGHT, HEIGHT, HEIGHT + 5}, 4);
		
		paint = new GradientPaint(0, HEIGHT, WHITE, 10, 0, IVORY);
		
		g2.setPaint(paint);
		g2.fillPolygon(new int[] {5, 10, 10, 5}, new int[] {5, 0, HEIGHT - 5, HEIGHT}, 4);
		g2.setColor(IVORY);
		g2.fillPolygon(new int[] {5, 10, WIDTH + 10, WIDTH + 5}, 
				new int[] {HEIGHT, HEIGHT - 5, HEIGHT - 5, HEIGHT}, 4);
		
		g2.setColor(LIGHT_GRAY);
		g2.drawPolygon(new int[] {5, 10, 10, 5}, new int[] {5, 0, HEIGHT - 5, HEIGHT}, 4);
		g2.drawPolygon(new int[] {5, 10, WIDTH + 10, WIDTH + 5}, 
				new int[] {HEIGHT, HEIGHT - 5, HEIGHT - 5, HEIGHT}, 4);
		
		paint = new GradientPaint(WIDTH, 0, WHITE, 10, HEIGHT, isSelected ? HIGHLIGHT : isHint ? HINT : IVORY);
		g2.setPaint(paint);
		
		g2.fillRect(10, 0, WIDTH, HEIGHT - 5);
		g2.setColor(LIGHT_GRAY);
		g2.drawRect(10, 0, WIDTH, HEIGHT - 5);
	}
	
	private void paintRound(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint paint = new GradientPaint(0, HEIGHT, WHITE, 5, 5, LILAC);
        
        g2.setPaint(paint);
		g2.fillPolygon(new int[] {0, 5, 5, 0}, new int[] {15, 10, HEIGHT - 5, HEIGHT}, 4);
		paint = new GradientPaint(5, HEIGHT - 5, WHITE, 5, HEIGHT + 5, LILAC);
		g2.setPaint(paint);
		g2.fillPolygon(new int[] {1, 4, 9, 4}, new int[] {HEIGHT, HEIGHT - 4, HEIGHT, HEIGHT + 4}, 4);
		g2.setColor(LILAC);
		g2.fillPolygon(new int[] {5, 10, WIDTH, WIDTH - 5}, new int[] {HEIGHT + 5, HEIGHT, HEIGHT, HEIGHT + 5}, 4);
        
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(0, 10, WIDTH, HEIGHT - 5, 15, 15);

		paint = new GradientPaint(5, HEIGHT - 5, WHITE, 10, 0, IVORY);
		g2.setPaint(paint);
		g2.fillPolygon(new int[] {5, 10, 10, 5}, new int[] {10, 5, HEIGHT - 10, HEIGHT - 5}, 4);
		paint = new GradientPaint(10, HEIGHT - 10, WHITE, 10, HEIGHT, IVORY);
		g2.setPaint(paint);
		g2.fillPolygon(new int[] {6, 9, 14, 9}, new int[] {HEIGHT - 5, HEIGHT - 9, HEIGHT - 5, HEIGHT - 1}, 4);
		g2.setColor(IVORY);
		g2.fillPolygon(new int[] {10, 15, WIDTH + 5, WIDTH}, new int[] {HEIGHT, HEIGHT - 5, HEIGHT - 5, HEIGHT}, 4);
		
		g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(5, 5, WIDTH, HEIGHT - 5, 15, 15);

        paint = new GradientPaint(WIDTH, 0, WHITE, 10, HEIGHT, isSelected ? HIGHLIGHT : isHint ? HINT : IVORY);
        g2.setPaint(paint);
        
        g2.fillRoundRect(11, 1, WIDTH - 1, HEIGHT - 6, 16, 16);

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRoundRect(10, 0, WIDTH, HEIGHT - 5, 15, 15);
    }
}
