import javax.swing.JFrame;


public class MahjongBoard extends JFrame {
	private static final long serialVersionUID = -8006300036685327355L;

	private static final int WIDTH = 1100;
	private static final int HEIGHT = 800;
	
	public MahjongBoard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(WIDTH, HEIGHT);
		
		GamePanel gamePanel = new GamePanel(WIDTH, HEIGHT, true);
		add(gamePanel);
		
		setVisible(true);
	}
	public static void main(String[] args) {
		new MahjongBoard();
	}

}
