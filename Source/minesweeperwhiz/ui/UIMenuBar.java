package minesweeperwhiz.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import minesweeperwhiz.MineSweeperWhizApp;
import minesweeperwhiz.MineSweeperWhizParameters;

public class UIMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	JMenu gameMenu;
	JMenuItem gameNewPlayable;
	JMenuItem gameNewSolver;
	JMenuItem gameExit;
	
	JMenu difficultyMenu;
	JRadioButtonMenuItem difficultyBeginner;
	JRadioButtonMenuItem difficultyIntermediate;
	JRadioButtonMenuItem difficultyAdvanced;
	
	JMenu solverMenu;
	JMenuItem solverStart;
	JMenuItem solverStop;
	
	public UIMenuBar() {		
		// Game Menu
		gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		gameNewPlayable = new JMenuItem("New Playable");
		gameNewPlayable.setMnemonic(KeyEvent.VK_P);
		gameNewPlayable.addActionListener(this);
		gameMenu.add(gameNewPlayable);
		
		gameNewSolver = new JMenuItem("New Solver");
		gameNewSolver.setMnemonic(KeyEvent.VK_S);
		gameNewSolver.addActionListener(this);
		gameMenu.add(gameNewSolver);
		
		gameExit = new JMenuItem("Exit");
		gameExit.setMnemonic(KeyEvent.VK_X);
		gameExit.addActionListener(this);
		gameMenu.add(gameExit);

		add(gameMenu);
		
		// Difficulty Menu
		difficultyMenu = new JMenu("Difficulty");
		difficultyMenu.setMnemonic(KeyEvent.VK_S);
		
		ButtonGroup difficultyGroup = new ButtonGroup();
		difficultyBeginner = new JRadioButtonMenuItem("Beginner");
		difficultyBeginner.setMnemonic(KeyEvent.VK_B);
		difficultyBeginner.addActionListener(this);
		difficultyGroup.add(difficultyBeginner);
		difficultyMenu.add(difficultyBeginner);
		
		difficultyIntermediate = new JRadioButtonMenuItem("Intermediate");
		difficultyIntermediate.setSelected(true);
		difficultyIntermediate.setMnemonic(KeyEvent.VK_I);
		difficultyIntermediate.addActionListener(this);
		difficultyGroup.add(difficultyIntermediate);
		difficultyMenu.add(difficultyIntermediate);
		
		difficultyAdvanced = new JRadioButtonMenuItem("Advanced");
		difficultyAdvanced.setMnemonic(KeyEvent.VK_A);
		difficultyAdvanced.addActionListener(this);
		difficultyGroup.add(difficultyAdvanced);
		difficultyMenu.add(difficultyAdvanced);
		
		add(difficultyMenu);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == gameNewPlayable) {
			newPlayableGame();
		}
		if (source == gameNewSolver) {
			newSolver();
		}
		else if (source == gameExit) {
			exitGame();
		}
		else if (source == difficultyBeginner) {
			setBeginner();
		}
		else if (source == difficultyIntermediate) {
			setIntermediate();
		}
		else if (source == difficultyAdvanced) {
			setAdvanced();
		}
		else if (source == solverStart) {
			startSolver();
		}
		else if (source == solverStop) {
			stopSolver();
		}
	}
	
	private void newPlayableGame() {
		MineSweeperWhizParameters.getInstance().useSolver = false;
		MineSweeperWhizApp.getInstance().newGame();
	}
	
	private void newSolver() {
		MineSweeperWhizParameters.getInstance().useSolver = true;
		MineSweeperWhizApp.getInstance().newGame();
	}
	
	private void exitGame() {
		System.exit(0);
	}
	
	private void setBeginner() {
		MineSweeperWhizParameters.createBeginnerParams();
		MineSweeperWhizApp.getInstance().newGame();
	}
	
	private void setIntermediate() {
		MineSweeperWhizParameters.createIntermediateParams();
		MineSweeperWhizApp.getInstance().newGame();
	}

	private void setAdvanced() {
		MineSweeperWhizParameters.createAdvancedParams();
		MineSweeperWhizApp.getInstance().newGame();
	}

}
