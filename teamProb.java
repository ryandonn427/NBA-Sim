
import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class teamProb{
	private String team;
	private String homeAway;
	private int time;
	private float shotMean;
	private float shotStdev;
	private float threeMean;
	private float threeStdev;
	private float turnoverMean;
	private float turnoverStdev;
	private float stealMean;
	private float stealStdev;
	private float reboundMean;
	private float reboundStdev;
	private float foulMean;
	private float foulStdev;
	private float shootingFoulMean;
	private float shootingFoulStdev;
	private String exclude = "";
	private String include = "";
	private String includeExclude;
	private boolean inc;
		public teamProb(String team,String homeAway){
		this.team = team;
		this.homeAway = homeAway;
		}
		public teamProb(String team, String homeAway,String includeExclude,boolean inc) {
			this.team = team;
			this.homeAway = homeAway;
			this.includeExclude = includeExclude;
			this.inc = inc;

		}
		
		
	//Start

	/*DECLARE a method called getShots()
		Generate a SQL query that returns a float that tells us how often this team shoot
	*/
	/*
		SELECT gameid,dates-datespassed FROM 
		(SELECT *,LAG(dates) OVER (ORDER BY dates) as datespassed 
				FROM (SELECT DISTINCT gameid,TO_DATE(CAST(date AS varchar),'YYYYMMDD') as dates 
						FROM pbp WHERE team1  = 'BKN' 
						ORDER BY dates) d) m;
	/*
	 * 
	 */
	public void generateExclusions() {
		System.out.println(includeExclude);
		if(inc==false) {
			exclude = String.format(" AND gameid IN "
				+ "(SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' "
				+ "EXCEPT "
				+ "SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' AND player1 = '%s')", team,team,includeExclude);
			include="";
		}else {
			include = String.format(" AND gameid IN "
					+ "(SELECT DISTINCT gameid FROM pbp "
					+ "WHERE team1 = '%s' AND player1 = '%s')", team,includeExclude);
			exclude="";
		}
		
	}
	public void generateExclusions(String player) {
		System.out.println(includeExclude);
		if(inc==false) {
			exclude = String.format(" AND gameid IN "
				+ "(SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' "
				+ "EXCEPT "
				+ "SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,team,includeExclude,player);
			include="";
		}else {
			include = String.format(" AND gameid IN "
					+ "(SELECT DISTINCT gameid FROM pbp "
					+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,includeExclude,player);
			exclude="";
		}
	
		
	}
	
	public ResultSet generateQuery(String command){
		try{
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(command);
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public ResultSet generateQuery(String command,Statement statement) throws Exception{
		ResultSet rs = statement.executeQuery(command);
		return rs;
	}
	public float getResult(String command,String type) throws SQLException{
		int result = 0;
		ResultSet rs = generateQuery(command);
		while(rs.next()) {
			result = rs.getInt(type);
		}
		return (float) (result);
	}
	public float getResult(String command,String type,Statement statement) throws Exception{
		int result = 0;
		ResultSet rs = generateQuery(command,statement);
		while(rs.next()) {
			result = rs.getInt(type);
		}
		return (float) (result);
	}
	public float getFreeThrow(boolean made) throws SQLException{
		String shot;
		if(made == true) {
			shot = "Made";
		}else {
			shot = "Missed";
		}
		
		String command = String.format("SELECT COUNT(action1) as freethrow FROM pbp "
				+ "WHERE team1 = '%s' AND %s = '%s' AND action1 = '"
				+shot
				+ " Free Throw'"
				+"%s"
				+"%s"
				+";", 
				team,homeAway,team,exclude, include);
		//System.out.println(command);
		String type = "freethrow";
		return getResult(command,type);
	}
	public float getShots() throws SQLException{
		System.out.println(includeExclude);
		System.out.println(exclude);
		String command = String.format("SELECT COUNT(action1) as shot FROM pbp "
				+ "WHERE  (team1 = '%s') AND %s = '%s' AND (action1 ~ 'shot' OR action1 ~ 'Shot')"
				+ "%s"
				+ "%s"
				+ ";",
				team,homeAway,team,exclude,include);
		System.out.println(exclude);
		System.out.println(command);
		String type = "shot";
		return getResult(command, type);
	}
	
	/*DECLARE a method called getTOV()
		Generate a SQL query that returns a float that tells us how often this team turns the ball over
	*/
	public float getTOV() throws SQLException{
		int result = 0;
		String command = String.format("SELECT COUNT(action1) as tov FROM pbp "
				+ "WHERE team1 = '%s' AND action1 = 'Turnover' AND %s = '%s'"
				+ "%s"
				+"%s"
				+ ";", 
				team,homeAway,team,exclude,include);
		//System.out.println(command);
		String type = "tov";
		return getResult(command,type);
		
	}
	/*
	DECLARE a method called getStoppage()
		Generate a SQL query that returns a float that tells us how often there is a stoppage
	*/
	public float getStoppage() throws SQLException{
		return getShots() - getFoul(false);
	}
	/*
	DECLARE a method called getFoul()
		Generate a SQL query that returns a float that tells us how often there is a foul
	*/
	public float getFoul(boolean Shooting) throws SQLException{
		String shoot;
		if(Shooting) {
			shoot = "Shooting ";
		}else {
			shoot = "";
		}
		String command = String.format("SELECT COUNT(action1) as foul FROM pbp "
				+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '"
				+shoot
				+ "Foul'"
				+ "%s"
				+"%s"
				+ ";", 
				homeAway,team,team,exclude,include);
		System.out.println(command);
		String type = "foul";
		return getResult(command,type);
	}

	/*
	DECLARE a method called block()
		Generate a SQL query that returns a float that tells us how often a team blocks a shot
	
	DECLARE a method called threePointer()
		Generate a SQL query that returns a float that tells us how often a team takes a three
	*/
	public float threePointer() throws SQLException{
		String command = String.format("SELECT COUNT(action1) as three FROM pbp "
				+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '3pt Shot'"
				+ "%s"
				+"%s"
				+ ";",
				homeAway,team,team,exclude,include);
		//System.out.println(command);
		String type = "three";
		return getResult(command,type);
	}
	/*
	DECLARE a method called madeShot()
		Generate a SQL query that retuns a float that tells us how often a team makes a shot
	*/
	public float madeShot(String trend) throws SQLException{
		String command = String.format("SELECT COUNT(makes) as make FROM "
				+ "(SELECT action1,points,gameid,"
				+ "twoseq as makes "
				+ "FROM pbp "
				+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
				+ "AND team1 = '%s' AND "
				+ "%s = '%s') trends "
				+ "WHERE makes ='%s'"
				+ "%s"
				+"%s"
				+ ";",team,homeAway,team,trend,exclude,include);
		//System.out.println(command);
		String type = "make";
		return getResult(command,type);
	}
	
	public float madeThrees(String trend) throws SQLException{
		String command = String.format("SELECT COUNT(makes) as make FROM "
				+ "(SELECT action1,points, gameid,"
				+ "twoseq as makes "
				+ "FROM pbp "
				+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
				+ "AND team1 = '%s' AND "
				+ "action1 = '3pt Shot' AND "
				+ "%s = '%s') trends "
				+ "WHERE makes ='%s'"
				+ "%s"
				+"%s"
				+ ";",team,homeAway,team,trend,exclude,include);
		//System.out.println(command);
		String type = "make";
		return getResult(command,type);
	}
	/*
	DECLARE a method called Steal()
		Generate a SQL query that returns a float that tells us how often a team steals given there is a turnover
	*/
	public float getSteal() throws SQLException{
		String command = String.format("SELECT count(action2) as steal FROM pbp "
				+ "WHERE team2 = '%s' AND %s = '%s' AND action2 = 'Steal'"
				+ "%s"
				+ "%s"
				+ ";"
				, team,homeAway,team,exclude,include);
		//System.out.println(command);
		String type = "steal";
		return getResult(command,type);
	}
	/*
	DECLARE a method called Assist()
		Generate a SQL query that returns a float that tells us how often a team gets an assist given there is a point

	DECLARE a method called Rebound()
		Generate a SQL query that returns a float that tells us how often a team gets a rebound
	*/
	public float getRebound() throws SQLException{
		String command = String.format("SELECT count(action1) as rebound "
				+ "FROM pbp "
				+ "WHERE team1 = '%s' and action1 = 'Rebound' AND %s = '%s'"
				+ "%s"
				+ "%s"
				+ ";"
				, team,homeAway,team,exclude,include);
		//System.out.println(command);
		String type = "rebound";
		return getResult(command,type);
	}
	public float getTotalRebounds() throws SQLException{
		String command  = String.format("SELECT COUNT(action1) as rebounds FROM pbp "
				+ "WHERE gameId IN (SELECT DISTINCT GameId FROM pbp WHERE team1 = '%s') "
				+ "AND action1 = 'Rebound' AND %s = '%s'"
				+ "%s"
				+"%s"
				+ ";",
				team,homeAway,team,exclude,include);
		//System.out.println(command);
		String type = "rebounds";
		return getResult(command,type);
	}
	public float getOffensiveRebound() throws SQLException{
		String command = String.format("SELECT COUNT(rebound.action1) offreb "
				+ "FROM (SELECT action1,LAG(team1) OVER (ORDER BY gameid,playid) team,team1,home,away,gameid FROM pbp) "
				+ "AS rebound WHERE rebound.team = rebound.team1 AND rebound.team1 = '%s' AND "
				+ "action1 = 'Rebound' AND %s = '%s'"
				+ "%s"
				+"%s"
				+ ";",
				team,homeAway,team,exclude,include);
		//System.out.println(command);
		String type = "offreb";
		return getResult(command,type);
	}
	
	public String getTeam() {
		return team;
	}
	int getTime(String type) throws Exception{
		switch(type) {
		case "Shot":
			time = (int) generateRandomNormal(shotMean, shotStdev);
		case "Three":
			time = (int) generateRandomNormal(threeMean,threeStdev);
		case "Rebound":
			time = (int) generateRandomNormal(reboundMean,reboundStdev);
		case "Turnover":
			time = (int) generateRandomNormal(turnoverMean,turnoverStdev);
		case "Steal":
			time = (int) generateRandomNormal(stealMean,stealStdev);
		case "Foul":
			time = (int) generateRandomNormal(foulMean,foulStdev);
		case "Shooting Foul":
			time = (int) generateRandomNormal(shootingFoulMean,shootingFoulStdev);
		}
		return time;
	}
	void getTimeVar() throws Exception{
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		Statement statement = connection.createStatement();
		
		String command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "(action1 ~ 'shot' OR action1 ~ 'Shot')"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
		//System.out.println(command);
			shotMean = getResult(command, "mean",statement);
			shotStdev = getResult(command, "stdev",statement);
			
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action1 = '3pt Shot'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
			//System.out.println(command);
			threeMean = getResult(command, "mean",statement);
			threeStdev = getResult(command, "stdev",statement);
			
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action1 = 'Rebound'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
			//System.out.println(command);
			reboundMean = getResult(command, "mean",statement);
			reboundStdev = getResult(command, "stdev",statement);
			
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action1  = 'Turnover'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
			//System.out.println(command);
			turnoverMean = getResult(command,"mean",statement);
			turnoverStdev = getResult(command,"stdev",statement);
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action2 = 'Steal'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
			//System.out.println(command);
			stealMean = getResult(command,"mean",statement);
			stealStdev = getResult(command,"stdev",statement);
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action1 = 'Foul'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);
			//System.out.println(command);
			foulMean = getResult(command,"mean",statement);
			foulStdev = getResult(command,"stdev",statement);
			command = String.format("SELECT AVG(time) as mean,STDDEV(time) as stdev FROM pbp "
					+ "WHERE team1 = '%s' AND "
					+ "action1 = 'Shooting Foul'"
					+ "%s"
					+"%s"
					+ ";", team,exclude,include);	
			//System.out.println(command);
			shootingFoulMean = getResult(command,"mean",statement);
			shootingFoulStdev = getResult(command,"stdev",statement);
	
	}
	float generateRandomNormal(float mean, float std) {
		boolean sub;
		float a=1;
		float b=2*mean;
		float c = mean*mean;
		Random randNumber = new Random();
		c  -= java.lang.Math.log(randNumber.nextFloat()*(java.lang.Math.sqrt(2*java.lang.Math.PI*(std*std)))) * 2*(std*std);
		if(randNumber.nextFloat()<.5) {
			sub=true;
		}else {
			sub = false;
		}
		return quadraticFormula(a,b,c,sub);		
	}
	
	float quadraticFormula(float a, float b, float c,boolean sub){
		return (float) (java.lang.Math.abs((-b+java.lang.Math.sqrt(java.lang.Math.pow(b,2)-(4*c)))/2)+(float) java.lang.Math.abs((-b-java.lang.Math.sqrt(java.lang.Math.pow(b,2)-(4*c)))/2))/2;		
			
		
	}
	/*
	 * We are going to create a query that fetches how many days rest a team has had
	 * 
	 * SELECT gameDate,LAG(gameDate) OVER (ORDER BY gameDate) 
	 * FROM (
	 * SELECT DISTINCT TO_DATE(CAST(date AS VARCHAR(15)),'YYYYMMDD') 
	 * gameDate 
	 * FROM pbp 
	 * WHERE team1 = '%s') dates;
	 * 
	 */
	
	
	
	
	

}
