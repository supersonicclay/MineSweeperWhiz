package minesolver.ui;

import javax.swing.JPanel;
import java.awt.GridLayout;
import minesolver.data.*;

public class UIBoard extends JPanel {
	private static final long serialVersionUID = 1L;

	
	public UIBoard() {
	}
	
	public void newGame(Board board) {
		removeAll();
		setLayout(new GridLayout(board.getHeight(), board.getWidth()));
		for (int y=0; y<board.getHeight(); ++y) {
			for (int x=0; x<board.getWidth(); ++x) {
				UICell c = new UICell(board.getCell(x, y));
				add(c);
			}
		}
		updateUI();
	}
	
	public void refresh(Board board) {
		for (int i=0; i<getComponentCount(); ++i) {
			((UICell)getComponent(i)).styleButton();
		}
	}
}
