/**
 * 
 */
package engines;

import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.Player;
import graphenvironment.Model;

/**
 * @author Anthony Jackson
 * @id 11170365
 * 
 *     This class counts the number of each edge type as the p_edge is varied
 *
 */
public class Simulator_EdgeCounter {

	static int numNewPlayersNeeded = 100;
	static int numModelsToBuild = 1000;
	static int modelCounter = 0;

	private static int playerNameSeed = 0;

	private static boolean newPlayerSpawned = true;
	private static boolean simulationComplete = false;

	private static double p_edge = 1.0;

	static OutputEngine outputEngine;

	static String[] headers = { "model", "#players", "p_edge", "AvgDeg", "#Coops", "#Defs", "#CC", "#DD", "#CD" };

	static Model model = new Model();

	public Simulator_EdgeCounter() {

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
					model.captureStatistics();
					reportStatus();

					System.out.println("Model: " + modelCounter + " --> #Cooperators: " + model.getNumCooperators()
							+ " #Defector: " + model.getNumDefectors() + " ----- #CC: " + model.getNumCCEdges()
							+ " #DD " + model.getNumDDEdges() + " #CD " + model.getNumCDEdges());

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
				model.getNumCDEdges() });

	}

	public static void addPlayerToModel(int playerID, double p_edge_CC, double p_edge_CD) {

		Player player = null;
		// double coinFlip = Math.random() * 2;
		double coinFlip = numNewPlayersNeeded % 2;
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

}
