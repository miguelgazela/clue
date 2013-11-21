package game_ui;

import game_logic.GameManagerAgent;
import jade.core.behaviours.OneShotBehaviour;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class CluedoGameGUI extends JFrame {

	private static final long serialVersionUID = -5412659765861585077L;

    // Instance Variables
    private JButton startGameBtn;
    private JPanel mainPanel;

    protected GameManagerAgent gameManagerAgent;

	public CluedoGameGUI(GameManagerAgent owner) {
        super("Cluedo - Multi-agent implementation");
		gameManagerAgent = owner;
		createAndShowUI();
	}
	
	private void createAndShowUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        getContentPane().add(mainPanel);

        startGameBtn = new JButton();
        startGameBtn.setText("Start game!");
        startGameBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame(e);
			}
		});
        getContentPane().add(startGameBtn);

        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
	}

	// add a behaviour to the host to start the game
	private void startGame(ActionEvent e) {
		gameManagerAgent.addBehaviour(new OneShotBehaviour() {
			public void action() {
				((GameManagerAgent)myAgent).createGame(3);
			}
		});
	}
}
