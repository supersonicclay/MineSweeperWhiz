package minesweeperwhiz.data;

public class Cell {

	/////////////////////////////////////////////////////////////////
	// Members
	/////////////////////////////////////////////////////////////////

	private Board board;
	private boolean mine;
	private boolean flagged;
	private boolean uncovered;
	private int numMinesNear = -1;
	private int x;
	private int y;


	/////////////////////////////////////////////////////////////////
	// Constructors
	/////////////////////////////////////////////////////////////////

	public Cell(int x, int y, Board board) {
		this.board = board;
		this.x = x;
		this.y = y;
	}


	/////////////////////////////////////////////////////////////////
	// Getters and Setters
	/////////////////////////////////////////////////////////////////

	public Board getBoard() {
		return board;
	}
	
	public boolean isMine() {
		return mine;
	}
	
	public boolean isFlagged() {
		return flagged;
	}
	
	public boolean isUncovered() {
		return uncovered;
	}

	public int getNumMinesNear() {
		return numMinesNear;
	}

	public void setMine(boolean isMine) {
		this.mine = isMine;
	}
	
	public void toggleFlagged() {
		if (!uncovered) {
			flagged = !flagged;
		}
	}
	
	public void uncover() {
		if (!flagged) {
			uncovered = true;
		}
	}

	public void setNumMinesNear(int numMinesNear) {
		this.numMinesNear = numMinesNear;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	
	/////////////////////////////////////////////////////////////////
	// Methods
	/////////////////////////////////////////////////////////////////

	public String toString()  {
		String ret;
		if (mine) {
			ret = "X";
		}
		else if (flagged) {
			ret = "F";
		}
		else if (uncovered) {
			ret = "" + numMinesNear;
		}
		else {
			ret = "-";
		}
		return ret;
	}
}
