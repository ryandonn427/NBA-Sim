
import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class teamProb{ 

	private String include;
	private String exclude;
	private String team;
	private String homeAway;
	private float total;
	String[] mapIter;
	private HashMap <String,Float> totalProp;
	teamProb(String team, String homeAway){
		totalProp = new HashMap<String,Float>();
		this.team = team;
		this.homeAway = homeAway;
		total=0;

	}
	

			/**
			 * This method is going to create lists for 
			 * both our variable names and our queries
			 */					
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
						
						
						
						
						

					

						
						
				
			
			
			
			/*This method is going to run through all the queries,
			 * execute them, and build our hashmap
			 */
		
	public String getTeam() {
		return team;
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
	
	
		
		
		private ArrayList <Queries>  queryGen;


	
	public void combineHash(HashMap<String,Float>...hashes) {
		total=0;
		for(HashMap props : hashes) {
//			System.out.println(props.get("weight"));
			total +=(float)props.get("weight");
//			System.out.println(String.format("The total is %.5f", total));
		}

		for(HashMap props2 : hashes) {
			Set statKeys = props2.keySet();
			Iterator keyIter = statKeys.iterator();
			while(keyIter.hasNext()) {
				String weight = (String)keyIter.next();
				if(totalProp.containsKey(weight)) {
					float prev = (float) totalProp.get(weight);
					float nextNum = ((float) props2.get(weight)) * ((float)props2.get("weight")/(float)total);
//					System.out.println(String.format("Next is %.5f", nextNum));
					totalProp.put(weight,prev+nextNum);
//					System.out.println(String.format("The total probability is %.5f", totalProp.get("shot")));
					
				}else {
					float nextNum = ((float) props2.get(weight)) * ((float)props2.get("weight")/(float)total);
//					System.out.println(String.format(weight));
					totalProp.put(weight,nextNum);					
				}
			}
		}
	}
	public HashMap callQueryGenerator() throws Exception{
		queryGen = new ArrayList<Queries>();
		queryGen.add(new Queries(String.format("%s = '%s'", homeAway,team),homeAway,team));
		if(include!=null) {
//			System.out.println(include);
			queryGen.add(new Queries(include,homeAway,team));
		}
		if(exclude!=null) {
			queryGen.add(new Queries(exclude,homeAway,team));
//			System.out.println(exclude);
		}
		for(Queries query: queryGen) {
			query.buildNames();
			query.createQueries();
			query.generateQuery();
			query.generateProportions();
		}
		System.out.println(queryGen.size());
		if(queryGen.size()==2) {
			combineHash(queryGen.get(0).getProportions(),queryGen.get(1).getProportions());
		}else if(queryGen.size()==3) {
			combineHash(queryGen.get(0).getProportions(),queryGen.get(1).getProportions(),queryGen.get(2).getProportions());
		}
		else {
			combineHash(queryGen.get(0).getProportions());
		}
		return totalProp;
		
	}
	public float getTime(String type) throws Exception{
//		System.out.println(queryGen.getTime(type));
		return queryGen.get(0).getTime(type);
	}
	

public String generateExclusions(String player) {
		
		exclude = String.format(" gameid IN "
				+ "(SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' "
				+ "EXCEPT "
				+ "SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' AND player1 = '%s')", team,team,player);
		return exclude;
	}
	public String generateInclusions(String player) {
		include = String.format(" gameid IN "
					+ "(SELECT DISTINCT gameid FROM pbp "
					+ "WHERE team1 = '%s' AND player1 = '%s')", team,player);
		return include;
		
	}

	public String generateExclusions(String player1,String player2) {
		
		exclude = String.format(" gameid IN "
				+ "(SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' "
				+ "EXCEPT "
				+ "SELECT DISTINCT gameid FROM pbp "
				+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,team,player1,player2);
		return exclude;
	}
	public String generateInclusions(String player1,String player2) {
		include = String.format(" gameid IN "
					+ "(SELECT DISTINCT gameid FROM pbp "
					+ "WHERE team1 = '%s' AND player1 IN ('%s','%s'))", team,player1,player2);
		return include;
		
	}
	
	public HashMap<String,Float> getProp(){
		return totalProp;
	}
	
	
	}

	
	


