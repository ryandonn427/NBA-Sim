import java.sql.*;
import java.util.*;

public class gameSim {
	teamProb home;
	teamProb away;
	private boolean homeMade;
	private boolean awayMade;
	private boolean homeMadeThree;
	private boolean awayMadeThree;
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
	private float homeFoul;
	private float awayFoul;
	private float homeShootingFoul;
	private float awayShootingFoul;
	private float homeFreeThrow;
	private float awayFreeThrow;
	private float homeMakeMiss;
	private float homeMakeMake;
	private float homeMissMake;
	private float homeMissMiss;
	private float awayMakeMiss;
	private float awayMakeMake;
	private float awayMissMake;
	private float awayMissMiss;
	private float homeMakeMissThree;
	private float homeMakeMakeThree;
	private float homeMissMakeThree;
	private float homeMissMissThree;
	private float awayMakeMissThree;
	private float awayMakeMakeThree;
	private float awayMissMakeThree;
	private float awayMissMissThree;
	
	
	
	public gameSim(teamProb home, teamProb away) {
		this.home = home;
		this.away = away;
	}
	void generateProbabilities() throws SQLException{
		homeShot = home.getShots()/(home.getShots() + home.getTOV()+away.getFoul(false));
		awayShot = away.getShots()/(away.getShots() + away.getTOV()+home.getFoul(false));
	
		homeThree = home.threePointer()/(home.getShots() + home.threePointer());
		awayThree = away.threePointer()/(away.getShots() + away.threePointer());

		homeMakeMiss = home.madeShot("OX")/(home.madeShot("OO")+home.madeShot("OX"));
		homeMakeMake = home.madeShot("OO")/(home.madeShot("OO")+home.madeShot("OX"));
		homeMissMake = home.madeShot("XO")/(home.madeShot("XX")+home.madeShot("XO"));
		homeMissMiss = home.madeShot("XX")/(home.madeShot("XO")+home.madeShot("XX"));
		
		awayMakeMiss = away.madeShot("OX")/(away.madeShot("OO")+away.madeShot("OX"));
		awayMakeMake = away.madeShot("OO")/(away.madeShot("OO")+away.madeShot("OX"));
		awayMissMake = away.madeShot("XO")/(away.madeShot("XX")+away.madeShot("XO"));
		awayMissMiss = away.madeShot("XX")/(away.madeShot("XO")+away.madeShot("XX"));

		homeMakeMissThree = home.madeThrees("OX")/(home.madeThrees("OO")+home.madeThrees("OX"));
		homeMakeMakeThree = home.madeThrees("OO")/(home.madeThrees("OO")+home.madeThrees("OX"));
		homeMissMakeThree = home.madeThrees("XO")/(home.madeThrees("XX")+home.madeThrees("XO"));
		homeMissMissThree = home.madeThrees("XX")/(home.madeThrees("XO")+home.madeThrees("XX"));
		
		awayMakeMissThree = away.madeThrees("OX")/(away.madeThrees("OO")+away.madeThrees("OX"));
		awayMakeMakeThree = away.madeThrees("OO")/(away.madeThrees("OO")+away.madeThrees("OX"));
		awayMissMakeThree = away.madeThrees("XO")/(away.madeThrees("XX")+away.madeThrees("XO"));
		awayMissMissThree = away.madeThrees("XX")/(away.madeThrees("XO")+away.madeThrees("XX"));

		
		homeTOV = home.getTOV()/(home.getShots() + home.getTOV()+away.getFoul(false));
		awayTOV = away.getTOV()/(away.getShots() + away.getTOV()+home.getFoul(false));
		
		homeSteal = home.getSteal()/homeTOV;
		awaySteal = away.getSteal()/awayTOV;
		
		homeRebound = home.getRebound()/(home.getRebound()+home.getTotalRebounds());
		awayRebound = away.getRebound()/(away.getRebound()+away.getTotalRebounds());
		
		homeOffensiveRebound = home.getOffensiveRebound();
		awayOffensiveRebound = away.getOffensiveRebound();
		
		homeFoul = away.getFoul(false)/(home.getShots() + home.getTOV()+away.getFoul(false));
		awayFoul = home.getFoul(false)/(away.getShots() + away.getTOV()+home.getFoul(false));
		
		homeShootingFoul = away.getFoul(true)/(away.getFoul(true)+away.getFoul(false));
		awayShootingFoul = home.getFoul(true)/(home.getFoul(true)+home.getFoul(false));
		
		homeFreeThrow = home.getFreeThrow(true)/(home.getFreeThrow(true)+home.getFreeThrow(false));
		awayFreeThrow = away.getFreeThrow(true)/(away.getFreeThrow(true)+away.getFreeThrow(false));
		
		
				
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
			}else if(generateRandomNumber(homeTOV)) {
				possession = away;
				//call turnover
				System.out.println(String.format("%s turns the ball over",home.getTeam()));
			}else if (generateRandomNumber(homeFoul)){
				System.out.println("Foul");
			}
		}else {
			System.out.println(awayShot);
			if(generateRandomNumber(awayShot) == true) {
				//call threepoint
				threePointer();
			}else if(generateRandomNumber(awayTOV)){
				//call turnover
				possession = home;
				System.out.println(String.format("%s turns the ball over",away.getTeam()));
				
			}else if(generateRandomNumber(awayFoul)){
				System.out.println("Foul");
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
	void Foul() {
		if(possession == home) {
			if(generateRandomNumber(homeShootingFoul)) {
				System.out.println("Going to the foul line");
				freeThrow();
				freeThrow();
				possession = away;
			}else {
				System.out.println("Just a stoppage");
			}
		}else {
			if(generateRandomNumber(awayShootingFoul)) {
				System.out.println("Going to the foul line");
				freeThrow();
				freeThrow();
				possession = home;
			}else {
				System.out.println("Just a stoppage");
			}
			
		}
	}
	
	void freeThrow() {
		if(possession == home) {
			if(generateRandomNumber(homeFreeThrow)) {
				homeScore++;
				System.out.println("The free throw was made");
			}else {
				System.out.println("The free throw was missed");
			}
		}else {
			if(generateRandomNumber(awayFreeThrow)) {
				awayScore++;
				System.out.println("The free throw was made");
			}else {
				System.out.println("The free throw was missed");
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
		float isMake = 0;
		if(amount == 2) {
			if(possession.equals(home)) {
				if(homeMade) {
					isMake = homeMakeMake;
				
				}else {
					isMake = homeMissMake;
				}
			}else {
				if(awayMade) {
					if(amount == 2) {
						isMake = awayMakeMake;
					}else if (amount ==3){
						isMake = awayMakeMakeThree;
					}
				}else {
					if(amount ==2) {
						isMake = awayMissMake;
					}else if(amount ==3){
						isMake = awayMissMakeThree;
					}
				}
			}
		}else {
			if(possession.equals(home)) {
				if(homeMadeThree) {
					isMake = homeMakeMakeThree;
				}else {
					isMake = homeMissMakeThree;
				}
			}else {
				if(awayMadeThree) {
					isMake = awayMakeMakeThree;
				}else {
					isMake = awayMissMakeThree;
				}
			}			
		}
		if(generateRandomNumber(isMake)) {
			if(possession.equals(home)) {
				homeScore+=amount;
				if(amount==3) {homeMadeThree = true;}else {homeMade = true;}
				possession = away;
			}else {
				awayScore+=amount;
				if(amount==3) {awayMadeThree = true;}else {awayMade = true;}
				possession = home;
			}
			
		}else {
			if(possession.equals(home)) {
				if(amount==3) {homeMadeThree = false;}else {homeMade = false;}
				Rebound();
			}else {
				if(amount==3) {awayMadeThree = false;}else {awayMade = false;}
				Rebound();
			}
		}
		
	}
	void Rebound() {
		if(possession == home) {
			if(generateRandomNumber(homeOffensiveRebound, awayRebound)) {
				possession = home;
				System.out.println(String.format("The %s gets the offensive rebound",home.getTeam()));
			}else {
				possession = away;
				System.out.println(String.format("The %s gets the rebound",away.getTeam()));			
			}
		}else {
			if(generateRandomNumber(awayOffensiveRebound, homeRebound)) {
				possession = away;
				System.out.println(String.format("The %s gets the offensive rebound",home.getTeam()));
			}else {
				possession = home;
				System.out.println(String.format("The %s gets the rebound",away.getTeam()));			
			}		
		}
	}
	public static void main(String[] args) {
		teamProb h = new teamProb("LAC","home");
		teamProb a = new teamProb("GSW","away");
		//home team goes first
		gameSim b = new gameSim(h,a);
		int homeWins = 0;
		int awayWins = 0;
		int over=0;
		int spread = 0;
		try {
			b.generateProbabilities();
			for(int i = 0 ; i<10000;i++) {
				b.startGame();
				if(b.homeScore>b.awayScore) {
					homeWins++;
				}else if(b.awayScore>b.homeScore){
					awayWins++;
				}
				if(b.homeScore + b.awayScore>226.5) {
					over++;
				}
				if(b.awayScore-b.homeScore>3.5) {
					spread++;
				}
			}
			System.out.println(String.format("The %s has won %.5f  times", b.home.getTeam(),(float)(homeWins)/((float)(homeWins)+(float)(awayWins))));
			System.out.println(String.format("The %s has won %.5f  times", b.away.getTeam(),(float)(awayWins)/((float)(homeWins)+(float)(awayWins))));
			System.out.println(String.format("The over has won %.5f  times",(float)(over)/((float)(over)+(float)(10000-over))));
			System.out.println(String.format("The under has won %.5f  times",(float)(10000-over)/((float)(over)+(float)(10000-over))));
			System.out.println(String.format("The spread has won %.5f  times",(float)(spread)/((float)(spread)+(float)(10000-spread))));

		}catch(Exception e ) {
			e.printStackTrace();
		}
		}
}
