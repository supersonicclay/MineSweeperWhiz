package minesweeperwhiz.ui;

import javax.swing.JLabel;

import minesweeperwhiz.data.Board;

public class UIMineTotals extends JLabel {
	private static final long serialVersionUID = 1L;

	public UIMineTotals() {
		setText("Flags: ");
	}
	
	public void refresh(Board board) {
		int numMines = board.getNumMines();
		int numFlags = board.getCells(null, true, null, null).size();
		
		setText("Flags: " + numFlags + "/" + numMines);
	}

}
