package minesolver;

import minesolver.data.*;
import minesolver.ui.*;
import minesolver.solver.*;

public class MineSolverApp implements GameStateCallback {

	private int gamesWon = 0;
	private int gamesLost = 0;
	private Solver solver;
	private Board board;

	private static MineSolverApp instance;
	public static MineSolverApp getInstance() {
		if (instance == null) {
			instance = new MineSolverApp();
		}
		return instance;
	}
	
	private MineSolverApp() {
	}	
	
	public void newGame() {
		
		//MineSolverParameters.createBeginnerParams();
		MineSolverParameters.createIntermediateParams();
		//MineSolverParameters.createAdvancedParams();
		
		MineSolverParameters params = MineSolverParameters.getInstance();
		
		board = new Board(this);

		if (params.useSolver) {
			solver = new Solver(board);
			solver.start();
		}

		UIMineSolverApp.getInstance().refresh(true, board);
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
		MineSolverParameters params = MineSolverParameters.getInstance();
		UIMineSolverApp.getInstance().refresh(false, board);
		printStats();
		UIMineSolverApp.getInstance().updateProgress(gamesWon + gamesLost);
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
		System.out.println("MineSolver");
		
		MineSolverApp.getInstance();
		UIMineSolverApp.getInstance();
		MineSolverApp.getInstance().newGame();
	}

}
