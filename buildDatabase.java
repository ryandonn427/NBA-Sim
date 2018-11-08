
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
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class buildDatabase {
	static int date = 0;
	static int year = 0;
	private static ArrayList <String> queries = new ArrayList <String>();
	static String[] teams= new String[2];
	public buildDatabase(int date,int year){
		this.date = date;
		this.year = year;
		
	}
	public static void createTeams() throws Exception{
		String url = "https://www.nba.com/players/active_players.json";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		System.out.println("Connected");
		Statement statement = connection.createStatement();
		String command = "CREATE TABLE players( \n" +
		"playerid integer PRIMARY KEY NOT NULL, \n" +
		"firstName char(50), \n" +
		"lastName char(50), \n"+
		"team char(50) \n" +
		");";

		statement.executeUpdate(command);
		System.out.println("Success");
		JSONArray myResponse = new JSONArray(response.toString());
		String firstName = "";
		String lastName = "";
		String team = "";
		int playerid = 0;
		
		for(int i=0; i<myResponse.length();i++){

			JSONObject player = myResponse.getJSONObject(i);
			firstName = (String) (player.get("firstName"));
			lastName = (String) (player.get("lastName"));
			playerid = Integer.parseInt((String)(player.get("personId")));
			team = (String) (player.getJSONObject("teamData").get("tricode"));
			if(firstName.contains("'")){
				firstName = firstName.replace("'","''");
			}
			if(lastName.contains("'")){
				lastName = lastName.replace("'","''");
			}
			command = String.format("INSERT INTO players(firstname,lastname,playerid,team) \n"+
			"VALUES \n" +
			"('%s','%s',%d,'%s');",firstName,lastName,playerid,team);
			System.out.println(command);
			statement.executeUpdate(command);
		}
	}
	
	public static void test() throws Exception{
		String url = "https://stats.nba.com/stats/playerdashboardbyyearoveryear?DateFrom=&DateTo=&GameSegment=&LastNGames=0&LeagueID=00&Location=&MeasureType=Advanced&Month=0&OpponentTeamID=0&Outcome=&PORound=0&PaceAdjust=N&PerMode=PerGame&Period=0&PlayerID=201166&PlusMinus=N&Rank=N&Season=2017-18&SeasonSegment=&SeasonType=Regular+Season&ShotClockRange=&Split=yoy&VsConference=&VsDivision=";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		System.out.println(con.getResponseCode());
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("done");
	}
	public  static JSONObject quarterTableValues(String id, int quarter) throws Exception{
		String url = String.format("https://data.nba.net/prod/v1/%d/00%d%s_pbp_%d.json", date,year,id,quarter);
		//System.out.println(url);
		URL obj = new URL(url);
		HttpURLConnection con  = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		JSONObject myResponse = new JSONObject(response.toString());
		return myResponse;
	}

	public static void generateTeams(int id) throws Exception{
		String idString = Integer.toString(id);
		while(idString.length() < 5){
			idString = "0" + idString;
		}
		String url = String.format("https://data.nba.net/prod/v1/%d/00%d%s_mini_boxscore.json",date,year,idString);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		JSONObject myResponse = new JSONObject(response.toString());
		JSONObject test = myResponse.getJSONObject("basicGameData");
		JSONObject home = test.getJSONObject("hTeam");
		JSONObject away = test.getJSONObject("vTeam");
		teams[0] = (String) (away.get("triCode"));
		teams[1] = (String) (home.get("triCode"));
		return;
	}
	public static JSONObject getMultipleGames(int b, int c,boolean d){
		//System.out.println("Intitializing the get multiple games method");
		try{
			String id = Integer.toString(b);
			while(id.length() < 5){
				id = "0" + id;
			}
			
			return quarterTableValues(id,c);
		}catch (Exception e){
			//System.out.println("Didn't work, trying next day");
			//(date);
			if(d == true){
				//System.out.println(a);
				return getMultipleGames(b,1,false) ;
			}else{
				return null;
			}
		}
	}
	public static boolean runPlaybPlay(int inputGame, int inputQuarter, boolean d){
		boolean trial = false;
		//System.out.println(inputDate);
		try{
			//inputDate = 1017 and inputGame/inputQuarter =1 and boolean d = true
			JSONArray test = getMultipleGames(inputGame,inputQuarter,d).getJSONArray("plays");	
			trial = true;
			int quarter = inputQuarter;
			float playTime = 0;
			String player ="";
			String text = "";
			int gameID = inputGame;
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
				//System.out.println(t);
				float newTime = 0;
				if(t.contains(".")){
					//System.out.println(t);
					String seconds = Match(":\\d+\\.",t);
					//System.out.println(seconds);
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
				//System.out.println(text);
					if(playTime<0){
						System.out.println("There was an error for this play \n" + text + " " + Float.toString(times[0]) + " " +  Float.toString(times[1]));
					}
					String patternString = "[A-Z]{3}";
					team1 = Match(patternString, text);
					
					
					
					String patternString2 = "[A-Z]{1}[a-z]+[A-Z]*[a-z]+";
					player = Match(patternString2,text);
					player = player.toLowerCase();
					
					player = Character.toString(player.charAt(0)).toUpperCase() + player.substring(1,player.length());
					
					if(text.contains("Rebound")){
						player1 = generateRebounder(text);
						action1 = "Rebound";
					}else if(text.contains("Stoppage")){
						action1 = "Referee Stoppage";
					}else if(text.contains("Turnover") || text.contains("Violation")){
						if(player.equals("Team")){
							action1 = "Turnover";
						}else if(text.contains("Steal")){
							player1 = generateTurnover(text);
							action1 = "Turnover";
							player2 = generateSteal(text);
							action2 = "Steal";
							if(team1.equals(teams[0])){
								team2 = teams[1];
							}else if(team1.equals(teams[1])){
								team2 = teams[0];
							}
							
						}else{
							player1 = player;
							action1 = "Turnover";
						}
					
					}else if(text.contains("Timeout")){
						action1 = "Timeout";
					}else if(text.contains("Start Period")){
						action1 = "Start Period";
					}else if(text.contains("End Period")){
						action1 = "End Period";
					}
					if(text.contains("shot")){
						text  = text.replace("shot","Shot");
					}
					if(text.contains("Shot")){
						
						if(text.contains("Shot Clock") == true){
							action1 = "Shot Clock Turnover";
						}else if(player.equals("Shot")){
							//System.out.println("player1 was never set");
						
						}else{
							player1 = player;
							action1 = generateShotDesc(text);
							//System.out.println(player1);
							//System.out.println(action1.contains(player1));
							if(action1.contains(player1)){
								action1 = action1.replace(player1,"");
							//	System.out.println(action1);
								action1 = action1.substring(1,action1.length());
							}
							if(text.contains("Block")){
								player2 = generateBlock(text);
								action2 = "Blocked Shot";
								if(team1.equals(teams[0])){
									team2 = teams[1];
								}else if(team1.equals(teams[1])){
									team2 = teams[0];
								}
							}else if(text.contains("Made")){
								if(text.contains("Assist")){
									action2 = "assist";
									player2 = generateAssister(text);
									team2 = team1;
								}
								if(text.contains("3pt")){			
									points = 3;
								}else{
									points = 2 ;
								}
															
							}
						}
						
					}
					
					if(text.contains("Substitution")){
						String [] subs = generateSub(text);
						//System.out.println(subs[1]);
						player1 = subs[0];
						action1 = "Subbed out";
						player2= subs[1];
						action2 = "Subbed in";
						team2 = team1;
					}
					
					
					if(text.contains("Foul")){
						if(text.contains("Shooting")&&text.contains("Foul")) {
							player1 = player;
							action1 = "Shooting Foul";
						}else {
							player1 = player;
							action1 = "Foul";
						}	
					}else if(text.contains("Technical")){
						player1 = player;
						action1 = "Technical";
					}else if(text.contains("Jump Ball")){
						action1 = "Jump Ball";
					}
					
					if(text.contains("Free Throw")){
						player1 = player;
						if (text.contains("Missed") == false){
							
							action1 = "Made Free Throw";
							points = 1;
						}else{
							action1 = "Missed Free Throw";
						}
					}


			String query = String.format("INSERT INTO pbp(playID, gameID, date, team1, player1, action1, player2, team2, action2, points, time,quarter)  \n" + 
			"VALUES \n" +
			"(%d, %d,%d, '%s', '%s', '%s', '%s', '%s', '%s', %d, %.1f, %d);", playID, gameID,date, team1, player1, action1, player2, team2, action2, points, playTime, quarter);

			queries.add(query);
			System.out.println(query);
	
		}
			
		}catch(Exception e){
			//e.printStackTrace();
			trial = false;
		}
		return trial;
	}
	public static void generateQuery() throws Exception{
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
		Statement statement = connection.createStatement();					
		for(String command:queries) {
			statement.executeUpdate(command);	
		}
		queries.clear();
		
		
	}
	
	public static boolean runMultScrapes(int inputGame,int quarter){
		boolean trial = false;
	//	System.out.println(inputDate);
		trial = runPlaybPlay(inputGame, quarter,true);		
	//	System.out.println(trial);
			if(trial == true){	
				if(quarter<4){
					quarter++;
					//   System.out.println("Press \"ENTER\" to continue...");
   					//Scanner scanner = new Scanner(System.in);
					//   scanner.nextLine();
					//System.out.println(inputDate);
					
					return runMultScrapes(inputGame,quarter);
				}
			}else{
				
				//System.out.println(inputDate);
				
				try{
					increaseDate(date);
					generateTeams(inputGame);
				}catch(Exception e){
					//e.printStackTrace();
					return false;
				}
				return runMultScrapes(inputGame, quarter);
				
			}
		return false;
					
	}
	public static boolean runMultScrapes(int inputGame){
		return runMultScrapes(inputGame,1);
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
			String command = "CREATE TABLE players( \n" +
			"playerid integer PRIMARY KEY NOT NULL, \n" +
			"firstName char(50), \n" +
			"lastName char(50), \n"+
			"team char(50) \n" +
			");";

			System.out.println(command);
			statement.executeQuery(command);
			System.out.println("Success");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String generateShotDesc(String play){
		try{
			String result = Match("([A-za-z1-9]+\\s)*+Shot:",play);
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
			String pattern = "[A-Za-z]+\\s*[A-Za-z\\.]*\\sSubstitution\\sreplaced\\sby(\\s[A-Za-z]+\\s*[A-Za-z\\.]*)*";
			String[] result = new String[2];
			String subs = Match(pattern,play);
			pattern = "^[A-Za-z]+";
			result[0] = Match(pattern,subs);
			pattern  = "[A-Za-z]+$";
			result[1]  = Match(pattern,subs);
			//System.out.println("The first element is the player to be subbed out and the second is the player coming in");
			return result;
		}catch(NullPointerException e){
			System.out.println("This got messed up");
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
	public static String generateBlock(String play){
		try{
			String pattern = "Block:\\s[A-Z]{1}[a-z]+[A-Z]*[a-z]+";
			String result = Match(pattern,play);
			pattern = "[A-Z]{1}[a-z]+[A-Z]*[a-z]+$";
			result = Match(pattern, result);
			return result;
		}catch(NullPointerException e){
			return null;
		}		
	}
	public static void increaseDate(int enterDate){
		String dateString = Integer.toString(enterDate);
		String yearString = dateString.substring(0,4);
		String month = dateString.substring(4,6);
		String day = dateString.substring(6,dateString.length());
		int dayInt = Integer.parseInt(day);
		int monthInt = Integer.parseInt(month);
		int yearInt = Integer.parseInt(yearString);
		dayInt++;
		if(dayInt>31){
			dayInt=1;
			monthInt++;
			if(monthInt > 12){
				monthInt = 1;
				yearInt++;
			}
		}
		yearString = Integer.toString(yearInt);
		month = Integer.toString(monthInt);
		day = Integer.toString(dayInt);
		while(month.length()<2){
			month = "0" + month;
		}
		while(day.length()<2){
			day = "0" + day;
		}
		String result = yearString + month + day;
		date = Integer.parseInt(result);
	}
	void update() throws Exception{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime now = LocalDateTime.now();
		int today = Integer.parseInt(dtf.format(now));
		queries.add("DELETE FROM pbp;");
		generateQuery();
		int i = 1;
		while(date<= today) {
			runMultScrapes(i);
			generateQuery();
			i++;
		}
	}
	
	public static void main(String args[]){
		try{
			
			buildDatabase a = new buildDatabase(20181016,218);
			a.update();
			System.out.println("done");
			

	
		}catch(Exception e){
			//e.printStackTrace();
		}
}
}
