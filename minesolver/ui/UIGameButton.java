package minesolver.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import minesolver.MineSolverApp;
import minesolver.data.Board;

public class UIGameButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;

	public UIGameButton() {
		setText(":)");
		setFont(new Font(null, Font.BOLD, 16));
		addActionListener(this);
	}
	
	public void refresh(Board board) {
		if (board.isWon()) {
			setText(":D");
		}
		else if (board.isLost()) {
			setText(":(");
		}
		else {
			setText(":)");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		MineSolverApp.getInstance().newGame();
	}

}
