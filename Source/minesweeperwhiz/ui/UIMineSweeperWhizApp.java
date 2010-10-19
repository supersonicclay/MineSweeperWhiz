package minesweeperwhiz.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import minesweeperwhiz.MineSweeperWhizParameters;
import minesweeperwhiz.data.Board;

public class UIMineSweeperWhizApp extends JFrame {
	private static final long serialVersionUID = 1L;

	private UIBoard uiBoard;
	private UIGameButton uiGameButton;
	private UIMineTotals uiMineTotals;
	private JProgressBar progressBar;

	private static UIMineSweeperWhizApp instance = null;

	public static UIMineSweeperWhizApp getInstance() {
		if (instance == null) {
			instance = new UIMineSweeperWhizApp();
		}
		return instance;
	}

	private UIMineSweeperWhizApp() {

		if (MineSweeperWhizParameters.getInstance().showUI) {
			setJMenuBar(new UIMenuBar());
			uiGameButton = new UIGameButton();
			getContentPane().add(uiGameButton, BorderLayout.NORTH);

			uiBoard = new UIBoard();
			getContentPane().add(uiBoard, BorderLayout.CENTER);
			
			uiMineTotals = new UIMineTotals();
			getContentPane().add(uiMineTotals, BorderLayout.SOUTH);
			
			setTitle("Mine Sweeper Whiz");
			
			setVisible(true);
		}
		else {
			progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
			getContentPane().add(progressBar);
			setSize(300, 100);
			setVisible(true);
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void refresh(boolean newGame, Board board) {
		MineSweeperWhizParameters params = MineSweeperWhizParameters.getInstance();
		if (params.showUI) {
			if (newGame) {
				setSize(params.windowWidth, params.windowHeight);
				uiBoard.newGame(board);
			}
			uiGameButton.refresh(board);
			uiBoard.refresh(board);
			uiMineTotals.refresh(board);
		}
	}
	
	public void updateProgress(int gamesPlayed) {
		if (progressBar != null) {
			progressBar.setMaximum(MineSweeperWhizParameters.getInstance().testRuns);
			progressBar.setValue(gamesPlayed);
		}
	}
}
