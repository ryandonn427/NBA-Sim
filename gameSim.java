import java.sql.*;
import java.util.*;

public class gameSim {
	teamProb home;
	teamProb away;
	private int homeScore;
	private int awayScore;
	private int time;
	private int quarter;
	private teamProb possession;
	private float homeShot;
	private float awayShot;
	private float homeThree;
	private float awayThree;
	private float homeMake;
	private float awayMake;
	private float homeThreeMake;
	private float awayThreeMake;
	private float homeTOV;
	private float awayTOV;
	private float homeSteal;
	private float awaySteal;
	private float homeRebound;
	private float awayRebound;
	private float homeOffensiveRebound;
	private float awayOffensiveRebound;
	
	public gameSim(teamProb home, teamProb away) {
		this.home = home;
		this.away = away;
	}
	void generateProbabilities() throws SQLException{
		homeShot = home.getShots()/(home.getShots() + home.getTOV());
		awayShot = away.getShots()/(away.getShots() + home.getTOV());
		System.out.println(home.getShots());
		System.out.println("The shots have been loaded");
		homeThree = home.threePointer()/(home.getShots() + home.threePointer());
		awayThree = away.threePointer()/(away.getShots() + away.threePointer());
		System.out.println("The threes have been loaded");
		homeMake = home.madeShot()/home.getShots();
		awayMake = away.madeShot()/away.getShots();
		System.out.println("The made shots have been loaded");
		homeThreeMake = home.madeThrees()/home.threePointer();
		awayThreeMake = away.madeThrees()/away.threePointer();
		System.out.println("The three makes have been loaded");
		homeTOV = home.getTOV()/(home.getShots() + home.getTOV());
		awayTOV = away.getTOV()/(away.getShots() + away.getTOV());
		System.out.println("The turnovers have been loaded");
		homeSteal = home.getSteal()/homeTOV;
		awaySteal = away.getSteal()/awayTOV;
		System.out.println("The steals have been loaded");
		
		homeRebound = home.getRebound()/(home.getRebound()+home.getTotalRebounds());
		awayRebound = away.getRebound()/(away.getRebound()+away.getTotalRebounds());
		
		homeOffensiveRebound = home.getOffensiveRebound();
		awayOffensiveRebound = away.getOffensiveRebound();
	}
	
	boolean generateRandomNumber(float Prob) {
		Random randNumber = new Random();
		if(randNumber.nextFloat() < Prob){
			return true;
		}else {
			return false;
		}
	}
	//DECLARE a method called generateRandomNumber that returns true if the home team wins the rebound battle
	
	boolean generateRandomNumber(float homeRebProb, float awayRebProb) {
		boolean first = generateRandomNumber(homeRebProb);
		boolean second = generateRandomNumber(awayRebProb);
		if(first) {
			if(second) {
				return generateRandomNumber(homeRebProb,awayRebProb);
			}else {
				return true;
			}
		}else {
			if(second) {
				return false;
			}else {
				return generateRandomNumber(homeRebProb,awayRebProb);
			}
		}
	}
	void startGame() throws SQLException{
		homeScore= 0;
		awayScore = 0;
		jumpBall();
		quarter=1;
		time=720;
		while(quarter<4) {
			while(time>0) {
				Shot();
				System.out.println(String.format("The score is %d (%s) to %d (%s)",homeScore,home.getTeam(),awayScore,away.getTeam()));
				System.out.println(time);
				System.out.println(quarter);
			}
		}
		
	}
	void jumpBall() {
		if(generateRandomNumber(0.5f) == true) {
			possession = home;
			System.out.println(String.format("The %s win the jump ball", home));
		}else {
			possession = away;
			System.out.println(String.format("The %s win the jump ball", away));
		}
	}
	public void Shot() {
		if(possession.equals(home)) {
			System.out.println(homeShot);
			if(generateRandomNumber(homeShot) == true) {
				threePointer();
				//call three point
			}else {
				//call turnover
				System.out.println(String.format("%s turns the ball over",home.getTeam()));
			}
		}else {
			System.out.println(awayShot);
			if(generateRandomNumber(awayShot) == true) {
				//call threepoint
				threePointer();
			}else {
				//call turnover
				System.out.println(String.format("%s turns the ball over",away.getTeam()));
				
			}
			
		}
		Random timeElapsed  = new Random();
		time-=timeElapsed.nextInt(24);
		if(possession.equals(home)){possession = away;}else {possession = home;}
		if(time<=0) {
			if(quarter<4) {
				quarter+=1;
				time = 720;
			}else {
				System.out.println("Game Over");
			}
	}
	}
	public void threePointer() {
		if(possession.equals(home)) {
			if(generateRandomNumber(homeThree)) {
				System.out.println(String.format("Three point shot attempted by %s",home.getTeam()));
				make(3);
			}else {
				System.out.println(String.format("Two point shot attempted by %s", home.getTeam()));
				make(2);
			}
		}else {
			if(generateRandomNumber(awayThree)) {
				System.out.println(String.format("Three point shot attempted by %s", away.getTeam()));
				make(3);
			}else {
				System.out.println(String.format("Two point shot attempted by %s", away.getTeam()));
				make(2);
			}
		}
	}
	public void make(int amount) {
		if(amount == 2) {
			if(possession.equals(home)) {
				if(generateRandomNumber(homeMake)) {
					System.out.println(String.format("Two point shot scored by %s",home.getTeam()));
					homeScore+=2;
				}else {
					System.out.println(String.format("Two point shot missed by %s", home.getTeam()));
				}
			}else {
				if(generateRandomNumber(awayMake)) {
					System.out.println(String.format("Two point shot scored by %s", away.getTeam()));
					awayScore+=2;
				}else {
					System.out.println(String.format("Two point shot missed by %s", away.getTeam()));
				}
			}
		}else {
			if(possession.equals(home)) {
				if(generateRandomNumber(homeThreeMake)) {
					System.out.println(String.format("Three point shot scored by %s",home.getTeam()));
					homeScore+=3;
				}else {
					System.out.println(String.format("Three point shot missed by %s", home.getTeam()));
					Rebound();
				}
			}else {
				if(generateRandomNumber(awayThreeMake)) {
					System.out.println(String.format("Three point shot scored by %s", away.getTeam()));
					awayScore+=3;
				}else {
					System.out.println(String.format("Three point shot missed by %s", away.getTeam()));
					Rebound();
				}
			}
		}
	}
	void Rebound() {
		if(generateRandomNumber(homeRebound, awayRebound)) {
			possession = home;
			System.out.println(String.format("The %s gets the rebound",home.getTeam()));
		}else {
			possession = away;
			System.out.println(String.format("The %s gets the rebound",away.getTeam()));			
		}
	}

	public static void main(String[] args) {
		teamProb h = new teamProb("POR");
		teamProb a = new teamProb("MIL");
		//home team goes first
		gameSim b = new gameSim(h,a);
		int homeWins = 0;
		int awayWins = 0;
		int over=0;
		
		try {
			b.generateProbabilities();
			for(int i = 0 ; i<1000;i++) {
				b.startGame();
				if(b.homeScore>b.awayScore) {
					homeWins++;
				}else {
					awayWins++;
				}
				if(b.homeScore + b.awayScore>228.5) {
					over++;
				}
					
			}
			System.out.println(String.format("The %s has won %.5f  times", b.home.getTeam(),(float)(homeWins)/((float)(homeWins)+(float)(awayWins))));
			System.out.println(String.format("The %s has won %.5f  times", b.away.getTeam(),(float)(awayWins)/((float)(homeWins)+(float)(awayWins))));
			System.out.println(String.format("The over has won %.5f  times",(float)(over)/((float)(over)+(float)(1000-over))));
			System.out.println(String.format("The under has won %.5f  times",(float)(1000-over)/((float)(over)+(float)(1000-over))));

		}catch(Exception e ) {
			e.printStackTrace();
		}
		}
}
