
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
	private int time;
		public teamProb(String team){
		this.team = team;
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

	public float getShots() throws SQLException{
		String command = String.format("SELECT COUNT(action1) as shot FROM pbp WHERE  (team1 = '%s') AND (action1 ~ 'shot' OR action1 ~ 'Shot');",team);
		String type = "shot";
		return getResult(command, type);
	}
	
	/*DECLARE a method called getTOV()
		Generate a SQL query that returns a float that tells us how often this team turns the ball over
	*/
	public float getTOV() throws SQLException{
		int result = 0;
		String command = String.format("SELECT COUNT(action1) as tov FROM pbp WHERE team1 = '%s' AND action1 = 'Turnover';", team);
		String type = "tov";
		return getResult(command,type);
		
	}
	/*
	DECLARE a method called getStoppage()
		Generate a SQL query that returns a float that tells us how often there is a stoppage
	*/
	public float getStoppage() throws SQLException{
		return getShots() - getFoul();
	}
	/*
	DECLARE a method called getFoul()
		Generate a SQL query that returns a float that tells us how often there is a foul
	*/
	public float getFoul() throws SQLException{
		String command = String.format("SELECT COUNT(action1) as foul FROM pbp WHERE team1 = '%s' AND action1 = 'Foul';", team);
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
		String command = String.format("SELECT COUNT(action1) as three FROM pbp WHERE team1 = '%s' AND action1 = '3pt Shot'",team);
		String type = "three";
		return getResult(command,type);
	}
	/*
	DECLARE a method called madeShot()
		Generate a SQL query that retuns a float that tells us how often a team makes a shot
	*/
	public float madeShot() throws SQLException{
		String command = String.format("SELECT COUNT(points) as made FROM pbp WHERE points>1 AND team1 = '%s';", team);
		String type = "made";
		return getResult(command,type);
	}
	
	public float madeThrees() throws SQLException{
		String command = String.format("SELECT COUNT(points) as made FROM pbp WHERE points>2 AND team1 = '%s';", team);
		String type = "made";
		return getResult(command,type);		
	}
	/*
	DECLARE a method called Steal()
		Generate a SQL query that returns a float that tells us how often a team steals given there is a turnover
	*/
	public float getSteal() throws SQLException{
		String command = String.format("SELECT count(action2) as steal FROM pbp WHERE team2 = '%s' and action2 = 'Steal';", team);
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
		String command = String.format("SELECT count(action1) as rebound FROM pbp WHERE team1 = '%s' and action1 = 'Rebound';", team);
		String type = "rebound";
		return getResult(command,type);
	}
	public float getTotalRebounds() throws SQLException{
		String command  = String.format("SELECT COUNT(action1) as rebounds FROM pbp WHERE gameId IN (SELECT DISTINCT GameId FROM pbp WHERE team1 = '%s') AND action1 = 'Rebound'",team);
		String type = "rebounds";
		return getResult(command,type);
	}
	public String getTeam() {
		return team;
	}

}
