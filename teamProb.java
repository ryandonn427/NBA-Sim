
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
	public teamProb(String team){
		this.team = team;
	}
    public void generatePlayers() throws Exception{
		Class.forName("org.postgresql.Driver");    	
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(String.format("SELECT * FROM players WHERE team = '%s'",team));
		
		while(results.next()) {
			players.add(new playProb(results.getString("lastname"),team));

		}
		System.out.println("I got this far");
		
	}
    public void generateStats() throws SQLException{
    	for(playProb player: players) {
    		System.out.println(player.name);
			player.generateAll();
			
    	}
    }
    public static void main(String[] args) throws Exception {
    	teamProb a = new teamProb("BKN");
    	a.generatePlayers();
    	a.generateStats();
    	
    	
    }
}
