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
 */

public class SimulatorData {

	static int numNewPlayersNeeded = 1000;
	static int numModelsToBuild = 1000;
	static int modelCounter = 0;
	int roundsPlayed = 0;
	private static int playerNameSeed = 0;
	private static int numMutationsCounter = 0;

	private static boolean newPlayerSpawned = true;
	private static boolean modelComplete = false;
	private boolean mutationComplete = false;
	private static boolean simulationComplete = false;

	private static double p_edge_CC = 1.0;
	private static double p_edge_CD = 0.35;

	static PrisonersDilemmaGame pdGame = new PrisonersDilemmaGame();
	static OutputEngine outputEngine;

	static String[] headers = { "model", "#players", "p_edge_CC", "p_edge_CD", "AvgDeg","#Coops", "#Defs",  "#CC", "#DD", "#CD", "Dist", "#Mutations" };

	static Model model = new Model();

	/**
	 * 
	 */
	public SimulatorData() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		outputEngine = new OutputEngine("", "mutator_1", headers);
		
		//p_edge_CC = ((double)(((100 - modelCounter)+9)/10*10))/100;
		//p_edge_CC = ((double)(((200 - modelCounter)+9)/10*10))/200;
		p_edge_CC = (double)(((1000-modelCounter)+9)/10*10)/10000;
		//p_edge_CC = 0.9;
		//p_edge_CD = ((double)(10-(modelCounter%10)))/10;
		//p_edge_CD = ((double)(((200 - modelCounter)+9)/10*10))/200;
		
		p_edge_CD = p_edge_CC*0.39;
		
		while (!simulationComplete) {

			if (newPlayerSpawned) {
				addPlayerToModel(++playerNameSeed, p_edge_CC, p_edge_CD);
				--numNewPlayersNeeded;
				model.merge();
				
				if (numNewPlayersNeeded < 1) {
					newPlayerSpawned = false;
					modelComplete = true;
					
					resetScores();
					playGame();
					model.captureStatistics();
					//reportStatus();
					
					 System.out.println("Model: "+modelCounter+ " --> #Cooperators: " + model.getNumCooperators() +
							 " #Defector: " + model.getNumDefectors() + " ----- #CC: " + model.getNumCCEdges()+" #DD " +
							 model.getNumDDEdges()+" #CD " +
							 model.getNumCDEdges() + " : Distance = " + model.getDistance());
					 
					 

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
				System.out.println();
				++modelCounter;
				// reset newPlayerSpawned
				newPlayerSpawned = true;
				// reset numNewPlayersNeeded
				// ********************************************increasing number
				// of players per model
				// TODO: reset number of players required for next model
				numNewPlayersNeeded = 1000;
				//p_edge_CC = ((double)(((100 - modelCounter)+9)/10*10))/100;
				//p_edge_CC = ((double)(((200 - modelCounter)+9)/10*10))/200;
				p_edge_CC = (double)(((1000-modelCounter)+9)/10*10)/10000;
				//p_edge_CC = 0.9;
				//p_edge_CD = ((double)(10-(modelCounter%10)))/10;
				//p_edge_CD = ((double)(((200 - modelCounter)+9)/10*10))/200;
				p_edge_CD = p_edge_CC*0.39;
				// p_edge = (double)(((modelCounter+9)/10)*10)/numModelsToBuild;
				// p_edge =
				// (double)(((modelCounter+19)/20)*20)/(numModelsToBuild*10);
				// p_edge =
				// p_edges[((int)(double)(((modelCounter+9)/10)*10)/10)-1];
				// p_edge =
				// (double)((((modelCounter+19))/20)*20)/(numModelsToBuild*2);

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

		 outputEngine.saveStat(new Object[]{modelCounter,
		 model.getNumPlayers(),
		 p_edge_CC,
		 p_edge_CD,
		 model.getDegree(),
		 model.getNumCooperators(),
		 model.getNumDefectors(),
		 model.getNumCCEdges(),
		 model.getNumDDEdges(),
		 model.getNumCDEdges(),
		 model.getDistance(),
		 numMutationsCounter});
		
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

	public static void addPlayerToModel(int playerID, double p_edge_CC, double p_edge_CD) {

		// Model model = graph.getModel();
		// beginUpdate();
		Player player = null;
		double coinFlip = Math.random() * 2;
		//double coinFlip = numNewPlayersNeeded%2;
		//double coinFlip = numNewPlayersNeeded-modelCounter;
		//if ((int) coinFlip < 0) {
		if ((int) coinFlip == 0) {
			player = new Cooperator(String.valueOf(playerID));
			model.incrementNumCooperators();
			model.addNewPlayer(player);
			for (Player c : model.getAllPlayers()) {
				double createEdge = Math.random();
				// if both are coops
				if(c instanceof Cooperator){
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
				if(c instanceof Defector){
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

//		model.addNewPlayer(player);

//		for (Player c : model.getAllPlayers()) {
//			double createEdge = Math.random();
//			
//			if (createEdge < p_edge_CC) {
//				model.addEdge(player.getPlayerId(), c.getPlayerId());
//			}
//		}

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
