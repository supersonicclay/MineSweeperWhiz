package minesweeperwhiz;

import minesweeperwhiz.data.*;
import minesweeperwhiz.ui.*;
import minesweeperwhiz.solver.*;

public class MineSweeperWhizApp implements GameStateCallback {

	private int gamesWon = 0;
	private int gamesLost = 0;
	private Solver solver;
	private Board board;

	private static MineSweeperWhizApp instance;
	public static MineSweeperWhizApp getInstance() {
		if (instance == null) {
			instance = new MineSweeperWhizApp();
		}
		return instance;
	}
	
	private MineSweeperWhizApp() {
	}	
	
	public void newGame() {
		
		//MineSweeperWhizParameters.createBeginnerParams();
		MineSweeperWhizParameters.createIntermediateParams();
		//MineSweeperWhizParameters.createAdvancedParams();
		
		MineSweeperWhizParameters params = MineSweeperWhizParameters.getInstance();
		
		board = new Board(this);

		if (params.useSolver) {
			solver = new Solver(board);
			solver.start();
		}

		UIMineSweeperWhizApp.getInstance().refresh(true, board);
	}
	
	public void gameWon() {
		++gamesWon;
		afterGame();
	}

	public void gameLost() {
		++gamesLost;
		afterGame();
	}
	
	private void afterGame() {
		MineSweeperWhizParameters params = MineSweeperWhizParameters.getInstance();
		UIMineSweeperWhizApp.getInstance().refresh(false, board);
		printStats();
		UIMineSweeperWhizApp.getInstance().updateProgress(gamesWon + gamesLost);
		if (gamesWon + gamesLost < params.testRuns && params.autoNewGame) {
			if (params.showUI && params.retrospectTime > 0) {
				try {
					Thread.sleep(params.retrospectTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			newGame();
		}
		else if (gamesWon + gamesLost >= params.testRuns) {
			System.exit(0);
		}
	}
	
	private void printStats() {
		System.out.println("");
		System.out.println("Total Games: " + (gamesWon + gamesLost));
		System.out.println("Games Won: " + gamesWon);
		System.out.println("Games Lost: " + gamesLost);
		System.out.println("Success Rate: " + 100.0f * gamesWon / (gamesWon + gamesLost) + "%");
		System.out.println("");
	}

	
	public static void main(String[] args) {
		System.out.println("minesweeperwhiz");
		
		MineSweeperWhizApp.getInstance();
		UIMineSweeperWhizApp.getInstance();
		MineSweeperWhizApp.getInstance().newGame();
	}

}
