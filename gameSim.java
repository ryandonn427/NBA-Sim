
public class gameSim {
	String home;
	String away;
	teamProb homeProb = new teamProb(home);
	teamProb awayProb = new teamProb(away);
	private int time;
	private int quarter;
	
	public gameSim(String home, String away) {
		this.home = home;
		this.away = away;
	}
	public void prepareStats() throws Exception{
		homeProb.generatePlayers();
		homeProb.generateStats();
		awayProb.generatePlayers();
		awayProb.generateStats();
	}
	public void Shot(double prob) {
		
	}
	public void Turnover(double prob) {
		
	}
	public void Steal(double prob) {
		
	}
	public void Block(double prob) {
		
	}
	public void Miss(double prob) {
		
	}
	public void Make(double prob) {
		
	}
	public void Rebound(double prob) {
		
	}
	public void Foul(double prob) {
		
	}
	
}
