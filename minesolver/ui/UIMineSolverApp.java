package minesolver.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import minesolver.MineSolverParameters;
import minesolver.data.Board;

public class UIMineSolverApp extends JFrame {
	private static final long serialVersionUID = 1L;

	private UIBoard uiBoard;
	private UIGameButton uiGameButton;
	private UIMineTotals uiMineTotals;
	private JProgressBar progressBar;

	private static UIMineSolverApp instance = null;

	public static UIMineSolverApp getInstance() {
		if (instance == null) {
			instance = new UIMineSolverApp();
		}
		return instance;
	}

	private UIMineSolverApp() {

		if (MineSolverParameters.getInstance().showUI) {
			uiGameButton = new UIGameButton();
			getContentPane().add(uiGameButton, BorderLayout.NORTH);

			uiBoard = new UIBoard();
			getContentPane().add(uiBoard, BorderLayout.CENTER);
			
			uiMineTotals = new UIMineTotals();
			getContentPane().add(uiMineTotals, BorderLayout.SOUTH);
			
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
		MineSolverParameters params = MineSolverParameters.getInstance();
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
			progressBar.setMaximum(MineSolverParameters.getInstance().testRuns);
			progressBar.setValue(gamesPlayed);
		}
	}
}
