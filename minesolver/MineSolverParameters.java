package minesolver;

import java.util.Random;

public class MineSolverParameters {

	// UI parameters
	public boolean showUI = false;
	public int windowWidth = 730;
	public int windowHeight = 730;
	public boolean showMinesCheat = false;
	
	// Board parameters
	public int boardWidth;
	public int boardHeight;
	public int numMines;

	// Solver parameters
	public boolean useSolver = true;
	public int testRuns = 5000;
	public boolean autoNewGame = true;
	public int retrospectTime = 2000; // only pauses if showing UI
	public int thinkTime = 200; // only pauses if showing UI
	public int checkCombinationsAt = 20;
	public boolean showGuessingMessage = false;
	
	// Seed parameters
	public int boardSeed = new Random().nextInt();
	public int solverSeed = new Random().nextInt();
	
	
	// Singleton stuff
	private MineSolverParameters() { }
	private static MineSolverParameters instance = null;
	public static MineSolverParameters getInstance() {
		if (instance == null) {
			instance = new MineSolverParameters();
		}
		return instance;
	}
	
	public static MineSolverParameters createBeginnerParams() {
		MineSolverParameters params = getInstance();
		params.boardWidth = 8;
		params.boardHeight = 8;
		params.numMines = 10;
		params.windowWidth = 400;
		params.windowHeight = 400;
		
		params.boardSeed = new Random().nextInt();
		params.solverSeed = new Random().nextInt();

		return params;
	}
	
	public static MineSolverParameters createIntermediateParams() {
		MineSolverParameters params = getInstance();
		params.boardWidth = 16;
		params.boardHeight = 16;
		params.numMines = 40;

		params.boardSeed = new Random().nextInt();
		params.solverSeed = new Random().nextInt();

		/*
		// Solved
		params.boardSeed = -1028301677;
		params.solverSeed = 221196443;
		//*/
		
		/*
		// Dependency at the end
		params.boardSeed = -1496944151;
		params.solverSeed = 78769517;
		//*/
		
		/*
		// Mines left thought
		params.boardSeed=-1589090337;
		params.solverSeed=185032814;
		//*/
		
		/*
		// Dependency Checking saves the game
		params.boardSeed = 1479071150;
		params.solverSeed=645551791;
		//*/
		
		/*
		params.boardSeed=1468962165;
		params.solverSeed=-1718697797;
		//*/
		
		/*
		// Dependency flagging
		params.boardSeed = -1586150148;
		params.solverSeed = -2093149522;
		//*/
		
		//*
		// More than one possible assumption
		//params.boardSeed=-737186802;
		//params.solverSeed=483267357;
		//params.boardSeed=-1809712233;
		//params.solverSeed=513801699;
		//*/

		return params;
	}
	
	public static MineSolverParameters createAdvancedParams() {
		MineSolverParameters params = getInstance();
		params.boardWidth = 30;
		params.boardHeight = 16;
		params.numMines = 99;
		params.windowWidth = 1300;

		params.boardSeed = new Random().nextInt();
		params.solverSeed = new Random().nextInt();
		
		/*
		// Almost solved
		params.boardSeed = 1051698440;
		//params.solverSeed = -2063763828; // close
		params.solverSeed = 839799979; // closer
		//*/
		
		return params;
	}

}
