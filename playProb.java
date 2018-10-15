public class playProb{
	static String name;
	static String team;
	static double steal;
	static double turnover;
	static double shot;
	static double typeShot;
	static double miss;
	static double make;
	static double block;
	static double rebound;
	static double assist;
	static int total;
	//SELECT DISTINCT COUNT(gameID) FROM pbp
	//WHERE player1 = this.name OR player2 = this.name;
	
	public playerProb(String name, String team){
		this.name = name;
		this.team = team;
	}
	
}
