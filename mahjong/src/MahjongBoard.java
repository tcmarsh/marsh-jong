import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


public class MahjongBoard extends JFrame {
	private static final long serialVersionUID = -8006300036685327355L;

	private static final int WIDTH = 1100;
	private static final int HEIGHT = 800;
	private GamePanel gamePanel;
	
	public MahjongBoard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(WIDTH, HEIGHT);
		
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("New Game");
		item.setMnemonic(KeyEvent.VK_N);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newGame();
			}
		});
		menu.add(item);
		menu.setMnemonic(KeyEvent.VK_F);
		
		menubar.add(menu);
		setJMenuBar(menubar);
		
		gamePanel = new GamePanel(WIDTH - 10, HEIGHT - 10, true);
		add(gamePanel);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int selection =
						JOptionPane.showConfirmDialog(e.getComponent(),
						"Do you want to quit the game?",
						"Exit Game",
						JOptionPane.YES_NO_OPTION);
				if (selection == 0) {
					System.exit(0);
				}
			}
		});
		setVisible(true);
	}
	
	public void checkEndGame() {
		String[] options = {"New Game", "Cancel", "Exit Game"};
		int selection = JOptionPane.showOptionDialog(this, "No more moves exist.\n" +
				"Choose Cancel to go back to this game, otherwise " +
				"choose New Game or Exit Game.", "End Game", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		
		switch (selection) {
		case 0:
			newGame();
			
			break;
		case 2:
			close();
			break;
		case 1:
			// Intentional fall-through
		default:
			break;
		}
	}

	private void close() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}
	private void newGame() {
		remove(gamePanel);
		
		gamePanel = new GamePanel(WIDTH - 10, HEIGHT - 10, true);
		add(gamePanel);
		
		repaint();
	}
	
	public static void main(String[] args) {
		new MahjongBoard();
	}

}
