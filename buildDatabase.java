import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.net.*;
import org.json.*;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
	public static JSONObject getMultipleGames(int a, int b, int c,boolean d){
		System.out.println("Intitializing the get multiple games method");
		try{
			String id = Integer.toString(b);
			while(id.length() < 5){
				id = "0" + id;
				System.out.println(id);
			}
			
			return quarterTableValues(a,id,c);
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
	public static void runPlaybPlay(int a, int b, int c, boolean d){
		try{
			JSONArray test = getMultipleGames(1017,1,1,true).getJSONArray("plays");
			int totalPoints = 0;
			int quarter = 1;
			float playTime = 0;
			String player ="";
			String text = "";
			int gameID = 1;
			int date = 1017;
			int playID = 0;
			String team1 = null;
			String player1  = null;
			String action1  = null;
			String player2 = null;
			String team2 = null;
			String action2 = null;
			int points = 0;
			float[] times = {720,720};
			for(int i= 0 ; i<test.length(); i++){
				playID=i;
				team1 = null;
				player1  = null;
				action1  = null;
				player2 = null;
				team2 = null;
				action2 = null;
				points = 0;
				JSONObject testDict = test.getJSONObject(i);
				String t= (String)(testDict.get("clock"));
				System.out.println(t);
				float newTime = 0;
				if(t.contains(".")){
					System.out.println(t);
					String seconds = Match(":\\d+\\.",t);
					System.out.println(seconds);
					seconds = seconds.substring(1,seconds.length()-1);
					float secondsInt = Float.parseFloat(seconds); 
					newTime = secondsInt + Float.parseFloat(Match("^\\d+",t))*60+(Float.parseFloat(Match("\\d+$",t))/10);
				}else{
					newTime = Float.parseFloat(Match("^\\d+",t))*60+Float.parseFloat(Match("\\d+$",t));
				}
				times[0] = times[1];
				times[1] = newTime;
				playTime = times[0]-times[1];
				text  = (String) (testDict.get("description"));
					if(playTime<0){
						System.out.println("There was an error for this play \n" + text + " " + Float.toString(times[0]) + " " +  Float.toString(times[1]));
					}
					String patternString = "[A-Z]{3}";
					team1 = Match(patternString, text);
					String patternString2 = "[A-Z][a-z]+";
					player = Match(patternString2,text);
					if(text.contains("Rebound")){
						player1 = generateRebounder(text);
						action1 = "Rebound";
					}
					
					if(text.contains("Turnover")){
						if(player.equals("Team")){
							action1 = "Turnover";
						}else if(text.contains("Steal")){
							player1 = generateTurnover(text);
							action1 = "Turnover";
							player2 = generateSteal(text);
							action2 = "Steal";
							team2 = "other team";
						}
					}
					if(text.contains("Timeout")){
						action1 = "Timeout";
					}
					if(text.contains("Shot") &&  text.contains("Shot Clock") == false){
						player1 = player;
						action1 = generateShotDesc(text);
						System.out.println(action1);
						if(action1.contains(player1)){
							action1 = action1.replace(player1,"");
							action1 = action1.substring(1,action1.length());
						}
						if(text.contains("Made")){
							if(text.contains("Assist")){
								action2 = "assist";
								player2 = generateAssister(text);
							}
							if(text.contains("3pt")){
								totalPoints+=3;
								points = 3;
							}else{
								totalPoints+=2;
								points = 2 ;
							}
						}
					}
					
					if(text.contains("Substitution")){
						String [] subs = generateSub(text);
						player1 = subs[0];
						action1 = "Subbed out";
						player2= subs[1];
						action2 = "Subbed in";
					}
					
					
					if(text.contains("Foul")){
						player1 = player;
						action1 = "Foul";
					}
					
					if(text.contains("Free Throw")){
						player1 = player;
						if (text.contains("Missed") == false){
							totalPoints+=1;
							action1 = "Made Free Throw";
							points = 1;
						}else{
							action1 = "Missed Free Throw";
						}
					}

					
				
			
			
			System.out.println(gameID + "\n" + playID + "\n" + player1 + "\n" + action1 + "\n" + team1 + "\n" + player2 + "\n" + action2 + "\n" + Float.toString(points) + "\n" + Float.toString(playTime) + "\n \n");

	
		}
		
		}catch(Exception e){
			System.out.println("caught");
		}
	}
	public static String Match(String patternString, String text){
		Pattern pattern  = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(text);
		if(matcher.find() == true){
			return matcher.group(0);
		}else{
			return null;
		}
	}
	public static void createTable(){
		try{
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
			System.out.println("Connected");
			Statement statement = connection.createStatement();
			String command = "CREATE TABLE pbp( \n" +
			"playID integer PRIMARY KEY NOT NULL, \n" +
			"gameID integer NOT NULL, \n" +
			"date integer, \n"+
			"team1 char(50), \n" +
			"player1 char(50), \n" +
			"action1 char(50), \n" +
			"player2 char(50), \n" +
			"team2 char(50), \n" +
			"action2 char(50), \n" +
			"points integer \n" +
			");";
			command = "SELECT * FROM pbp;";
			command = "ALTER TABLE pbp ADD COLUMN quarter integer;";
			System.out.println(command);
			statement.executeQuery(command);
			System.out.println("Success");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String generateShotDesc(String play){
		try{
			String result = Match("([A-za-z1-9]+\\s)+Shot:",play);
			result =Match("(\\s[A-Za-z1-9]+)+",result);
			result = result.substring(1,result.length());
			return result;
		}catch(NullPointerException e){
			//System.out.println("null pointer exception");
			return null;
		}
	}	
	public static String generateAssister(String play){
		try{
			String pattern = "Assist: [A-Za-z]+";
			String result = Match(pattern,play);
			pattern  = "([A-Za-z]+)$";
			result  = Match(pattern,result);
			return result;
		}catch(NullPointerException e){
			return null;
		}
	}
	public static String generateRebounder(String play){
		try{
			String pattern = "[A-Za-z]+\\sRebound";
			String result = Match(pattern,play);
			pattern  = "^[A-Za-z]+";
			result  = Match(pattern,result);
			return result;
		}catch(NullPointerException e){
			return null;
		}
	}
	public static String[] generateSub(String play){
		try{
			String pattern = "[A-Za-z]+\\sSubstitution\\sreplaced\\sby\\s[A-Za-z]+";
			String[] result = new String[2];
			String subs = Match(pattern,play);
			pattern = "^[A-Za-z]+";
			result[0] = Match(pattern,subs);
			pattern  = "[A-Za-z]+$";
			result[1]  = Match(pattern,subs);
			System.out.println("The first element is the player to be subbed out and the second is the player coming in");
			return result;
		}catch(NullPointerException e){
			return null;
		}
	}
	
	public static String generateTurnover(String play){
		try{
			String pattern = "[A-Za-z]+\\sTurnover";
			String result = Match(pattern,play);
			pattern = "^[A-Za-z]+";
			result = Match(pattern, result);
			return result;
		}catch(NullPointerException e){
			return null;
		}
	}
	public static String generateSteal(String play){
		try{
			String pattern = "Steal:[A-Za-z]+";
			String result = Match(pattern,play);
			pattern = "[A-Za-z]+$";
			result = Match(pattern, result);
			return result;
		}catch(NullPointerException e){
			return null;
		}		
	}
	public static void main(String args[]){
		try{
			buildDatabase a = new buildDatabase();
			//a.createTable();
			a.runPlaybPlay(0,0,0,true);
		}catch(Exception e){
			e.printStackTrace();
		}
}
}
