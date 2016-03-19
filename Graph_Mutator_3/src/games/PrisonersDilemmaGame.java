package games;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.HashMap;
import java.util.Map;

import graphcomponents.Edge;
import graphcomponents.Player;
import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.PlayerType;
import graphenvironment.Model;

public class PrisonersDilemmaGame {

	private static Map<String, Integer> payoff;
	
	public PrisonersDilemmaGame(){
		
		payoff = new HashMap<String, Integer>();
		// initialise the payoff matrix
    	// S < P < R < T
    	payoff.put("T", 5);
    	payoff.put("R", 3);  
    	payoff.put("P", 1);
    	payoff.put("S", 0);
	}
	
	public void playGame(Player player1, Player player2){
			
		// CC
		if(player1 instanceof Cooperator && player2 instanceof Cooperator){
			player1.addToScore(payoff.get("R"));
			player2.addToScore(payoff.get("R"));
		}
		// CD
		if (player1 instanceof Cooperator && player2 instanceof Defector){
			player1.addToScore(payoff.get("S"));
			player2.addToScore(payoff.get("T"));
		}
		// DC
		if (player1 instanceof Defector && player2 instanceof Cooperator){
			player1.addToScore(payoff.get("T"));
			player2.addToScore(payoff.get("S"));
		}
		// DD
		if (player1 instanceof Defector && player2 instanceof Defector){
			player1.addToScore(payoff.get("P"));
			player2.addToScore(payoff.get("P"));
		}            			
	}

}
