import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;


public class MahjongBoard extends JFrame {
	private static final long serialVersionUID = -8006300036685327355L;

	private static final int BOARD_WIDTH = 1100;
	private static final int BOARD_HEIGHT = 800;
	private GamePanel gamePanel;

	// File has New game, then a separator before these items
	private final int UNDO_INDEX = 4;
	private final int REDO_INDEX = UNDO_INDEX + 1;
	private final int HINT_INDEX = REDO_INDEX + 1;
	// Save menu item is after a separator
	private final int SAVE_INDEX = HINT_INDEX + 2;

	private final String title = "Marsh-jong: \t";
	private final String operationString = "This help text describes the operation of the MahJong game. \n" +
			"FILE MENU:\n" +
			"\tNew Game: Starts a new game\n" +
			"\tNew Numbered Game: Starts a specific game with the number given by the user - the number for any given game can be found in the title bar while the game is in play.\n" +
			"\tRestart: Restarts the current game without retaining any memory of redo or undo operations.\n" +
			"\tUndo/Redo: Performs one undo or redo operation, if available.\n" +
			"\tSave Game: Not yet implemented.\n" +
			"\tExit Game: Closes the game and exits.\n\n" +
			"OPTIONS MENU:\n" +
			"\tRounded Corners: Sets whether the tiles will be square or have rounded corners.\n" +
			"\tSound: Sets the sound on or off.\n" +
			"\tHigh Scores: Displays the high scores.\n" +
			"\tRemoved Tiles: Displays the window with the tiles that have been removed. This menu is disabled while the panel is visible. Close it to re-enable.\n\n";
	private final String rulesString = "This help text describes the rules of MahJong.\n\n" +
			"Click on any tile to highlight it. When one tile is highlighted, clicking" +
			"on another tile will check to see if they are a match. If the tiles are a" +
			"match, the tiles will be removed. The following are the types of tiles:\n\n" +
			"\tCircle tiles - match the same number of circles on a tile\n" +
			"\tBamboo tiles - match the same number of bamboo on a tile, the one bamboo tile has a picture of a bird\n" +
			"\tCharacter tiles - Chinese number symbols, match the same number on tiles to remove\n" +
			"\tFlower tiles - pictures of flowers, match any of them to remove\n" +
			"\tSeason tiles - match any of butterfly, sun, snowflake and leaf to remove\n\n" +
			"After any tile has been removed, it will show in the removed panel if you" +
			"choose to display it. Undo and Redo operations are available and will either" +
			"replace or re-remove a tile from the board. The hint button will highlight" +
			"a possible choice, but the use of the hint will disqualify the current game" +
			"for the purpose of high scores. If you remove all tiles, you get a surprise!\n\n" +
			"GOOD LUCK!";

	private JFrame removedFrame = null;
	private JMenuItem removedTilesItem = null;
	private JFrame helpFrame = new JFrame();

	public MahjongBoard() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(BOARD_WIDTH, BOARD_HEIGHT);
		setLayout(new BorderLayout());

		gamePanel = new GamePanel(getContentPane().getWidth(),
				getContentPane().getHeight(), true);

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("New Game");
		item.setMnemonic(KeyEvent.VK_N);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		item.setToolTipText("Begins a new, random game.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(
						(JMenuItem) e.getSource(),
						"Abandon current game and start a new one?",
						"New Game",
						JOptionPane.YES_NO_OPTION
						);
				if (selection == 0) {
						newGame();
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("New Numbered Game");
		item.setMnemonic(KeyEvent.VK_G);
		item.setToolTipText("Begins a new game given a game number.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String number = JOptionPane.showInputDialog(
						"Enter the game number");
				if (Helper.isInteger(number) && number.length() == 6) {
					newGame(Long.parseLong(number));
				}
				else if (number != null) {
					JOptionPane.showMessageDialog((JMenuItem) e.getSource(),
							"Invalid input, must be a six-digit number",
							"Bad Game Number",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("Restart");
		item.setToolTipText("Restarts the current game and clears the undo/redo actions.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(
						(JMenuItem) e.getSource(),
						"Abandon current game and restart?",
						"New Game",
						JOptionPane.YES_NO_OPTION
						);
				if (selection == 0) {
					newGame(gamePanel.gameNumber);
				}
			}
		});
		menu.add(item);

		menu.addSeparator();

		item = new JMenuItem("Undo");
		item.setMnemonic(KeyEvent.VK_U);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.undo();
			}
		});
		item.setEnabled(gamePanel.canUndo());
		menu.add(item, UNDO_INDEX);

		item = new JMenuItem("Redo");
		item.setMnemonic(KeyEvent.VK_R);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.redo();
			}
		});
		item.setEnabled(gamePanel.canRedo());
		menu.add(item, REDO_INDEX);

		item = new JMenuItem("Hint");
		item.setMnemonic(KeyEvent.VK_H);
		item.setToolTipText("Highlights a possible move.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gamePanel.allowHint(((JMenuItem) e.getSource()).getTopLevelAncestor())) {
					gamePanel.hint(true);
				}
			}
		});
		menu.add(item, HINT_INDEX);

		menu.addSeparator();

		item = new JMenuItem("Save Game");
		item.setMnemonic(KeyEvent.VK_S);
		item.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		item.setToolTipText("Not implemented - keep track of the game number to replay this game for now.");
		item.setEnabled(false);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		item.setEnabled(false);
		menu.add(item, SAVE_INDEX);

		item = new JMenuItem("Exit Game");
		item.setToolTipText("Exits the game. To continue from the current game state, use Save Game first.");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		menu.add(item);

		menu.setMnemonic(KeyEvent.VK_F);

		menubar.add(menu);

		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);

		item = new JCheckBoxMenuItem("Rounded Corners");
		item.setMnemonic(KeyEvent.VK_R);
		((JCheckBoxMenuItem) item).setSelected(true);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePanel.toggleRoundedCorners();
			}
		});
		menu.add(item);

		item = new JCheckBoxMenuItem("Sound");
		((JCheckBoxMenuItem) item).setSelected(true);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!(e.getSource() instanceof JCheckBoxMenuItem)) {
					return;
				}

				JCheckBoxMenuItem checkbox = (JCheckBoxMenuItem) e.getSource();
				gamePanel.setSound(checkbox.isSelected());
			}
		});
		menu.add(item);

		menu.addSeparator();

		item = new JMenuItem("High Scores");
		item.setEnabled(false);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		menu.add(item);

		removedTilesItem = new JMenuItem("Removed Tiles");
		removedTilesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (removedFrame == null) {
					initRemovedFrame();
				}
				removedFrame.setVisible(true);
				removedTilesItem.setEnabled(false);
			}
		});
		menu.add(removedTilesItem);

		menubar.add(menu);
		setJMenuBar(menubar);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);

		item = new JMenuItem("Operation");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				JTextArea text = new JTextArea();
				JScrollPane scrollPane = new JScrollPane(text);
				text.setText(operationString);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setTabSize(2);
				scrollPane.setPreferredSize(new Dimension(550, 350));
				panel.add(scrollPane);
				helpFrame.setTitle("Operation Instructions");
				helpFrame.add(panel);
				helpFrame.setVisible(true);
				helpFrame.setSize(600, 400);
			}
		});
		menu.add(item);
		item = new JMenuItem("Game Rules");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel();
				JTextArea text = new JTextArea();
				JScrollPane scrollPane = new JScrollPane(text);
				text.setText(rulesString);
				text.setLineWrap(true);
				text.setWrapStyleWord(true);
				text.setTabSize(2);
				scrollPane.setPreferredSize(new Dimension(550, 350));
				panel.add(scrollPane);
				helpFrame.setTitle("Game Rules");
				helpFrame.add(panel);
				helpFrame.setVisible(true);
				helpFrame.setSize(600, 400);
			}
		});
		menu.add(item);

		menubar.add(menu);

		helpFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				helpFrame.dispose();
				helpFrame = new JFrame();
				helpFrame.addWindowListener(this);
			}
		});

		add(gamePanel);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
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

	setTitle(title + gamePanel.gameNumber);
		setResizable(false);
		setVisible(true);
	}

	private void initRemovedFrame() {
		removedFrame = new JFrame("Removed Tiles");
		JScrollPane scrollPane = new JScrollPane(gamePanel.getRemovedPanel());
		removedFrame.add(scrollPane);
		removedFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				removedTilesItem.setEnabled(true);
			}
		});
		gamePanel.resizeRemovedFrame();
	}
	/**
	 * Calls up a dialog to check what action the user wants to perform after
	 * the last possible tile set is removed
	 */
	public void checkEndGame() {
		String[] options = {"New Game", "Cancel", "Exit Game"};
		int selection = JOptionPane.showOptionDialog(this, "No more moves exist.\n" +
				"Choose Cancel to go back to this game, otherwise " +
				"choose New Game or Exit Game.", "End Game",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);

		gamePanel.stopFireworks();
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
	/**
	 * Called to enable the undo/redo menus, save menu, etc.
	 */
	public void checkEnabledMenus() {
		JMenu menu = getJMenuBar().getMenu(0);
		menu.getMenuComponent(UNDO_INDEX).setEnabled(gamePanel.canUndo());
		menu.getMenuComponent(REDO_INDEX)
				.setEnabled(gamePanel.canRedo());
	}

	private void close() {
		WindowEvent event = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
	}
	private void newGame() {
		newGame(null);
	}
	private void newGame(Long randomNumber) {
		boolean roundedCorners = gamePanel.hasRoundedCorners();
		remove(gamePanel);

		int width = getContentPane().getWidth();
		int height = getContentPane().getHeight();

		if (randomNumber == null) {
			gamePanel = new GamePanel(width, height, roundedCorners);
		}
		else {
			gamePanel = new GamePanel(width, height, roundedCorners, randomNumber);
		}

		add(gamePanel);

		checkEnabledMenus();

		setTitle(title + gamePanel.gameNumber);
		repaint();
	}

	public static void main(String[] args) {
		new MahjongBoard();
	}

}
