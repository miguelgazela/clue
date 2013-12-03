package game_logic;

public class CluedoLogger {

		private boolean logging;
		private static CluedoLogger instance = null;
		
		private CluedoLogger() {
			logging = true;
		}
		
		// lazy instantiation 
		public static CluedoLogger getInstance() {
			if(instance == null) {
				instance = new CluedoLogger();
			}
			return instance;
		}
		
		public void log(String msg) {
			if(logging) {
				System.out.println(msg);
			}
		}
}
