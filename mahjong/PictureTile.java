


import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

abstract public class PictureTile extends Tile{
	private static final long serialVersionUID = 221638277442731505L;
	
	private String name;
    
    public PictureTile(String name) {
        this.name = name;
		
		setToolTipText(toString());
    }
 
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		ImageIcon image = new ImageIcon(getClass().getResource("images/" + name + ".png"));
		image = new ImageIcon(
				image.getImage().getScaledInstance(
						(int) (image.getIconWidth() * .8), 
						(int) (image.getIconHeight() * .8), 
						Image.SCALE_DEFAULT));
		
		int x = (WIDTH - image.getIconWidth()) / 2 + 10;
		int y = (HEIGHT - image.getIconHeight()) / 2;
		g.drawImage(image.getImage(), x, y, this);
	}
	
	@Override
    public String toString() {
        String[] validNames = {"Chrysanthemum", "Orchid", "Plum", "Bamboo", "Spring",
            "Summer", "Fall", "Winter"};
    
        int index = -1;
        for (int i = 0; i < validNames.length; i++) {
            if (validNames[i].equals(name)) {
                index = i;
                break;
            }
        }
        
        if (index < 0) {
            return "Invalid Picture Tile";
        }
        return name;
    }
}
