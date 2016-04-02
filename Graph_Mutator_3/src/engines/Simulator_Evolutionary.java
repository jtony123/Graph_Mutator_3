/**
 * 
 */
package engines;

import java.util.ArrayList;
import java.util.List;

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
 */

public class Simulator_Evolutionary {

	static int numNewPlayersNeeded = 100;
	static int numModelsToBuild = 100;
	static int numEvolutionsToRun = 1000;
	static int modelCounter = 0;
	static int evolutionCounter = 0;
	int roundsPlayed = 0;
	private static int playerNameSeed = 0;
	private static int numMutationsCounter = 0;

	private static boolean newPlayerSpawned = true;
	private static boolean modelComplete = false;
	private boolean mutationComplete = false;
	private static boolean evolutionComplete = false;
	private static boolean simulationComplete = false;

	private static double p_edge_CC = 1.0;
	private static double p_edge_CD = 0.35;

	private static int gameCost = 3;
	private static int spawnThreshold = 15;

	static PrisonersDilemmaGame pdGame = new PrisonersDilemmaGame();
	static OutputEngine outputEngine;

	static String[] headers = { "model", "#evol", "#players", "p_edge_CC", "p_edge_CD", "AvgDeg", "#Coops", "#Defs", "#CC",
			"#DD", "#CD", "Dist" };

	static Model model = new Model();

	/**
	 * 
	 */
	public Simulator_Evolutionary() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		outputEngine = new OutputEngine("", "mutator_1", headers);

		//p_edge_CC = (double) (((1000 - modelCounter) + 9) / 10 * 10) / 10000;
		p_edge_CC = 0.1;
		p_edge_CD = p_edge_CC * 0.39;

		while (!simulationComplete) {
			
			if (newPlayerSpawned) {
				addPlayerToModel(++playerNameSeed, p_edge_CC, p_edge_CD);
				--numNewPlayersNeeded;
				model.merge();

				if (numNewPlayersNeeded < 1) {
					newPlayerSpawned = false;
					modelComplete = true;
				}

			} else if (modelComplete) {
				
				playGame();
				model.captureStatistics();
				System.out.println("Model: " + modelCounter + " --> #Cooperators: " + model.getNumCooperators()
				+ " #Defector: " + model.getNumDefectors() + " ----- #CC: " + model.getNumCCEdges()
				+ " #DD " + model.getNumDDEdges() + " #CD " + model.getNumCDEdges() + " : Distance = "
				+ model.getDistance() + " numPlayers " + model.getNumPlayers());
				reportStatus();

				// run the evolution
				if (!evolutionComplete) {
					// charge the players the cost of the game
					// check which players can spawn
					++evolutionCounter;
					System.out.println("evolving " + evolutionCounter);
					System.out.println("sp = " +(spawnThreshold*evolutionCounter) + "; cost = " + (gameCost*evolutionCounter));
					evolveModel();
					

					if (evolutionCounter == numEvolutionsToRun) {
						evolutionComplete = true;
						modelComplete = false;
						newPlayerSpawned = false;						
					}
					
					if(model.getAllPlayers().isEmpty() || model.getAllPlayers().size() > 2000){
						evolutionComplete = true;
						modelComplete = false;
						newPlayerSpawned = false;
					}

				}

			} else if (modelCounter <= numModelsToBuild) {
				
				playGame();
				model.captureStatistics();
				// output the final stats
				System.out.println("Model: " + modelCounter + " --> #Cooperators: " + model.getNumCooperators()
				+ " #Defector: " + model.getNumDefectors() + " ----- #CC: " + model.getNumCCEdges()
				+ " #DD " + model.getNumDDEdges() + " #CD " + model.getNumCDEdges() + " : Distance = "
				+ model.getDistance() + " numPlayers " + model.getNumPlayers());
				System.out.println("evolution complete");
				System.out.println();
				System.out.println();
				reportStatus();
				// blamk line in output
				outputEngine.saveStat(new Object[] {"",""});
				
				// build the next model
				// increment the numModelsCounter
				System.out.println("setup for next model");
				
				++modelCounter;
				evolutionCounter = 0;
				// reset newPlayerSpawned
				newPlayerSpawned = true;
				evolutionComplete = false;
				// reset numNewPlayersNeeded
				// ********************************************increasing number
				// of players per model
				// TODO: reset number of players required for next model
				numNewPlayersNeeded = 100;
				//p_edge_CC = (double) (((1000 - modelCounter) + 9) / 10 * 10) / 10000;	
				p_edge_CC = 0.1;
				p_edge_CD = p_edge_CC * 0.39;
				
				System.out.println("pedgeCC = "+p_edge_CC + " pedgeCD = " + p_edge_CD);

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

	private static void evolveModel() {
		// int numNewPlayersNeeded = 0;

		// decrement scores
		for (Player player : model.getAllPlayers()) {
			player.decrementPlayerScore(gameCost*evolutionCounter);
		}

		// check what players can spawn and which are eliminated
		List<Player> removedPlayers = new ArrayList<Player>();
		List<Edge> removedEdges = new ArrayList<Edge>();

		for (Player player : model.getAllPlayers()) {

			// which players can spawn
			if (player.getScore() > (spawnThreshold*evolutionCounter)) {
				player.setScore(10);
				++numNewPlayersNeeded;
				newPlayerSpawned = true;
			}

			// which players get eliminated
			if (player.getScore() < 0) {
				if (player instanceof Cooperator) {
					model.decrementNumCooperators();
				} else {
					model.decrementNumDefectors();
				}
				removedPlayers.add(player);

				for (Edge edge : model.getAllEdges()) {
					if (edge.getSource().equals(player) || edge.getTarget().equals(player)) {
						if (!removedEdges.contains(edge)) {
							removedEdges.add(edge);
						}
					}
				}
			}
		}

		for (Edge edge : removedEdges) {
			if (edge.getTarget() instanceof Cooperator && edge.getSource() instanceof Cooperator) {
				model.decrementNumCCEdges();
			} else if (edge.getTarget() instanceof Defector && edge.getSource() instanceof Defector) {
				model.decrementNumDDEdges();
			} else {
				model.decrementNumCDEdges();
			}
		}

		model.getAllEdges().removeAll(removedEdges);
		model.getAllPlayers().removeAll(removedPlayers);
		// System.gc();
	}

	private static void reportStatus() {

		outputEngine.saveStat(new Object[] { modelCounter, evolutionCounter, model.getNumPlayers(), p_edge_CC, p_edge_CD,
				model.getDegree(), model.getNumCooperators(), model.getNumDefectors(), model.getNumCCEdges(),
				model.getNumDDEdges(), model.getNumCDEdges(), model.getDistance() });

	}

	public static void resetScores() {
		for (Player player : model.getAllPlayers()) {
			player.setScore(0);
		}
	}

	public static void playGame() {
		for (Edge edge : model.getAllEdges()) {
			pdGame.playGame(edge.getSource(), edge.getTarget());
		}
	}

	public static void addPlayerToModel(int playerID, double p_edge_CC, double p_edge_CD) {

		// Model model = graph.getModel();
		// beginUpdate();
		Player player = null;
		double coinFlip = Math.random() * 2;
		// double coinFlip = numNewPlayersNeeded%2;
		// double coinFlip = numNewPlayersNeeded-modelCounter;
		// if ((int) coinFlip < 0) {
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
				model.decrementNumCDEdges();
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

}
