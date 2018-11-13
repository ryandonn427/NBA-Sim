
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
		public teamProb(String team,String homeAway){
		this.team = team;
		this.homeAway = homeAway;
	}
	
	//Start

	/*DECLARE a method called getShots()
		Generate a SQL query that returns a float that tells us how often this team shoot
	*/
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
	public float getResult(String command,String type) throws SQLException{
		int result = 0;
		ResultSet rs = generateQuery(command);
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
				+ " Free Throw';", 
				team,homeAway,team);
		String type = "freethrow";
		return getResult(command,type);
	}
	public float getShots() throws SQLException{
		String command = String.format("SELECT COUNT(action1) as shot FROM pbp "
				+ "WHERE  (team1 = '%s') AND %s = '%s' AND (action1 ~ 'shot' OR action1 ~ 'Shot');",
				team,homeAway,team);
		String type = "shot";
		return getResult(command, type);
	}
	
	/*DECLARE a method called getTOV()
		Generate a SQL query that returns a float that tells us how often this team turns the ball over
	*/
	public float getTOV() throws SQLException{
		int result = 0;
		String command = String.format("SELECT COUNT(action1) as tov FROM pbp "
				+ "WHERE team1 = '%s' AND action1 = 'Turnover' AND %s = '%s';", 
				team,homeAway,team);
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
				+ "Foul';", 
				homeAway,team,team);
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
				+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '3pt Shot'",
				homeAway,team,team);
		String type = "three";
		return getResult(command,type);
	}
	/*
	DECLARE a method called madeShot()
		Generate a SQL query that retuns a float that tells us how often a team makes a shot
	*/
	public float madeShot(String trend) throws SQLException{
		String command = String.format("SELECT COUNT(makes) as make FROM "
				+ "(SELECT action1,points, CASE "
				+ "WHEN points>1 AND lag(points) over (order by gameid,playid) >1 THEN 'OO' "
				+ "WHEN points>1 AND lag(points)over (order by gameid,playid)<1 THEN 'XO' "
				+ "WHEN points<1 AND lag(points)over (order by gameid,playid)<1 THEN 'XX'"
				+ "WHEN points<1 AND lag(points)over (order by gameid,playid)>1 THEN 'OX' "
				+ "END as makes  "
				+ "FROM pbp "
				+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
				+ "AND team1 = '%s' AND "
				+ "%s = '%s') trends "
				+ "WHERE makes ='%s';",team,homeAway,team,trend);
		String type = "make";
		return getResult(command,type);
	}
	
	public float madeThrees(String trend) throws SQLException{
		String command = String.format("SELECT COUNT(makes) as make FROM "
				+ "(SELECT action1,points, CASE "
				+ "WHEN points>2 AND lag(points) over (order by gameid,playid) >2 THEN 'OO' "
				+ "WHEN points>2 AND lag(points)over (order by gameid,playid)<1 THEN 'XO' "
				+ "WHEN points<1 AND lag(points)over (order by gameid,playid)<1 THEN 'XX'"
				+ "WHEN points<1 AND lag(points)over (order by gameid,playid)>2 THEN 'OX' "
				+ "END as makes  "
				+ "FROM pbp "
				+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
				+ "AND team1 = '%s' AND "
				+ "action1 = '3pt Shot' AND "
				+ "%s = '%s') trends "
				+ "WHERE makes ='%s';",team,homeAway,team,trend);
		String type = "make";
		return getResult(command,type);
	}
	/*
	DECLARE a method called Steal()
		Generate a SQL query that returns a float that tells us how often a team steals given there is a turnover
	*/
	public float getSteal() throws SQLException{
		String command = String.format("SELECT count(action2) as steal FROM pbp "
				+ "WHERE team2 = '%s' AND %s = '%s' AND action2 = 'Steal';"
				, team,homeAway,team);
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
				+ "WHERE team1 = '%s' and action1 = 'Rebound' AND %s = '%s';"
				, team,homeAway,team);
		String type = "rebound";
		return getResult(command,type);
	}
	public float getTotalRebounds() throws SQLException{
		String command  = String.format("SELECT COUNT(action1) as rebounds FROM pbp "
				+ "WHERE gameId IN (SELECT DISTINCT GameId FROM pbp WHERE team1 = '%s') "
				+ "AND action1 = 'Rebound' AND %s = '%s'",
				team,homeAway,team);
		String type = "rebounds";
		return getResult(command,type);
	}
	public float getOffensiveRebound() throws SQLException{
		String command = String.format("SELECT COUNT(rebound.action1) offreb "
				+ "FROM (SELECT action1,LAG(team1) OVER (ORDER BY gameid,playid) team,team1,home,away FROM pbp) "
				+ "AS rebound WHERE rebound.team = rebound.team1 AND rebound.team1 = '%s' AND "
				+ "action1 = 'Rebound' AND %s = '%s';",
				team,homeAway,team);
		String type = "offreb";
		return getResult(command,type);
	}
	
	public String getTeam() {
		return team;
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
