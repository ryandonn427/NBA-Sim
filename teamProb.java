
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
	
	teamProb(String team, String homeAway){
		this.team = team;
		this.homeAway = homeAway;
	}
	String getTeam() {
		return team;
	}
		private class AllQueries{
	
			private String[] names;
			private String[] queryStrings;
			/*
			 * Create a hashmap with the stat name as the key and
			 * another hashmap as the value
			 * The inner hashmap contains the specific cond as the key
			 * and the actual proportion as the value
			 */
			private HashMap<String,Float> results;
			private HashMap<String,Float> proportions;
			private HashMap<HashMap<String,Float>,Integer> weight; 
			private AllQueries(){
				results = new HashMap<String,Float>();
				proportions = new HashMap<String,Float>();
				weight = new HashMap<HashMap<String,Float>,Integer>();
			}
			
			/**
			 * This method is going to create lists for 
			 * both our variable names and our queries
			 */
			private void buildLists(String category) {


			names = new String[] {
						"freeThrowMade",
						"freeThrowMissed",
						"shot",
						"tov",
						"shootingFoul",
						"foul",
						"three",
						"shotsMade",
						"threesMade",
						"steals",
						"rebounds",
						"totalRebounds",
						"offensiveRebounds",
						"meanShotTime",
						"stdevShotTime",
						"meanThreeTime",
						"stdevThreeTime",
						"meanReboundTime",
						"stdevReboundTime",
						"meanTurnoverTime",
						"stdevTurnoverTime",
						"meanStealTime",
						"stdevStealTime",
						"meanFoulTime",
						"stdevFoulTime",
						"meanShootingFoulTime",
						"stdevShootingFoulTime",
						"homeAwayWeight"
						
				};
				queryStrings = new String[]{
						new String (String.format("SELECT COUNT(action1) as freeThrowMade,"
								+ "COUNT(DISTINCT gameid) freeThrowMade FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND action1 = '"
								+"Made"
								+ " Free Throw'"
								+";",
								team,homeAway,team)),
						new String (String.format("SELECT COUNT(action1) as freeThrowMissed,"
								+ "COUNT(DISTINCT gameid) hafreethrowc FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND action1 = '"
								+"Missed"
								+ " Free Throw'"
								+";",
								team,homeAway,team)),

						new String (String.format("SELECT COUNT(action1) as shot FROM pbp "
									+ "WHERE  (team1 = '%s') AND %s = '%s' AND (action1 ~ 'shot' OR action1 ~ 'Shot')"
									+ ";",
									team,homeAway,team)),
						
						new String (String.format("SELECT COUNT(action1) as tov FROM pbp "
									+ "WHERE team1 = '%s' AND action1 = 'Turnover' AND %s = '%s'"
									+ ";", 
									team,homeAway,team)),
						
						new String (String.format("SELECT COUNT(action1) as shootingFoul FROM pbp "
									+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '"
									+"Shooting"
									+ "Foul'"
									+ ";", 
									homeAway,team,team)),
						new String (String.format("SELECT COUNT(action1) as foul FROM pbp "
									+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '"
									+ "Foul'"
									+ ";", 
									homeAway,team,team)),

						new String (String.format("SELECT COUNT(action1) as three FROM pbp "
									+ "WHERE %s = '%s' AND team1 = '%s' AND action1 = '3pt Shot'"
									+ ";",
									homeAway,team,team)),
						new String(String.format("SELECT COUNT(action1) as shotsMade FROM"
								+ " pbp WHERE team1 = '%s' AND"
								+ " %s = '%s' AND"
								+ " (action1 ~'shot' OR action1~ 'Shot')"
								+ " AND points >1 "
								+ " AND points <3;", team,homeAway,team)),
						new String(String.format("SELECT COUNT(action1) as threesMade FROM"
								+ " pbp WHERE team1 = '%s' AND"
								+ " %s = '%s' AND"
								+ " (action1 ~'shot' OR action1 ~ 'Shot')"
								+ " AND points = 3;", team,homeAway,team)),
						
//						
//						
//						new String (String.format("SELECT COUNT(makes) as shotsMade FROM "
//									+ "(SELECT action1,points,gameid,"
//									+ "twoseq as makes "
//									+ "FROM pbp "
//									+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
//									+ "AND team1 = '%s' AND "
//									+ "%s = '%s') trends "
//									+ "WHERE makes ='XO'"
//									+ ";",
//									team,homeAway,team)),
//						
//						
//						new String (String.format("SELECT COUNT(makes) as threesMade FROM "
//									+ "(SELECT action1,points, gameid,"
//									+ "twoseq as makes "
//									+ "FROM pbp "
//									+ "WHERE (action1 ~'shot' OR action1 ~ 'Shot') "
//									+ "AND team1 = '%s' AND "
//									+ "action1 = '3pt Shot' AND "
//									+ "%s = 'XO') trends "
//									+ "WHERE makes ='%s'"
//									+ ";",
//									team,homeAway,team)),
//						
						new String (String.format("SELECT count(action2) as steals FROM pbp "
									+ "WHERE team2 = '%s' AND %s = '%s' AND action2 = 'Steal'"
									+ ";"
									, team,homeAway,team)),
						
						new String (String.format("SELECT count(action1) as rebounds "
									+ "FROM pbp "
									+ "WHERE team1 = '%s' and action1 = 'Rebound' AND %s = '%s'"
									+ ";"
									, team,homeAway,team)),
						
						new String (String.format("SELECT COUNT(action1) as totalRebounds FROM pbp "
									+ "WHERE gameId IN (SELECT DISTINCT GameId FROM pbp WHERE team1 = '%s') "
									+ "AND action1 = 'Rebound' AND %s = '%s'"
									+ ";",
									team,homeAway,team)),
						
						new String (String.format("SELECT COUNT(rebound.action1) offensiveRebounds "
									+ "FROM (SELECT action1,LAG(team1) OVER (ORDER BY gameid,playid) team,team1,home,away,gameid FROM pbp) "
									+ "AS rebound WHERE rebound.team = rebound.team1 AND rebound.team1 = '%s' AND "
									+ "action1 = 'Rebound' AND %s = '%s'"
									+ ";",
									team,homeAway,team)),
						
						
						
						new String (String.format("SELECT AVG(time) as meanShotTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "(action1 ~ 'shot' OR action1 ~ 'Shot')"
										+ ";", team)),
						
						new String (String.format("SELECT STDDEV(time) as stdevShotTime FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND "
								+ "(action1 ~ 'shot' OR action1 ~ 'Shot')"
								+ ";", team)),
						
						new String (String.format("SELECT AVG(time) as meanThreeTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = '3pt Shot'"
										+ ";", team)),
						
						new String (String.format("SELECT STDDEV(time) as stdevThreeTime FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND "
								+ "action1 = '3pt Shot'"
								+ ";", team)),
				
						new String (String.format("SELECT AVG(time) as meanReboundTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = 'Rebound'"
										+ ";", team)),
						
						new String (String.format("SELECT STDDEV(time) as stdevReboundTime FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND "
								+ "action1 = 'Rebound'"
								+ ";", team)),
					
				  		new String (String.format("SELECT AVG(time) as meanTurnoverTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1  = 'Turnover'"
										+ ";", team)),

				  		new String (String.format("SELECT STDDEV(time) as stdevTurnoverTime FROM pbp "
								+ "WHERE team1 = '%s' AND %s = '%s' AND "
								+ "action1  = 'Turnover'"
								+ ";", team)),

				  		
						new String (String.format("SELECT AVG(time) as meanStealTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action2 = 'Steal'"
										+ ";", team)),
						
						new String (String.format("SELECT STDDEV(time) as stdevStealTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action2 = 'Steal'"
										+ ";", team)),
						
						new String (String.format("SELECT AVG(time) as meanFoulTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = 'Foul'"
										+ ";", team)),

						new String (String.format("SELECT STDDEV(time) as stdevFoulTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = 'Foul'"
										+ ";", team)),
						new String (String.format("SELECT AVG(time) as meanShootingFoulTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = 'Shooting Foul'"
										+ ";", team))
						,
						new String (String.format("SELECT STDDEV(time) as stdevShootingFoulTime FROM pbp "
										+ "WHERE team1 = '%s' AND %s = '%s' AND "
										+ "action1 = 'Shooting Foul'"
										+ ";", team,homeAway,team)),
						new String (String.format("SELECT DISTINCT COUNT(gameid) as homeAwayWeights FROM pbp"
								+ " WHERE team1 = '%s' AND %s = '%s';", team, homeAway,team))
						
                      };
					
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

						
						
				
			
			
			
			/*This method is going to run through all the queries,
			 * execute them, and build our hashmap
			 */
			 
			private void generateProportions() throws Exception{
				proportions.put("shot",(float)results.get("shot")/(results.get("shot")+results.get("foul")+results.get("tov")));
				proportions.put("threePointShot", (float)results.get("three")/(results.get("three")+results.get("shot")));
				proportions.put("tov", (float)results.get("tov")/(results.get("shot")+results.get("foul")+results.get("tov")));
				proportions.put("steal", (float)results.get("steals")/(results.get("tov")+results.get("steals")));
				proportions.put("rebound", (float)results.get("rebounds")+(results.get("rebounds")+results.get("totalRebounds")));
				proportions.put("offensiveRebound",(float) results.get("offensiveRebounds"));
				proportions.put("foul", (float)results.get("foul")/(results.get("tov")+results.get("shot")+results.get("foul")));
				proportions.put("shootingFoul", (float)results.get("shootingFoul")/(results.get("foul")+results.get("shootingFoul")));
				proportions.put("freethrow", (float)results.get("freeThrowMade")/(results.get("freeThrowMade")+results.get("freeThrowMissed")));
				proportions.put("made", (float)results.get("threesMade")/results.get("shot"));
				proportions.put("threesMade", (float)results.get("threesMade")/results.get("three"));
				proportions.put("offensiveRebounds", results.get("offensiveRebounds")/results.get("totalRebounds"));
				proportions.put("rebounds", results.get("rebounds")/results.get("totalRebounds"));

			}
			private void generateQuery() throws Exception{
				Class.forName("org.postgresql.Driver");
				Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/nba","postgres","baseball");
				Statement statement = connection.createStatement();
				
				for(int i =0 ; i<queryStrings.length;i++) {
					ResultSet rs = statement.executeQuery(queryStrings[i]);
					while(rs.next()) {
//						System.out.println(rs.getFloat(names[i]));
						results.put(names[i], rs.getFloat(names[i]));

					}
				}
				System.out.println("The queries have been executed");

			}
			

		
		
//	//Start
//
//	/*DECLARE a method called getShots()
//		Generate a SQL query that returns a float that tells us how often this team shoot
//	*/
//	/*
//		SELECT gameid,dates-datespassed FROM 
//		(SELECT *,LAG(dates) OVER (ORDER BY dates) as datespassed 
//				FROM (SELECT DISTINCT gameid,TO_DATE(CAST(date AS varchar),'YYYYMMDD') as dates 
//						FROM pbp WHERE team1  = 'BKN' 
//						ORDER BY dates) d) m;
//	/*
//	 * 
//	 */
//	public void generateExclusions() {
//		System.out.println(includeExclude);
//		if(inc==false) {
//			exclude = String.format(" AND gameid IN "
//				+ "(SELECT DISTINCT gameid FROM pbp "
//				+ "WHERE team1 = '%s' "
//				+ "EXCEPT "
//				+ "SELECT DISTINCT gameid FROM pbp "
//				+ "WHERE team1 = '%s' AND player1 = '%s')", team,team,includeExclude);
//			include="";
//		}else {
//			include = String.format(" AND gameid IN "
//					+ "(SELECT DISTINCT gameid FROM pbp "
//					+ "WHERE team1 = '%s' AND player1 = '%s')", team,includeExclude);
//			exclude="";
//		}
//		
//	}
//	public void generateExclusions(String player) {
//		System.out.println(includeExclude);
//		if(inc==false) {
//			exclude = String.format(" AND gameid IN "
//				+ "(SELECT DISTINCT gameid FROM pbp "
//				+ "WHERE team1 = '%s' "
//				+ "EXCEPT "
//				+ "SELECT DISTINCT gameid FROM pbp "
//				+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,team,includeExclude,player);
//			include="";
//		}else {
//			include = String.format(" AND gameid IN "
//					+ "(SELECT DISTINCT gameid FROM pbp "
//					+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,includeExclude,player);
//			exclude="";
//		}
//	
//		
//	}
//	
	public String getTeam() {
		return team;
	}
	int getTime(String type) throws Exception{
		int timeElapsed = 0;
//		System.out.println(results.get("meanShotTime"));
		switch(type) {
		case "Shot":
			timeElapsed= (int)generateRandomNormal(results.get("meanShotTime"), results.get("stdevShotTime"));
		case "Three":
			timeElapsed= (int)generateRandomNormal(results.get("meanThreeTime"),results.get("stdevThreeTime"));
		case "Rebound":
			timeElapsed= (int)generateRandomNormal(results.get("meanReboundTime"),results.get("stdevReboundTime"));
		case "Turnover":
			timeElapsed= (int)generateRandomNormal(results.get("meanTurnoverTime"),results.get("stdevTurnoverTime"));
		case "Steal":
			timeElapsed= (int)generateRandomNormal(results.get("meanStealTime"),results.get("stdevStealTime"));
		case "Foul":
			timeElapsed= (int)generateRandomNormal(results.get("meanFoulTime"),results.get("stdevFoulTime"));
		case "Shooting Foul":
			timeElapsed= (int)generateRandomNormal(results.get("meanShootingFoulTime"),results.get("stdevShootingFoulTime"));
		}
		return timeElapsed;
	}

	float generateRandomNormal(float mean, float std) {
		boolean sub;
		float a=1;
		float b=2*mean;
		float c = mean*mean;
		Random randNumber = new Random();
		c  -= java.lang.Math.log(randNumber.nextFloat()*(java.lang.Math.sqrt(2*java.lang.Math.PI*(std*std)))) * 2*(std*std);
		if(randNumber.nextFloat()<.5) {
			sub=true;
		}else {
			sub = false;
		}
		return quadraticFormula(a,b,c,sub);		
	}
	
	float quadraticFormula(float a, float b, float c,boolean sub){
		return (float) (java.lang.Math.abs((-b+java.lang.Math.sqrt(java.lang.Math.pow(b,2)-(4*c)))/2)+(float) java.lang.Math.abs((-b-java.lang.Math.sqrt(java.lang.Math.pow(b,2)-(4*c)))/2))/2;		
			
		
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
		
		private AllQueries queryGen;
	public HashMap callQueryGenerator() throws Exception{
		queryGen = new AllQueries();
		queryGen.buildLists("ok");
		queryGen.generateQuery();
		queryGen.generateProportions();
		return queryGen.proportions;
	}
	public float getTime(String type) throws Exception{
//		System.out.println(queryGen.getTime(type));
		return queryGen.getTime(type);
	}
	
	
	}

	
	


