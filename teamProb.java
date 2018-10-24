
import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
public class teamProb{
	ArrayList <playProb> players = new playProb(12);
	String team;
	public teamProb(String team){
		this.team = team;
	}
    public void generatePlayers(){
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("SELECT * FROM players WHERE team = '%s' team",team));
	}
}
