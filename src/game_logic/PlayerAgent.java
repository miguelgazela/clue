package game_logic;
// ------------------------------------------------------------
//   ParamAgent:   An Agent receiving parameters             
//
//   Usage:    % javac ParamAgent.java
//             % java jade.Boot  fred:ParamAgent(3 "Allo there")
//
// ... on UNIX, the agent specifier and arguments must be quoted:
//
//             % java jade.Boot 'fred:ParamAgent(3 "Allo there")'
// ------------------------------------------------------------

import java.io.IOException;
import java.rmi.activation.ActivationID;
import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

 public class PlayerAgent extends Agent 
 { 
	private static final long serialVersionUID = -4614773070990660799L;
	
	private CluedoLogger logger;
	private ArrayList<CluedoCard> myCards = null;

	protected void setup() 
	{ 
		logger = CluedoLogger.getInstance();
		logger.log("Created agent: "+getLocalName()+" with AID: "+getAID());


		try {
			// create the agent description of itself and register it
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
		} catch (FIPAException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		// notify the game manager agent that this player is ready to play
		GameMessage msg = new GameMessage(GameMessage.READY_PLAY);
		ACLMessage ready = new ACLMessage(ACLMessage.INFORM);
		
		try {
			ready.setContentObject(msg);
			ready.addReceiver(new AID("host", AID.ISLOCALNAME));
			send(ready);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		addBehaviour(new PlayerBehaviour());
	}
	
	private class PlayerBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 6875374470516568688L;

		@Override
		public void action() {
			ACLMessage msg = receive();

			if(msg != null) {
				try {
					GameMessage message = (GameMessage) msg.getContentObject();

					switch (message.getType()) {

					case GameMessage.DISTRIBUTE_CARDS: // receiving this players cards
					{
						if(myCards == null) {
							myCards = (ArrayList<CluedoCard>) message.getObject(0);
						} else {
							logger.warning("Receiving cards again.");
						}
					}
					break;

					default:
					{
						// should not get here!!!
						System.exit(-1);
					}
					break;
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			} else { // if no message is arrived, block the behaviour
				block();
			}
		}
	}
 }