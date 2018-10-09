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

			String player ="";
			int points = 0;
			String assist = "";
			String freeThrow = "";
			String result = "None";
			for(int i= 0 ; i<test.length(); i++){
				JSONObject testDict = test.getJSONObject(i);
				Iterator <String> playKeys = testDict.keys();
				while(playKeys.hasNext()){
					String playKey = playKeys.next();
					if(playKey.equals("description")){
						String text  = (String) (testDict.get(playKey));
						String patternString2 = "[A-Z][a-z]+";
						Pattern pattern2 = Pattern.compile(patternString2);
						Matcher matcher2 = pattern2.matcher(text);
						Boolean matches2 = matcher2.find();
						if(matches2 = true){
							player = matcher2.group(0);
							if(player.equals("Start")){
								player = "The game has started";
							}else if(player.equals("Jump")){
								player = "Jump Ball";
							}else if(player.equals("Team")){
								player = "Team Play";
							}else if (player.equals("Stoppage")){
								player = "Stoppage by the referee";
							}else if(player.equals("End")){
								player = "The quarter is over";
							}else if(player.equals("Instant")){
								player = "The referees are reviewing the play";
						}
					}
					//System.out.println(text);
					//print whether the team scored
					if(text.contains("Made")){
						if(text.contains("3pt")){
							totalPoints+=3;
							points = 3;
						}else{
							//System.out.println("Points were scored on this possession");
							totalPoints+=2;
							points = 2 ;
						}
					}else if(text.contains("Missed")){
						//System.out.println("A shot was taken but there were no points");
						points = 0;
					}
				
					if(text.contains("Assist")){
						//System.out.println("There was an assist");
						assist = "assist";
					}else{
						assist = "";
					}
					
					if(text.contains("Free Throw") && text.contains("Missed") == false){
						totalPoints+=1;
						freeThrow = "free throw";
						points = 1;
					}else{
						freeThrow = "";
					}
					//System.out.println(text);
					String patternString = "[A-Z]{3}";
					Pattern pattern = Pattern.compile(patternString);
					Matcher matcher = pattern.matcher(text);
					boolean matches = matcher.find();
					if(matches == true){
						result = matcher.group(0);
					}
					//System.out.println("The team with the ball is " + result);
				}else if(playKey.equals("clock")){
					//System.out.println("The time on the clock is : " + (String) (testDict.get(playKey)));
				}
				
			}
			System.out.println(player + " from " + result + " scored "+ points + " " + assist + " " + freeThrow + " " + totalPoints);
		}
		System.out.println("There were " + totalPoints + " points");
		
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
	
	public static void main(String args[]){
		Connection connection  = null;
		try{
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
			System.out.println("Connected");
			Statement statement = connection.createStatement();
			
		}catch(Exception e){
			e.printStackTrace();
		}
}
}
