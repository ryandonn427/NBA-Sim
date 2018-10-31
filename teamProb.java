
import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
public class teamProb{
	ArrayList <playProb> players = new ArrayList <playProb>(12);
	String team;
	// double steal;
	// double turnover;
	 private double shot;
	// double typeShot;
	 private double miss;
	 private double make;
	// double block;
	// double rebound;
	// double assist;
	 private double total;
	public teamProb(String team){
		this.team = team;
	}
    public void generatePlayers() throws Exception{
		Class.forName("org.postgresql.Driver");    	
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		Statement statement = connection.createStatement();
		//I added a limit to the query so we can test more efficiently
		ResultSet results = statement.executeQuery(String.format("SELECT * FROM players WHERE team = '%s' LIMIT 3",team));
		
		while(results.next()) {
			players.add(new playProb(results.getString("lastname"),team));
			
		}
		System.out.println("I got this far");
		
	}
    public double generateTotals() throws SQLException{
    	ArrayList <Double> totals = new ArrayList <Double>();
    	double count = 0;
    	for(playProb player: players) {
			player.generateTotal();
			totals.add(player.getTotal());
			count+=player.getTotal();
    	}
    	total = count;
    	return count;
    }

    public double generateShots() throws SQLException{
    	ArrayList <Double> shots = new ArrayList <Double>();
    	double count = 0;
    	for(playProb player: players) {
			player.generateShot();
			shots.add(player.getShots());
			count+=player.getShots();
    	}
    	shot= count;
    	return count;
    }    
    
    public double generateMade() throws SQLException{
    	ArrayList <Double> mades = new ArrayList <Double>();
    	double count = 0;
    	for(playProb player: players) {
			player.generateMadeShot();
			mades.add(player.getMade());
			count+=player.getMade();
    	}
    	make= count;
    	return count;
    }
    
    public double generateMiss() throws SQLException{
    	ArrayList <Double> misses = new ArrayList <Double>();
    	double count = 0;
    	for(playProb player: players) {
			player.generateMissedShots();
			misses.add(player.getMissed());
			count+=player.getMissed();
    	}
    	miss= count;
    	return count;
    }    
    public static void main(String[] args) throws Exception {
    	teamProb a = new teamProb("BKN");
    	a.generatePlayers();
    	a.generateMiss();
    	System.out.println(a.miss);
    	
    }
}
