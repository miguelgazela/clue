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

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

 public class Player extends Agent 
 { 
	private static final long serialVersionUID = -4614773070990660799L;

	private class PlayerBehaviour extends CyclicBehaviour {
		
		@Override
		public void action() {
			
		}

	}

	protected void setup() 
	{ 
		System.out.println("Created a new agent: "+getLocalName()+" with AID: "+getAID());

		try {
			// create the agent descrption of itself
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName( getAID() );
			DFService.register( this, dfd );
		} catch (FIPAException e) {
			e.printStackTrace();
		}

//		Object[] args = getArguments();
//		String s;
//		
//		if (args != null) {
//			for (int i = 0; i <args.length; i++) {
//				s = (String) args[i];
//				System.out.println("p" + i + ": " + s);
//			}
//		}
		
		// notify the game manager agent that we're ready to play
		ACLMessage ready = new ACLMessage(ACLMessage.INFORM);
		ready.setContent("READY");
		ready.addReceiver(new AID("host", AID.ISLOCALNAME));
		send(ready);
		
		// adding behaviour
		addBehaviour(new PlayerBehaviour());
	}
 }