
/**
 * 
 */
package engines;

import java.util.ArrayList;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.List;

import games.PrisonersDilemmaGame;
import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.Edge;
import graphcomponents.Player;
import graphenvironment.CircleLayout;
import graphenvironment.ControlBar;
import graphenvironment.Graph;
import graphenvironment.Layout;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SimulatorEvolutionUI extends Application {

	// TODO:
	int numNewPlayersNeeded;
	int numModelsToBuild = 99;
	int numEvolutionsToRun = 1000;
	int modelCounter = 0;
	int evolutionCounter = 0;
	int roundsPlayed = 0;
	private int playerNameSeed = 0;

	private boolean newPlayerSpawned = true;
	private static boolean modelComplete = false;
	private static boolean evolutionComplete = false;
	private static boolean simulationComplete = false;

	private double p_edge_CC = 1.0;
	private double p_edge_CD = 0.35;

	private int gameCost = 2;
	private int spawnThreshold = 15;

	PrisonersDilemmaGame pdGame = new PrisonersDilemmaGame();
	static OutputEngine outputEngine;

	static String[] headers = { "model", "#evol", "#players", "p_edge_CC", "p_edge_CD", "AvgDeg", "#Coops", "#Defs", "#CC",
			"#DD", "#CD", "Dist" };
		
	private double timeDelay = 100;

	Graph graph = new Graph();
	BorderPane root = new BorderPane();

	KeyFrame keyFrame;

	Duration duration = Duration.millis(timeDelay);

	private Timeline timeline;
	private boolean paused = false;
	Scene scene = null;
	Stage mainStage;
	ControlBar controlBar = new ControlBar();
	

	@Override
	public void init() {
		String[] headers = {"model","#players","p(edge)", "mutations","#C-#D", "Cs-Ds", "#CC","#DD","#CD"};
		
	}

	@Override
	public void start(Stage primaryStage) {

		mainStage = primaryStage;
		graph = new Graph();

		root.setCenter(graph.getScrollPane());
		root.setLeft(controlBar.getControlPanel());

		controlBar.getStartButton().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				controlBar.getStartButton().setDisable(true);
				
				numModelsToBuild = controlBar.getNumModelsRequired();
				controlBar.setNumModelsRequired(numModelsToBuild);				
				numNewPlayersNeeded = controlBar.getNumPlayersPerModel();//65;
				controlBar.setNumPlayersPerModel(numNewPlayersNeeded);
				spawnThreshold = controlBar.getSpawnThreshold();
				gameCost = controlBar.getCost();
				p_edge_CC = controlBar.getP_edge_CC();
				p_edge_CD = p_edge_CC*0.39;				
				controlBar.setP_edge_CC(p_edge_CC);	
				controlBar.setP_edge_CD(p_edge_CD);
				
				timeline = new Timeline();
				timeline.setCycleCount(Timeline.INDEFINITE);
				timeline.getKeyFrames().add(keyFrame);
				timeline.playFromStart();
			}
		});

		controlBar.getPauseButton().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				if (paused) {
					controlBar.getPauseButton().setText("Pause");
					timeline.play();
				} else {
					controlBar.getPauseButton().setText("Resume");
					timeline.pause();
				}
				paused = !paused;
			}
		});

		controlBar.getResetButton().setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				mainStage.close();
				mainStage = new Stage();
				root = new BorderPane();
				scene = new Scene(root, 1024, 500);
				//mainStage.setScene(scene);
				graph = new Graph();
				root.setCenter(graph.getScrollPane());
				root.setLeft(controlBar.getControlPanel());
				mainStage.setScene(scene);
				Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
				mainStage.setX(primaryScreenBounds.getMinX());
				mainStage.setY(primaryScreenBounds.getMinY());
				mainStage.setWidth(primaryScreenBounds.getWidth());
				mainStage.setHeight(primaryScreenBounds.getHeight());
				mainStage.show();
				mainStage.show();
				
				numModelsToBuild = controlBar.getNumModelsRequired();
				controlBar.setNumModelsRequired(numModelsToBuild);	
				numNewPlayersNeeded = controlBar.getNumPlayersPerModel();
				controlBar.setNumPlayersPerModel(numNewPlayersNeeded);
				p_edge_CC = controlBar.getP_edge_CC();
				p_edge_CD = p_edge_CC*0.39;				
				controlBar.setP_edge_CC(p_edge_CC);	
				controlBar.setP_edge_CD(p_edge_CD);
				newPlayerSpawned = true;
				modelCounter = 0;
				evolutionComplete = false;
				controlBar.setP_edge_CC(p_edge_CC);
				
				playerNameSeed = 0;
				evolutionCounter = 0;
				
				controlBar.getStartButton().setDisable(false);
			}
		});

		scene = new Scene(root, 1024, 500);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		mainStage.setScene(scene);
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		mainStage.setX(primaryScreenBounds.getMinX());
		mainStage.setY(primaryScreenBounds.getMinY());
		mainStage.setWidth(primaryScreenBounds.getWidth());
		mainStage.setHeight(primaryScreenBounds.getHeight());
		mainStage.show();

		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {

				Layout layout = null;				

				if (newPlayerSpawned) {
					graph.addPlayerToModel(++playerNameSeed, p_edge_CC, p_edge_CD);
					--numNewPlayersNeeded;
					graph.getModel().merge();
					layout = new CircleLayout();
					layout.execute(graph);
					if (numNewPlayersNeeded < 1) {						
						graph.endUpdate();
						newPlayerSpawned = false;
						modelComplete = true;	
					}
					
				} else if(modelComplete){
					// here the evolution takes place
					// play a round of prisoners dilemma before each evolution and record the scores accumulated
					playGame();
					graph.getModel().captureStatistics();	
					
					if (!evolutionComplete) {
						// charge the players the cost of the game
						// check which players can spawn
						++evolutionCounter;
						evolveModel();
						layout = new CircleLayout();
						layout.execute(graph);
						timeline.pause();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						timeline.play();						

						if (evolutionCounter == numEvolutionsToRun 
								|| graph.getModel().getAllPlayers().isEmpty() 
								|| graph.getModel().getAllPlayers().size() > 150) {

							evolutionComplete = true;
							modelComplete = false;
							newPlayerSpawned = false;						
						}

					}
					
					
				} else if (modelCounter < numModelsToBuild) {
					
					timeline.pause();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeline.play();
					
					// build the next model
					// increment the numModelsCounter
					System.out.println();
					++modelCounter;
					// reset newPlayerSpawned
					newPlayerSpawned = true;
					evolutionComplete = false;
					numNewPlayersNeeded = controlBar.getNumPlayersPerModel();
					controlBar.setP_edge_CC(p_edge_CC);
					
					playerNameSeed = 0;
					evolutionCounter = 0;
					graph.resetGraph();

				} else {
					simulationComplete = true;
				}

				graph.getModel().merge();

				controlBar.setNumPlayers(graph.getModel().getNumPlayers(), controlBar.getNumPlayersPerModel());
				controlBar.setNumCoops((int) (((double) graph.getModel().getNumCooperators() / graph.getModel().getNumPlayers()) * 100));
				controlBar.setNumDefs((int) (((double) graph.getModel().getNumDefectors()/ graph.getModel().getNumPlayers()) * 100));
				controlBar.setModelProgress((double)graph.getModel().getNumPlayers()/controlBar.getNumPlayersPerModel());
				controlBar.updateRatios(graph.getModel().getPercentageCooperators(), 
						100.0-graph.getModel().getPercentageCooperators(), 
						graph.getModel().getCC_Percentage(), 
						graph.getModel().getDD_Percentage(), 
						graph.getModel().getCD_Percentage());
				controlBar.setModelNum(modelCounter);
				controlBar.setNumEvolutions(evolutionCounter);
				if (simulationComplete) {
					timeline.stop();
				}
			}
		};

		keyFrame = new KeyFrame(duration, onFinished);

	}
	
	void reportStatus(){
		
		System.out.println();
		System.out.println("Model: "+modelCounter +" --> Coops " +graph.getModel().getNumCooperators()+ " : Defs "+graph.getModel().getNumDefectors());
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private void evolveModel() {
		// int numNewPlayersNeeded = 0;

		// decrement scores
		for (Player player : graph.getModel().getAllPlayers()) {
			player.decrementPlayerScore(gameCost*evolutionCounter);
			player.incrementAge();
		}

		// check what players can spawn and which are eliminated
		List<Player> removedPlayers = new ArrayList<Player>();
		List<Edge> removedEdges = new ArrayList<Edge>();

		for (Player player : graph.getModel().getAllPlayers()) {

			// which players can spawn
			if (player.getScore() > (spawnThreshold*evolutionCounter)) {
				//model.getSpawnedPlayers().add(player);
				//System.out.println("Player "+player.getid );
				player.scaleUpPlayer();
				player.setScore(10);
				++numNewPlayersNeeded;
				newPlayerSpawned = true;
			}

			// which players get eliminated
			if (player.getScore() < 0) {
				if (player instanceof Cooperator) {
					graph.getModel().decrementNumCooperators();
				} else {
					graph.getModel().decrementNumDefectors();
				}
				graph.getModel().getDeadPlayers().add(player);
				removedPlayers.add(player);

				for (Edge edge : graph.getModel().getAllEdges()) {
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
				graph.getModel().decrementNumCCEdges();
			} else if (edge.getTarget() instanceof Defector && edge.getSource() instanceof Defector) {
				graph.getModel().decrementNumDDEdges();
			} else {
				graph.getModel().decrementNumCDEdges();
			}
		}
		graph.getModel().getRemovedEdges().addAll(removedEdges);
		graph.getModel().getRemovedPlayers().addAll(removedPlayers);

		//graph.getModel().getAllEdges().removeAll(removedEdges);
		//graph.getModel().getAllPlayers().removeAll(removedPlayers);
		// System.gc();
		graph.endUpdate();
	}


	public void resetScores() {
		for (Player player : graph.getModel().getAllPlayers()) {
			player.setScore(0);
		}
	}

	public void playGame() {
		for (Edge edge : graph.getModel().getAllEdges()) {
			pdGame.playGame(edge.getSource(), edge.getTarget());
		}
	}

	public void addPlayerToModel(int playerID, double p_edge_CC, double p_edge_CD) {

		// Model model = graph.getModel();
		// beginUpdate();
		Player player = null;
		double coinFlip = Math.random() * 2;
		// double coinFlip = numNewPlayersNeeded%2;
		// double coinFlip = numNewPlayersNeeded-modelCounter;
		// if ((int) coinFlip < 0) {
		if ((int) coinFlip == 0) {
			player = new Cooperator(String.valueOf(playerID));
			graph.getModel().incrementNumCooperators();
			graph.getModel().addNewPlayer(player);
			for (Player c : graph.getModel().getAllPlayers()) {
				double createEdge = Math.random();
				// if both are coops
				if (c instanceof Cooperator) {
					if (createEdge < p_edge_CC) {
						graph.getModel().addEdge(player.getPlayerId(), c.getPlayerId());
					}
					// the other must be a defector
				} else {
					if (createEdge < p_edge_CD) {
						graph.getModel().addEdge(player.getPlayerId(), c.getPlayerId());
					}
				}

			}

		} else {
			player = new Defector(String.valueOf(playerID));
			graph.getModel().incrementNumDefectors();
			graph.getModel().addNewPlayer(player);
			for (Player c : graph.getModel().getAllPlayers()) {
				double createEdge = Math.random();
				// if both are defectors
				if (c instanceof Defector) {
					if (createEdge < p_edge_CC) {
						graph.getModel().addEdge(player.getPlayerId(), c.getPlayerId());
					}
					// the other must be a cooperator
				} else {
					if (createEdge < p_edge_CD) {
						graph.getModel().addEdge(player.getPlayerId(), c.getPlayerId());
					}
				}
			}
		}
	}

//	public static void mutatePlayer() {
//
//		Player playerToMutate = model.getPlayerQueue().poll();
//		// create a new cooperator
//		Player player = new Cooperator(playerToMutate.getPlayerId());
//		// System.out.println("Mutating player "+playerToMutate.getPlayerId());
//		// TODO: this is a really nasty way of doing this, change to hashmap
//		model.getAllPlayers().add(model.getAllPlayers().indexOf(playerToMutate), player);
//		model.getAllPlayers().remove(playerToMutate);
//
//		model.getAllEdges().removeAll(playerToMutate.getPlayersEdges());
//
//		model.getPlayerMap().put(player.getPlayerId(), player);
//		model.getEdgeMap().put(player, new ArrayList<Player>());
//
//		for (Player otherplayer : model.getEdgeMap().get(playerToMutate)) {
//			// System.out.println("Connected to " + otherplayer);
//			// adjust edge ratio counts
//			if (otherplayer instanceof Defector) {
//				// if the other player is a defector
//				model.decrementNumDDEdges();
//				// --numDDEdges;
//			} else {
//				// the other player must be a cooperator
//				model.decrementNumCDEdges();
//				// --numCDEdges;
//			}
//			otherplayer.getPlayersEdges().remove(playerToMutate);
//			model.getEdgeMap().get(otherplayer).remove(playerToMutate);
//			Edge edge = model.addEdge(player.getPlayerId(), otherplayer.getPlayerId());
//
//		}
//
//		model.getAllEdges().addAll(model.getAddedEdges());
//		model.getAddedEdges().clear();
//		model.getEdgeMap().remove(playerToMutate);
//
//		model.incrementNumCooperators();// numCooperators;
//		model.decrementNumDefectors();// --numDefectors;
//		// return null;
//	}

}

