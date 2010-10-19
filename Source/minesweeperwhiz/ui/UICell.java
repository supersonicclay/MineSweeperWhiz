package minesweeperwhiz.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

import minesweeperwhiz.MineSweeperWhizParameters;
import minesweeperwhiz.data.*;

public class UICell extends JButton implements MouseListener {
	private static final long serialVersionUID = 1L;

	private Cell cell;

	public UICell(Cell cell) {
		this.cell = cell;
		this.addMouseListener(this);
	}
	
	public void styleButton() {

		setFont(new Font(null, Font.BOLD, 11));
		setBackground(Color.LIGHT_GRAY);
		
		if (cell.isUncovered()) {
			if (cell.isMine()) {
				setText("X");
				setBackground(Color.RED);
			}
			else {
				if (cell.getNumMinesNear() > 0) {
					setText("" + cell.getNumMinesNear());
				}
				setForeground(getForegroundColor(cell.getNumMinesNear()));
				setBackground(Color.WHITE);
			}
		}

		if (cell.isFlagged()) {
			if (cell.getBoard().isLost() && !cell.isMine()) {
				// Misflagged
				setText("X");
			}
			setBackground(Color.GREEN);
		}
		
		if (cell.isMine()) {
			if (cell.getBoard().isWon()) {
				setText("M");
				setBackground(Color.GREEN);
			}
			else if (cell.getBoard().isLost() && !cell.isFlagged()) {
				setBackground(Color.RED);
			}
			
			if (MineSweeperWhizParameters.getInstance().showMinesCheat) {
				setText("M");
			}
		}
		
	}
	
	private Color getForegroundColor(int numMinesNear) {
		Color fg;
		switch (numMinesNear) {
			case 1:
				fg = Color.BLUE;
				break;
			case 2:
				fg = new Color(0, 128, 0);
				break;
			case 3:
				fg = Color.RED;
				break;
			case 4:
				fg = new Color(0, 0, 128); //dark blue
				break;
			case 5:
				fg = new Color(128, 0, 0); //maroon
				break;
			case 6:
				fg = Color.CYAN;
				break;
			case 7:
				fg = new Color(255, 0, 255); // purple
				break;
			case 8:
			default:
				fg = Color.BLACK;
				break;
		}
		return fg;
	}

	public void mouseClicked(MouseEvent e) {
		Board b = cell.getBoard();
		if (!b.isWon() && !b.isLost()) {
			// Right click toggles flagged state
			if (e.getButton() == MouseEvent.BUTTON3) {
				b.flagCell(cell.getX(), cell.getY());
			}
			
			// Left click
			else if (e.getButton() == MouseEvent.BUTTON1) {
				if (!cell.isUncovered()) {
					b.uncoverCell(cell.getX(), cell.getY());
				}
				else {
					b.uncoverSatisfiedSurroundingCells(cell.getX(), cell.getY());
				}
			}
			UIMineSweeperWhizApp.getInstance().refresh(false, cell.getBoard());
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
