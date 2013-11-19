package minesolver.solver;

import minesolver.MineSolverParameters;
import minesolver.data.*;
import minesolver.ui.UIMineSolverApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JOptionPane;


public class Solver extends Thread {
	
	private Board board;
	private Random rand;
	private boolean dead = false;
	
	public Solver(Board board) {
		this.board = board;
		
		int seed = MineSolverParameters.getInstance().solverSeed;
		System.out.println("params.solverSeed=" + seed + ";");
		rand = new Random(seed);
		setName("Solver: " + seed);
	}
	public void run() {
		MineSolverParameters params = MineSolverParameters.getInstance();
		try {
			while (!dead && !board.isWon() && !board.isLost()) {
				if (params.showUI && params.thinkTime > 0) {
					Thread.sleep(params.thinkTime);
				}
				think();
				UIMineSolverApp.getInstance().refresh(false, board);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void die() {
		dead = true;
	}
	
	
	private void think() {
		System.out.print("THINK: ");
		Cell c;
		Iterator<Cell> itr;
		
		boolean doneThinking = false;
		
		ArrayList<Cell> coveredUnflaggedCells = board.getCells(false, false, null, null);
		ArrayList<Cell> uncoveredNonZeroCells = board.getCells(true, null, null, true);
		
		// If we've flagged all the mines, we're done; just clear the rest
		if (board.getCells(null, true, null, null).size() == board.getNumMines()) {
			uncoverAllCoveredUnflagged();
			doneThinking = true;
		}
		
		// First pass
		if (!doneThinking) {
			// Look at each of the cells with numbers
			itr = uncoveredNonZeroCells.iterator();
			while (itr.hasNext()) {
				c = itr.next();
				if (board.isCellCompletelySatisfied(c.getX(), c.getY())) {
					continue;
				}
				else if (checkForSatisfiedByFlags(c)) {
					doneThinking = true;
					break;
				}
				else if (checkForSatisfiedByCovered(c)) {
					doneThinking = true;
					break;
				}
			}
		}

		// Second pass
		if (!doneThinking) {
			itr = uncoveredNonZeroCells.iterator();
			while (itr.hasNext()) {
				c = itr.next();
				if (checkForDependencies(c)) {
					doneThinking = true;
					break;
				}
			}
		}
		
		// Third pass
		if (!doneThinking) {
			// For now, let's keep it faster and not do this until the end
			if (board.getCells(false, false, null, null).size() <= MineSolverParameters.getInstance().checkCombinationsAt) {
				if (checkMineCombinations()) {
					doneThinking = true;
				}
			}
		}
		
		if (!doneThinking) {
			makeGuess(coveredUnflaggedCells, uncoveredNonZeroCells);
		}
	}
	
	private void uncoverAllCoveredUnflagged() {
		ArrayList<Cell> allCovered = board.getCells(false, false, null, null);
		Iterator<Cell> itr = allCovered.iterator();
		while (itr.hasNext()) {
			Cell c = itr.next();
			board.uncoverCell(c.getX(), c.getY());
		}
	}
	
	private boolean checkForSatisfiedByFlags(Cell cell) {
		if (board.isCellSatisfiedByFlags(cell.getX(), cell.getY())) {
			System.out.println("Cell is satisfied by flags: " + coords(cell));
			board.uncoverSatisfiedSurroundingCells(cell.getX(), cell.getY());
			return true;
		}
		return false;
	}
	
	private boolean checkForSatisfiedByCovered(Cell cell) {
		if (board.isCellSatisfiedByCovered(cell.getX(), cell.getY())) {
			System.out.println("Cell is satisfied by covered: " + coords(cell));
			flagSurroundingCells(cell.getX(), cell.getY());
			return true;
		}
		return false;
	}
	
	/**
	 * Checks to see if cell's possible surrounding mines would fill a neighboring
	 * uncovered, non-zero cell. If so, clears remaining surrounding cells of that
	 * neighbor.
	 * At the same time checks to see if cell's possible surrounding mines would be
	 * sufficient to flag some surrounding cells of a neighbor. 
	 * 
	 * @param cell
	 * @return
	 */
	private boolean checkForDependencies(Cell cell) {

		// First lets determine how many mines are left for this cell
		ArrayList<Cell> flagged = board.getSurroundingCells(cell.getX(), cell.getY(), null, true, null, null);
		// This is the number of mines that will be in possibleMines
		int minesLeft = cell.getNumMinesNear() - flagged.size();
		

		// Get the surrounding possible mines (unflagged)
		ArrayList<Cell> possibleMines = board.getSurroundingCells(cell.getX(), cell.getY(), false, false, null, null);
		Iterator<Cell> itr = possibleMines.iterator();
		while (itr.hasNext()) {
			Cell possibleMine = itr.next();
			
			// Get the uncovered non-zero neighbors of each possibility
			ArrayList<Cell> neighbors = board.getSurroundingCells(possibleMine.getX(), possibleMine.getY(), true, null, null, true);
			Iterator<Cell> itr2 = neighbors.iterator();
			while (itr2.hasNext()) {
				Cell neighbor = itr2.next();
				if (neighbor == cell) { // not interested in the original cell
					continue;
				}
				
				// Get the flagged mines surrounding this neighbor
				ArrayList<Cell> neighborFlagged = board.getSurroundingCells(neighbor.getX(), neighbor.getY(), null, true, null, null);
				
				// Get the neighbor's surrounding possible mines (unflagged)
				ArrayList<Cell> neighborsPossibleMines = board.getSurroundingCells(neighbor.getX(), neighbor.getY(), false, false, null, null);
				
				// neighborsExtra = neighborsPossibleMines - possibleMines
				ArrayList<Cell> neighborsExtra = new ArrayList<Cell>(neighborsPossibleMines);
				neighborsExtra.removeAll(possibleMines);
				
				// If the original cell's possible mines are all near this neighbor
				if (neighborsPossibleMines.containsAll(possibleMines) && neighborsPossibleMines.size() > possibleMines.size()) {
					if (neighbor.getNumMinesNear() - neighborFlagged.size() - minesLeft == 0) {
						// The number of mines in possibleMines will satisfy this neighbor
						//  Clear neighbor's remaining covered surrounding cells
						System.out.println("Possible mines near " + coords(cell) + " satisfied neighboring cell: " + coords(neighbor));
						Iterator<Cell> uncoverItr = neighborsExtra.iterator();
						while (uncoverItr.hasNext()) {
							Cell cellToUncover = uncoverItr.next();
							board.uncoverCell(cellToUncover.getX(), cellToUncover.getY());
						}
						return true;
					}
					else if (neighbor.getNumMinesNear() - neighborFlagged.size() - minesLeft == neighborsExtra.size()) {
						// The number of mines in possibleMines will let us flag all of neighborsExtra
						System.out.println("Possible mines near " + coords(cell) + " is enough to flag extras for neighboring cell: " + coords(neighbor));
						Iterator<Cell> flagItr = neighborsExtra.iterator();
						while (flagItr.hasNext()) {
							Cell cellToFlag = flagItr.next();
							board.flagCell(cellToFlag.getX(), cellToFlag.getY());
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks combinations of possible mines under the remaining
	 * covered cells. If a cell is a mine in all combinations
	 * that could work, that cell is flagged. If a cell is not
	 * a mine in all combinations that could work, that cell is 
	 * cleared.
	 * 
	 * @return true if it flags or clears at least one cell
	 */
	private boolean checkMineCombinations() {
		ArrayList<Cell> potentialMines  = board.getCells(false, false, null, null);
		int minesLeft = board.getNumMines() - board.getCells(null, true, null, null).size(); // total mines - total flagged
		
		// The number of assumptions that are possible
		int possibleAssumptions = 0;
		
		// Count of the number of times that each mine is possible
		int[] possibleAssumptionTotals = new int[potentialMines.size()];
		
		// Create a CombinationGenerator that will pick mines for us
		CombinationGenerator cg = new CombinationGenerator(potentialMines.size(), minesLeft);
		while (cg.hasMore()) {
			int[] indices = cg.getNext();
			
			ArrayList<Cell> assumedMines = new ArrayList<Cell>(minesLeft);
			for (int i=0; i<indices.length; ++i) {
				assumedMines.add(potentialMines.get(indices[i]));
			}
			
			if (isAssumptionPossible(assumedMines)) {
				for (int i=0; i<indices.length; ++i) {
					++possibleAssumptionTotals[indices[i]];
				}
				++possibleAssumptions;
			}
		}

		boolean didSomething = false;
		for (int i=0; i<possibleAssumptionTotals.length; ++i) {
			Cell c = potentialMines.get(i);
			if (possibleAssumptionTotals[i] == 0) {
				// In all combinations that work, this cell is not a mine
				board.uncoverCell(c.getX(), c.getY());
				didSomething = true;
			}
			else if (possibleAssumptionTotals[i] == possibleAssumptions) {
				// In all combinations that work, this cell is a mine
				board.flagCell(c.getX(), c.getY());
				didSomething = true;
			}
		}
		
		return didSomething;
	}

	/**
	 * Assumes the given cells are the remaining unflagged mines
	 * and checks for any contradiction. A contradiction is
	 * an uncovered cell that is overfilled or underfilled.
	 * 
	 * @param assumedMines an array of cells that are assumed to be mines
	 * @return true if there are no contradictions; false otherwise
	 */
	private boolean isAssumptionPossible(ArrayList<Cell> assumedMines) {
		
		boolean isPossible = true;

		// Get all the uncovered cells and check them for contraditction
		ArrayList<Cell> uncoveredNonZeroCells = board.getCells(true, null, null, true);
		Iterator<Cell> itr = uncoveredNonZeroCells.iterator();
		while (itr.hasNext()) {
			Cell c = itr.next();

			int minesNear = 0;
			ArrayList<Cell> coveredSurrounding = board.getSurroundingCells(c.getX(), c.getY(), false, null, null, null);
			Iterator<Cell> surroundingItr = coveredSurrounding.iterator();
			while (surroundingItr.hasNext()) {
				Cell surroudingCell = surroundingItr.next();
				if (surroudingCell.isFlagged()) {
					++minesNear;
				}
				else if (assumedMines.contains(surroudingCell)) {
					++minesNear;
				}
			}
			
			if (minesNear != c.getNumMinesNear()) {
				isPossible = false;
				break;
			}
		}

		return isPossible;
	}
	
	private void makeGuess(ArrayList<Cell> coveredCells, ArrayList<Cell> uncoveredNonZeroCells) {
		
		MineSolverParameters params = MineSolverParameters.getInstance();
		
		float minProbability = 100.0f; // probability of hitting a mine
		ArrayList<Cell> surroundingBestChanceCell = null;
		
		Cell c;
		Iterator<Cell> itr;
		
		itr = uncoveredNonZeroCells.iterator();
		while (itr.hasNext()) {
			c = itr.next();
			
			ArrayList<Cell> surroundingFlagged = board.getSurroundingCells(c.getX(), c.getY(), null, true, null, null);
			ArrayList<Cell> surroundingCoveredUnflagged = board.getSurroundingCells(c.getX(), c.getY(), false, false, null, null);
			float prob = (float)(c.getNumMinesNear()-surroundingFlagged.size()) / surroundingCoveredUnflagged.size();
			if (prob < minProbability) {
				minProbability = prob;
				surroundingBestChanceCell = surroundingCoveredUnflagged;
			}
		}

		// The number of mines that are left is the total number of mines
		//  minus how many we've flagged
		int numMinesLeft = board.getNumMines() - board.getCells(false, true, null, null).size();
		// The possible locations for the remaining mines are the covered, unflagged cells
		int numPossibleLocations = board.getCells(false, false, null, null).size();
		float wildGuessProbability = (float)numMinesLeft / numPossibleLocations;

		if (minProbability <= wildGuessProbability) {
			if (params.showUI && params.showGuessingMessage) {
				JOptionPane.showMessageDialog(null, "Making an educated guess");
			}
			
			c = surroundingBestChanceCell.get(rand.nextInt(surroundingBestChanceCell.size()));
			System.out.println("Making an educated guess:  " + coords(c));
			board.uncoverCell(c.getX(), c.getY());
		}
		else {
			if (params.showUI && params.showGuessingMessage) {
				JOptionPane.showMessageDialog(null, "Making a wild guess");
			}
			int randCell = rand.nextInt(coveredCells.size());
			c = coveredCells.get(randCell);
			System.out.println("Making a wild guess: " + coords(c));
			board.uncoverCell(c.getX(), c.getY());
		}
	}

	private void flagSurroundingCells(int x, int y) {
		ArrayList<Cell> surroundingCells = board.getSurroundingCells(x, y, null, false, null, null);
		Iterator<Cell> itr = surroundingCells.iterator();
		while (itr.hasNext()) {
			itr.next().toggleFlagged();
		}
	}
	
	
	private static String coords(Cell c) {
		return "(" + c.getX() + ", " + c.getY() + ")";
	}
}
