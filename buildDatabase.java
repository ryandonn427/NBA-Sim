import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.net.*;
import org.json.JSONObject;
import java.sql.Statement;
import java.util.*;
public class buildDatabase {
	public  static JSONObject quarterTableValues(int date, String id, int quarter) throws Exception{
		//v1/#### is the date and 00217 is the year and 00004 is the id of the game
		//int date = 1017;
		//int id = 1;
		//int quarter=1;
		String url = String.format("https://data.nba.net/prod/v1/2017%d/00217%s_pbp_%d.json", date,id,quarter);
		System.out.println(url);
		URL obj = new URL(url);
		System.out.println("created the new URL object");
		HttpURLConnection con  = (HttpURLConnection) obj.openConnection();
		System.out.println("Created the connection");
		con.setRequestMethod("GET");
		System.out.println("Sent the GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		System.out.println("Set the user agent");
		int responseCode = con.getResponseCode();
		System.out.println("Got the response code");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		System.out.println("Got the BufferedReader");
		String inputLine;
		StringBuffer response = new StringBuffer();
		System.out.println("Got up to the read line step");
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		JSONObject myResponse = new JSONObject(response.toString());
		return myResponse;
	}
	public static Object getMultipleGames(int a, int b, int c,boolean d){
		System.out.println("Intitializing the get multiple games method");
		try{
			String id = Integer.toString(b);
			while(id.length() < 5){
				id = "0" + id;
				System.out.println(id);
			}
			
			return quarterTableValues(a,id,c).get("plays");
		}catch (Exception e){
			System.out.println("Didn't work, trying next day");
			a++;
			if(d == true){
				return getMultipleGames(a,b,1,false) ;
			}else{
				return null;
			}
		}
	}
	
	public static void main(String args[]){
		buildDatabase a = new buildDatabase();
		System.out.println(a.getMultipleGames(1017,1,1,true).getClass());
		
		
}
}