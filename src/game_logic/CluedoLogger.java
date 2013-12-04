package game_logic;

import jade.tools.logging.ontology.SetLevel;

public class CluedoLogger {
	
		public static final int ERRORS = 1;
		public static final int WARNINGS = 2;
		public static final int LOGS = 3;

		private boolean logging;
		private int logLevel;
		private static CluedoLogger instance = null;
		
		private CluedoLogger() {
			logging = true;
			logLevel = LOGS;
		}
		
		// lazy instantiation 
		public static CluedoLogger getInstance() {
			if(instance == null) {
				instance = new CluedoLogger();
			}
			return instance;
		}
		
		public void log(String msg) {
			if(logging && logLevel <= LOGS) {
				System.out.println(msg);
			}
		}
		
		public void log(String player, String msg) {
			if(logging && logLevel <= LOGS) {
				System.out.println(player+" says: "+msg);
			}
		}
		
		public void warning(String msg) {
			if(logging && logLevel <= WARNINGS) {
				System.out.println(msg);
			}
		}
		
		public void error(String msg) {
			if(logging && logLevel == ERRORS) {
				System.out.println(msg);
			}
		}
		
		public void SetLevel(int level) {
			if(level < ERRORS || level > LOGS) {
				logLevel = LOGS;
			} else {
				logLevel = level;
			}
		}
		
		public void turnOn() {
			logging = true;
		}
		
		public void turnOff() {
			logging = false;
		}
}
