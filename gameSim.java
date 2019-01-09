import java.sql.*;
import java.util.*;

public class gameSim {
	teamProb home;
	teamProb away;
	HashMap homeProps;
	HashMap awayProps;
	public int homeScore;
	public int awayScore;
	private int time;
	private int quarter;
	private teamProb possession;
	public gameSim(teamProb home, teamProb away) {
		this.home = home;
		this.away = away;
	}
	
	void generateProbabilities() throws Exception{
		homeProps = home.callQueryGenerator();
		awayProps = away.callQueryGenerator();
	}	
	boolean generateRandomNumber(float Prob) {
		Random randNumber = new Random();
		if(randNumber.nextFloat() < Prob){
			return true;
		}else {
			return false;
		}
	}
	boolean generateRandomNumber(float homeRebProb, float awayRebProb) {
//		System.out.println(homeRebProb);
//		System.out.println(awayRebProb);
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
	//DECLARE a method called generateRandomNumber that returns true if the home team wins the rebound battle
	
	boolean generateRandomNumber() {
		boolean first = generateRandomNumber((float)homeProps.get("rebound"));
		boolean second = generateRandomNumber((float)awayProps.get("rebound"));
		if(first) {
			if(second) {
				return generateRandomNumber();
			}else {
				return true;
			}
		}else {
			if(second) {
				return false;
			}else {
				return generateRandomNumber();
			}
		}
	}
	void startGame() throws Exception{
		homeScore= 0;
		awayScore = 0;
		jumpBall();
		quarter=1;
		time=720;
		while(quarter<4) {
			while(time>0) {
				Shot();
//				System.out.println(String.format("The score is %d (%s) to %d (%s)",homeScore,home.getTeam(),awayScore,away.getTeam()));
//				System.out.println(String.format("There is %d time left and it is quarter number %d", (int)time,quarter));
				
			}
		}
		
	}
	void jumpBall() {
		if(generateRandomNumber(0.5f) == true) {
			possession = home;
//			System.out.println(String.format("The %s win the jump ball", home));
		}else {
			possession = away;
//			System.out.println(String.format("The %s win the jump ball", away));
		}
	}
	public void Shot() throws Exception{
		if(possession.equals(home)) {
//			System.out.println(homeShot);
			if(generateRandomNumber((float)homeProps.get("shot")) == true) {
				threePointer();
				//call three point
			}else if(generateRandomNumber((float)homeProps.get("tov"))) {
				possession = away;
				//call turnover
//				System.out.println(String.format("%s turns the ball over",home.getTeam()));
			}else if (generateRandomNumber((float)homeProps.get("foul"))){
//				System.out.println("Foul");
			}
		}else {
//			System.out.println(awayShot);
			if(generateRandomNumber((float)awayProps.get("shot")) == true) {
				//call threepoint
				threePointer();
			}else if(generateRandomNumber((float)awayProps.get("tov"))){
				//call turnover
				possession = home;
//				System.out.println(String.format("%s turns the ball over",away.getTeam()));
				
			}else if(generateRandomNumber((float)awayProps.get("foul"))){
				//System.out.println("Foul");
			}
			
		}
		//Random timeElapsed  = new Random();
		//time-=timeElapsed.nextInt(24);
		if(possession.equals(home)){possession = away;}else {possession = home;}
		if(time<=0) {
			if(quarter<4) {
				quarter+=1;
				time = 720;
			}else {
//				System.out.println("Game Over");
			}
	}
	}
	void Foul() throws Exception{
		if(possession == home) {
			if(generateRandomNumber((float)homeProps.get("shootingFoul"))) {
//				System.out.println("Going to the foul line");
				freeThrow();
				freeThrow();
				time-=home.getTime("Shooting Foul");
				possession = away;
			}else {
				time-=home.getTime("Foul");
//				System.out.println("Just a stoppage");
			}
		}else {
			if(generateRandomNumber((float)awayProps.get("shootingFoul"))) {
//				System.out.println("Going to the foul line");
				freeThrow();
				freeThrow();
				time-=away.getTime("Shooting Foul");
				possession = home;
			}else {
				time-=away.getTime("Foul");
//				System.out.println("Just a stoppage");
			}
			
		}
	}
	
	void freeThrow() {
		if(possession == home) {
			if(generateRandomNumber((float)homeProps.get("freethrow"))) {
				homeScore++;
//				System.out.println("The free throw was made");
			}else {
//				System.out.println("The free throw was missed");
			}
		}else {
			if(generateRandomNumber((float)(awayProps.get("freethrow")))) {
				awayScore++;
//				System.out.println("The free throw was made");
			}else {
//				System.out.println("The free throw was missed");
			}
		}
	}
	public void threePointer() throws Exception{
		if(possession.equals(home)) {
			if(generateRandomNumber((float)homeProps.get("threePointShot"))) {
//				System.out.println(String.format("Three point shot attempted by %s",home.getTeam()));
				time-=home.getTime("Three");
				make(3);
			}else {
//				System.out.println(String.format("Two point shot attempted by %s", home.getTeam()));
				time-=home.getTime("Shot");
				make(2);
			}
		}else {
			if(generateRandomNumber((float)awayProps.get("threePointShot"))) {
//				System.out.println(String.format("Three point shot attempted by %s", away.getTeam()));
				time-=away.getTime("Three");
				make(3);
			}else {
//				System.out.println(String.format("Two point shot attempted by %s", away.getTeam()));
				time-=away.getTime("Shot");
				make(2);
			}
		}
	}
	public void make(int amount) throws Exception{
			if(possession.equals(home)) {
				if(amount ==3) {
					if(generateRandomNumber((float)homeProps.get("threesMade"))){
						homeScore+=amount;
						possession = away;
			
					}else {
						Rebound();
					}
				}else {
					if(generateRandomNumber((float)homeProps.get("made"))){
						homeScore+=amount;
						possession = away;
					}else {
						Rebound();
					}
				}	
			}else {
				if(amount ==3) {
					if(generateRandomNumber((float)awayProps.get("threesMade"))){
						awayScore+=amount;
						possession = home;
			
					}else {
						Rebound();
					}
				}else {
					if(generateRandomNumber((float)awayProps.get("made"))){
						awayScore+=amount;
						possession = home;
					}else {
						Rebound();
					}
				}	
				
			}
			
		
		
	}
	void Rebound() throws Exception{
		if(possession == home) {
			time-=home.getTime("Rebound");
			if(generateRandomNumber((float)homeProps.get("offensiveRebounds"), (float) awayProps.get("rebounds"))) {
				possession = home;
//				System.out.println(String.format("The %s gets the offensive rebound",home.getTeam()));
			}else {
				possession = away;
//				System.out.println(String.format("The %s gets the rebound",away.getTeam()));			
			}
		}else {
			time-=away.getTime("Rebound");
			if(generateRandomNumber((float) awayProps.get("offensiveRebounds"), (float)homeProps.get("rebounds"))) {
				possession = away;
//				System.out.println(String.format("The %s gets the offensive rebound",home.getTeam()));
			}else {
				possession = home;
//				System.out.println(String.format("The %s gets the rebound",away.getTeam()));			
			}		
		}
	}
	public static void main(String[] args) throws Exception{
		teamProb h = new teamProb("LAC","home");
		teamProb a = new teamProb("CHA","away");
		//home team goes first
		h.generateExclusions("Mbah a Moute");
		h.generateInclusions("Harris");
//		a.generateExclusions("Porzingis");
//		a.generateInclusions("Knox");
		gameSim b = new gameSim(h,a);
		b.generateProbabilities();
		int homeWins = 0;
		int awayWins = 0;
		int over=0;
		int spread = 0;
		try {
			for(int i = 0 ; i<10000;i++) {
				System.out.println(((float)i)/((float)10000));
				b.startGame();
				if(b.homeScore>b.awayScore) {
					homeWins++;
				}else if(b.awayScore>b.homeScore){
					awayWins++;
				}
				if(b.homeScore-b.awayScore>6.5) {
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
