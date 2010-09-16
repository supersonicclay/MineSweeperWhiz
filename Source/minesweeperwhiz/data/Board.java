package minesweeperwhiz.data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import minesweeperwhiz.GameStateCallback;
import minesweeperwhiz.MineSweeperWhizParameters;

public class Board {

	private Random rand;
	
	private Cell[][] cells;
	private int width;
	private int height;
	private int numMines;
	private boolean won;
	private boolean lost;
	
	GameStateCallback callback;

	/////////////////////////////////////////////////////////////////
	// Getters and Setters
	/////////////////////////////////////////////////////////////////
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getNumMines() {
		return numMines;
	}
	
	public Cell getCell(int x, int y) {
		return cells[x][y];
	}
	
	public boolean isWon() {
		return won;
	}
	
	public boolean isLost() {
		return lost;
	}
	
	
	/////////////////////////////////////////////////////////////////
	// Methods
	/////////////////////////////////////////////////////////////////

	public Board(GameStateCallback callback) {
		MineSweeperWhizParameters params = MineSweeperWhizParameters.getInstance();
		int seed = params.boardSeed;
		System.out.println("params.boardSeed=" + seed + ";");
		rand = new Random(seed);

		this.callback = callback;
		width = params.boardWidth;
		height = params.boardHeight;
		numMines = params.numMines;
		won = false;
		lost = false;

		constructCells();
		pickMinePositions();
		countNumMinesNear();
	}

	private void constructCells() {
		cells = new Cell[width][height];
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				cells[x][y] = new Cell(x, y, this);
			}
		}
	}

	private void pickMinePositions() {
		// Generate a list of possibilities
		ArrayList<Point> possibleMinePositions = new ArrayList<Point>(width*height);
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				possibleMinePositions.add(new Point(x, y));
			}
		}

		// Pick a position for each mine
		ArrayList<Point> minePositions = new ArrayList<Point>(numMines);
		for (int i=0; i<numMines; ++i) {
			// Pick a random position out of the list of possibilities and remove it as a possibility (for future mines)
			int randIndex = rand.nextInt(possibleMinePositions.size());
			Point minePosition = possibleMinePositions.remove(randIndex);
			minePositions.add(minePosition);
			cells[(int)minePosition.getX()][(int)minePosition.getY()].setMine(true);
		}
	}

	private void countNumMinesNear() {
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				Cell c = cells[x][y];
				if (c.isMine()) {
					c.setNumMinesNear(-1);
				}
				else {
					c.setNumMinesNear(getSurroundingCells(x, y, null, null, true, null).size());
				}
			}
		}
	}

	public ArrayList<Cell> getCells(Boolean uncovered, Boolean flagged, Boolean mine, Boolean minesNear) {
		ArrayList<Cell> cellsArray = new ArrayList<Cell>();
		for (int x=0; x<width; ++x) {
			for (int y=0; y<height; ++y) {
				Cell c = cells[x][y];
				if ((uncovered == null || c.isUncovered() == uncovered) &&
					(flagged == null || c.isFlagged() == flagged) &&
					(mine == null || c.isMine() == mine) && 
					(minesNear == null || (minesNear ? c.getNumMinesNear() > 0 : c.getNumMinesNear() == 0))) {
					cellsArray.add(c);
				}
			}
		}
		return cellsArray;
	}
	
	/**
	 * Get cells that surround the cell at (x,y). Does not include the cell at (x, y).
	 * 
	 * @param x
	 * @param y
	 * @param uncovered null means you don't care about the covered state
	 * @param flagged null means you don't care about the flagged state
	 * @param mine null means you don't care about the mine state
	 * @param minesNear null means you don't care whether mines are near
	 * @return
	 */
	public ArrayList<Cell> getSurroundingCells(int x, int y, Boolean uncovered, Boolean flagged, Boolean mine, Boolean minesNear) {
		ArrayList<Cell> surroundingCells = new ArrayList<Cell>();
		for (int i=Math.max(x-1, 0); i<=Math.min(x+1, width-1); ++i) {
			for (int j=Math.max(y-1, 0); j<=Math.min(y+1, height-1); ++j) {
				if (i != x || j != y) {
					Cell c = cells[i][j];
					if ((uncovered == null || c.isUncovered() == uncovered) &&
						(flagged == null || c.isFlagged() == flagged) &&
						(mine == null || c.isMine() == mine) &&
						(minesNear == null || (minesNear ? c.getNumMinesNear() > 0 : c.getNumMinesNear() == 0))) {
						surroundingCells.add(c);
					}
				}
			}
		}
		return surroundingCells;
	}
	
	public boolean isCellSatisfiedByFlags(int x, int y) {
		int numFlagsNear = getSurroundingCells(x, y, null, true, null, null).size();
		return numFlagsNear == cells[x][y].getNumMinesNear();
	}
	
	public boolean isCellSatisfiedByCovered(int x, int y) {
		int numCoveredNear = getSurroundingCells(x, y, false, null, null, null).size();
		return numCoveredNear == cells[x][y].getNumMinesNear();
	}

	/**
	 * Return true if this cell's surrounding cells are all flagged or
	 * uncovered and the number of surrounding flags is equal to the number
	 * of mines near. Otherwise returns false.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isCellCompletelySatisfied(int x, int y) {
		int numFlagsNear = getSurroundingCells(x, y, null, true, null, null).size();
		int numCoveredNear = getSurroundingCells(x, y, false, null, null, null).size();
		return (numFlagsNear == numCoveredNear) && (numFlagsNear == cells[x][y].getNumMinesNear());
	}
	
	
	public void flagCell(int x, int y) {
		cells[x][y].toggleFlagged();
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return true if we found a win or a loss
	 */
	public boolean uncoverCell(int x, int y) {
		Cell cell = cells[x][y];
		if (!cell.isUncovered() && !cell.isFlagged()) {
			// Uncover this cell
			cell.uncover();
			
			// Uncover all surrounding cells if there are no mines near
			if (cell.getNumMinesNear() == 0) {
				if (uncoverSurroundingCells(x, y)) {
					return true;
				}
			}
			
			if (checkForWinOrLoss()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean uncoverSurroundingCells(int x, int y) {
		ArrayList<Cell> surroundingCells = getSurroundingCells(x, y, false, false, null, null);
		Iterator<Cell> i = surroundingCells.iterator();
		while (i.hasNext()) {
			Cell sc = i.next();
			if (uncoverCell(sc.getX(), sc.getY())) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean uncoverSatisfiedSurroundingCells(int x, int y) {
		Cell cell = cells[x][y];
		if (cell.isUncovered() && isCellSatisfiedByFlags(x, y)) {
			if (uncoverSurroundingCells(x, y)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForWinOrLoss() {
		if (getCells(false, null, false, null).size() == 0) {
			System.out.println("All non-mines have been uncovered. They win.");
			won = true;
			if (callback != null) {
				callback.gameWon();
			}
			return true;
		}
		else if (getCells(true, null, true, null).size() > 0) {
			System.out.println("We found an uncovered mine. They lose.");
			lost = true;
			if (callback != null) {
				callback.gameLost();
			}
			return true;
		}
		return false;
	}
	
	public String toString() {
		String ret = "";
		for (int y=0; y<height; ++y) {
			for (int x=0; x<width; ++x) {
				ret += cells[x][y].toString() + " ";
			}
			ret += "\n";
		}
		return ret;
	}

}
