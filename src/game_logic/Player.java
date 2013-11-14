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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
 
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
		Object[] args = getArguments();
		String s;
		
		if (args != null) {
			for (int i = 0; i <args.length; i++) {
				s = (String) args[i];
				System.out.println("p" + i + ": " + s);
			}
		}
		
		// adding behaviour
		addBehaviour(new PlayerBehaviour());
	}
 }