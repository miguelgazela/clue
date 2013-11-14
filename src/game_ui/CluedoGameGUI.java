package game_ui;

import game_logic.GameManagerAgent;

import jade.core.behaviours.OneShotBehaviour;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class CluedoGameGUI extends JFrame {

	private static final long serialVersionUID = -5412659765861585077L;

    // Instance Variables
    private BorderLayout borderLayout_1;
    private GridLayout gridLayout_1;
    private JPanel mainPanel;
    private JButton startGameBtn;
    private JSlider numPlayersSlider;

    protected GameManagerAgent gameManagerAgent;

	public CluedoGameGUI(GameManagerAgent owner) {
		gameManagerAgent = owner;
		setupUI();
	}
	
	public void show() {
		System.out.println("Showing UI");
	}
	
	private void setupUI() {
		borderLayout_1 = new BorderLayout();
        gridLayout_1 = new GridLayout();
        mainPanel = new JPanel();
        startGameBtn = new JButton();
        numPlayersSlider = new JSlider();

        this.getContentPane().setLayout(borderLayout_1);
        mainPanel.setLayout(gridLayout_1);

        startGameBtn.setText("Start game!");
        startGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame(e);
			}
		});
	}
	
	private void startGame(ActionEvent e) {
		// add a behaviour to the host to start the game
		gameManagerAgent.addBehaviour(new OneShotBehaviour() {
			public void action() {
				((GameManagerAgent)myAgent).startGame(numPlayersSlider.getValue());
			}
		});
	}
}
