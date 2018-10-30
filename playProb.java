
import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.json.*;
import java.net.*;

public class playProb{
	 String name;
	 String team;
	// double steal;
	// double turnover;
	 double shot;
	// double typeShot;
	// double miss;
	// double make;
	// double block;
	// double rebound;
	// double assist;
	 int total;
	//SELECT DISTINCT COUNT(gameID) FROM pbp
	//WHERE player1 = this.name OR player2 = this.name;
	
	public playProb(String name, String team){
		this.name = name;
		this.team = team;
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
	 public float generateTotal() throws SQLException{
		String command = String.format("SELECT DISTINCT COUNT(gameID) as total FROM pbp WHERE (player1 = '%s' ) OR (player2 = '%s');",name,team,name,team);
		ResultSet rs = generateQuery(command);
		int result=0;
		while(rs.next()){
			result = rs.getInt("total");
		}
		return (float)(result);
	 }
	 public float generateShot() throws SQLException{
		 String command = String.format("SELECT COUNT(action1) as shot FROM pbp WHERE  (player1 = '%s') AND (action1 ~ 'shot' OR action1 ~ 'Shot');",name,team);
		 ResultSet rs = generateQuery(command);
		 int result = 0;
		 while(rs.next()){
			 result = rs.getInt("shot");
		 }
		 this.shot = (float) (result);
		 return (float)(result);
	 }			
	 public float generateMadeShot() throws SQLException{
		 String command = String.format("SELECT COUNT(action1) as made FROM pbp WHERE (player1 = '%s') AND points>0;",name,team);
		 ResultSet rs = generateQuery(command);
		 int result = 0;
		 while(rs.next()){
			 result = rs.getInt("made");
		 }
		return (float)(result);
		}
	public float generateMissedShots() throws SQLException{
		float result = generateShot() - generateMadeShot();
		return result;
	}
	public float generateFreeThrows() throws SQLException{
		int result = 0;
		String command = String.format("SELECT COUNT(action1) as shot FROM pbp WHERE  (player1 = '%s') AND action1 ~ 'Free Throw';",name,team);
		ResultSet rs = generateQuery(command);
		while(rs.next()){
			result = rs.getInt("shot");
		}
		return (float)(result);
	}
	public void generateAll() throws SQLException{
		generateFreeThrows();
		generateMadeShot();
		generateMissedShots();
		generateShot();
		generateTotal();
	}
	public boolean checkName() throws SQLException{
		String command = String.format("SELECT player1,team1 FROM pbp WHERE player1 = '%s' GROUP BY player1,team1", name);
		ResultSet rs = generateQuery(command);
		int count = 0;
		while(rs.next()) {
			count++;
			System.out.println(rs.getString("player1"));
		}
		if(count>1) {
			System.out.println("This player does have a duplicate, please check the team");
			return false;
			
		}else {
			System.out.println("This player has no duplicate");
			return true;
		}
	}
	public static void main(String[] args){
		playProb a  = new playProb("Anderson", "CLE");
		try{
			System.out.println(a.generateMadeShot());
			System.out.println(a.checkName());
		
	}catch(Exception e){
		System.out.println("DIdn't work");
		e.printStackTrace();
	}
	}
}
