/**
 * 
 */
package engines;

import java.util.ArrayList;

import games.PrisonersDilemmaGame;
import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.Edge;
import graphcomponents.Player;
import graphenvironment.Model;


/**
 * @author Anthony Jackson
 * @id 11170365
 * 
 *     This class records the ratio of CC to CD once equilibrium
 *     has been reached, as the p_edge is varied
 *
 */
public class Simulator_RatioComparator {

	static int numNewPlayersNeeded = 100;
	static int numModelsToBuild = 1000;
	static int modelCounter = 0;

	private static int playerNameSeed = 0;
	private static int numMutationsCounter = 0;

	private static boolean newPlayerSpawned = true;

	private static boolean modelComplete = false;
	private static boolean simulationComplete = false;

	private static double p_edge = 1.0;
	static PrisonersDilemmaGame pdGame = new PrisonersDilemmaGame();

	static OutputEngine outputEngine;

	static String[] headers = { "model", "#players", "p_edge", "AvgDeg", "#Coops", "#Defs", "|cc|", "|dd|", "|cd|", "C_Score", "D_Score", "Distance", "#Mutations"};

	static Model model = new Model();

	public Simulator_RatioComparator() {

	}

	public static void main(String[] args) {

		outputEngine = new OutputEngine("", "edge_counter", headers);

		p_edge = (double) (((1000 - modelCounter) + 99) / 100 * 100) / 1000;

		while (!simulationComplete) {

			if (newPlayerSpawned) {
				addPlayerToModel(++playerNameSeed, p_edge, p_edge);
				--numNewPlayersNeeded;
				model.merge();

				if (numNewPlayersNeeded < 1) {
					newPlayerSpawned = false;
					modelComplete = true;
					
					resetScores();
					playGame();
					model.captureStatistics();
					//reportStatus();

					System.out.println("Model: " + modelCounter + " --> #Cooperators: " + model.getNumCooperators()
							+ " #Defector: " + model.getNumDefectors() + " Distance = " + model.getDistance());
					
					
					// model complete, now put the defectors into the priority
					// queue
					for (Player player : model.getAllPlayers()) {
						if (player instanceof Defector) {
							model.getPlayerQueue().add(player);
						}
					}

				}
				
			} else if (modelComplete) {
				// here the mutation takes place
				// play a round of prisoners dilemma before each mutation and
				// record the scores accumulated
				
				resetScores();
				playGame();				
				model.captureStatistics();
				//reportStatus();
				
				// check distance
			if(model.getDistance() <= 0){
				System.out.println("CC " + (model.getNumCCEdges() * 4) + " : CD " + ((double) model.getNumCDEdges() * 5));

				++numMutationsCounter;
				mutatePlayer();
				//resetScores();
				//playGame();					
				//model.captureStatistics();
				
				//reportStatus();

			} else {
				 System.out.println("Model: "+modelCounter+ " --> #Cooperators: " + model.getNumCooperators() +
				 " #Defector: " + model.getNumDefectors() + " ----- #CC: " + model.getNumCCEdges()+" #DD " +
				 model.getNumDDEdges()+" #CD " +
				 model.getNumCDEdges());

				modelComplete = false;
				resetScores();
				playGame();
				model.captureStatistics();
				reportStatus();
				
			}


			} else if (modelCounter <= numModelsToBuild) {
				// build the next model
				// increment the numModelsCounter
				++modelCounter;
				// reset newPlayerSpawned
				newPlayerSpawned = true;
				// reset number of players required for next model
				numNewPlayersNeeded = 100;
				// p_edge is determined from the model counter and
				// reduces every 100 models built by 0.1
				p_edge = (double) (((1000 - modelCounter) + 99) / 100 * 100) / 1000;

				playerNameSeed = 0;
				numMutationsCounter = 0;
				model = new Model();

			} else {
				simulationComplete = true;
			}

			model.merge();
		}
		System.out.println("Simulation complete");
	}

	/**
	 * 
	 */
	private static void reportStatus() {

		outputEngine.saveStat(new Object[] { modelCounter, model.getNumPlayers(), p_edge, model.getDegree(),
				model.getNumCooperators(), model.getNumDefectors(), model.getNumCCEdges(), model.getNumDDEdges(),
				model.getNumCDEdges(), model.getCoopScore(), model.getDefScore(), model.getDistance(), numMutationsCounter });

	}

	public static void addPlayerToModel(int playerID, double p_edge_CC, double p_edge_CD) {

		Player player = null;
		 double coinFlip = Math.random() * 2;
		//double coinFlip = numNewPlayersNeeded % 2;
		// double coinFlip = numNewPlayersNeeded-(modelCounter%100);
		// if ((int) coinFlip <= 0) {
		if ((int) coinFlip == 0) {
			player = new Cooperator(String.valueOf(playerID));
			model.incrementNumCooperators();
			model.addNewPlayer(player);
			for (Player c : model.getAllPlayers()) {
				double createEdge = Math.random();
				// if both are coops
				if (c instanceof Cooperator) {
					if (createEdge < p_edge_CC) {
						model.addEdge(player.getPlayerId(), c.getPlayerId());
					}
					// the other must be a defector
				} else {
					if (createEdge < p_edge_CD) {
						model.addEdge(player.getPlayerId(), c.getPlayerId());
					}
				}

			}

		} else {
			player = new Defector(String.valueOf(playerID));
			model.incrementNumDefectors();
			model.addNewPlayer(player);
			for (Player c : model.getAllPlayers()) {
				double createEdge = Math.random();
				// if both are defectors
				if (c instanceof Defector) {
					if (createEdge < p_edge_CC) {
						model.addEdge(player.getPlayerId(), c.getPlayerId());
					}
					// the other must be a cooperator
				} else {
					if (createEdge < p_edge_CD) {
						model.addEdge(player.getPlayerId(), c.getPlayerId());
					}
				}
			}
		}
	}
	
	public static void mutatePlayer() {

		Player playerToMutate = model.getPlayerQueue().poll();
		// create a new cooperator
		Player player = new Cooperator(playerToMutate.getPlayerId());
		// System.out.println("Mutating player "+playerToMutate.getPlayerId());
		// TODO: this is a really nasty way of doing this, change to hashmap
		model.getAllPlayers().add(model.getAllPlayers().indexOf(playerToMutate), player);
		model.getAllPlayers().remove(playerToMutate);

		model.getAllEdges().removeAll(playerToMutate.getPlayersEdges());

		model.getPlayerMap().put(player.getPlayerId(), player);
		model.getEdgeMap().put(player, new ArrayList<Player>());

		for (Player otherplayer : model.getEdgeMap().get(playerToMutate)) {
			// System.out.println("Connected to " + otherplayer);
			// adjust edge ratio counts
			if (otherplayer instanceof Defector) {
				// if the other player is a defector
				model.decrementNumDDEdges();
				// --numDDEdges;
			} else {
				// the other player must be a cooperator
				model.derementNumCDEdges();
				// --numCDEdges;
			}
			otherplayer.getPlayersEdges().remove(playerToMutate);
			model.getEdgeMap().get(otherplayer).remove(playerToMutate);
			Edge edge = model.addEdge(player.getPlayerId(), otherplayer.getPlayerId());

		}

		model.getAllEdges().addAll(model.getAddedEdges());
		model.getAddedEdges().clear();
		model.getEdgeMap().remove(playerToMutate);

		model.incrementNumCooperators();// numCooperators;
		model.decrementNumDefectors();// --numDefectors;
		// return null;
	}
	public static void resetScores(){
		for (Player player : model.getAllPlayers()) {
			player.setScore(0);
		}
	}
	
	public static void playGame(){
		for (Edge edge : model.getAllEdges()) {
			pdGame.playGame(edge.getSource(), edge.getTarget());
		}
	}

}


