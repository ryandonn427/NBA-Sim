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
		
		homeRebound = home.getRebound()/(home.getRebound()+away.getRebound());
		awayRebound = away.getRebound()/(away.getRebound()+home.getRebound());
		

	}
	
	boolean generateRandomNumber(float Prob) {
		Random randNumber = new Random();
		if(randNumber.nextFloat() < Prob){
			return true;
		}else {
			return false;
		}
	}
	void startGame() throws SQLException{
		homeScore= 0;
		awayScore = 0;
		generateProbabilities();
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
		time-=24;
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
				}
			}else {
				if(generateRandomNumber(awayThreeMake)) {
					System.out.println(String.format("Three point shot scored by %s", away.getTeam()));
					awayScore+=3;
				}else {
					System.out.println(String.format("Three point shot scored by %s", away.getTeam()));
				}
			}
		}
	}
	public static void main(String[] args) {
		teamProb h = new teamProb("NYK");
		teamProb a = new teamProb("BKN");
		gameSim b = new gameSim(a,h);
		try {
			b.startGame();
		}catch(Exception e ) {
			e.printStackTrace();
		}
		}
}
