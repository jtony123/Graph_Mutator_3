/**
 * 
 */
package engines;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.List;

import games.PrisonersDilemmaGame;
import graphcomponents.Edge;
import graphcomponents.Player;
import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.PlayerType;
import graphenvironment.CircleLayout;
import graphenvironment.ControlBar;
import graphenvironment.Graph;
import graphenvironment.Layout;
import graphenvironment.Model;
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

public class SimulatorUI extends Application {

	// TODO:

	//double[] p_edges = {0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,0.1,0.15,0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55,0.6, 0.65,0.7, 0.75,0.8,0.85,0.9,0.95,1.0};
	int numNewPlayersNeeded = 65;
	//int numPlayersCounter = 0;
//	int numCooperators = 0;
//	int numDefectors = 0;
	int numModelsToBuild = 0;
	int modelCounter = 1;
	int roundsPlayed = 0;
	private int playerNameSeed = 0;
	private int numMutationsCounter = 0;

	private boolean newPlayerSpawned = true;
	private boolean modelComplete = false;
	private boolean mutationComplete = false;
	private boolean simulationComplete = false;

	private static double timeDelay = 200;
	private static double p_edge = 1;

	Graph graph = new Graph();
	BorderPane root = new BorderPane();
	PrisonersDilemmaGame pdGame = new PrisonersDilemmaGame();
	OutputEngine outputEngine;

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
		//outputEngine = new OutputEngine("", "mutator", headers);
		
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
				
				//numNewPlayersNeeded = (modelCounter*10-5)%50;
				numNewPlayersNeeded = controlBar.getNumPlayersPerModel();//65;
				controlBar.setNumPlayersPerModel(numNewPlayersNeeded);
				p_edge = controlBar.getP_edge_CC();
				//p_edge = (double)(((modelCounter+19)/20)*20)/(numModelsToBuild*10);
				//p_edge = p_edges[((int)(double)(((modelCounter+9)/10)*10)/10)-1];//(double)(((modelCounter+19)/20)*20)/(numModelsToBuild*10);
				//p_edge = (double)((((modelCounter+19))/20)*20)/(numModelsToBuild*2);
				
				controlBar.setP_edge_CC(p_edge);				
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
				roundsPlayed = 0;
				newPlayerSpawned = true;
				controlBar.getStartButton().setDisable(false);
				// outputEngine_1 = new OutputEngine_1(null);
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
					graph.addPlayerToGraph(++playerNameSeed, p_edge);
					--numNewPlayersNeeded;
					layout = new CircleLayout();
					layout.execute(graph);
					if (numNewPlayersNeeded < 1) {						
						graph.endUpdate();
						newPlayerSpawned = false;
						modelComplete = true;
						//outputEngine.saveStat(new Object[]{modelCounter, graph.getModel().getNumPlayers() ,numMutationsCounter, graph.getModel().getNumCooperators(), graph.getModel().getNumDefectors()});
						
						
						// model complete, now put the defectors into the priority queue
						for(Player player : graph.getModel().getAllPlayers()){
							if(player instanceof Defector){
								graph.getModel().getPlayerQueue().add(player);
							}
						}
						
						// escape loop in the case where there are no defectors to put in the queue
						// not needed, if all cooperators then ratio should be satisfied
						
						
					}
					
				} else if(modelComplete){
					// here the mutation takes place
					// play a round of prisoners dilemma before each mutation and record the scores accumulated
					//reportStatus();	
					
					// check the ratio of CCedges to CD edges
					//if(graph.getModel().getCC_CD_EdgeRatio() < 4.0/5.0){
					if(((double)(graph.getModel().getNumCCEdges())*4) < ((double)graph.getModel().getNumCDEdges()*5)){
						
						System.out.println("CC "+(graph.getModel().getNumCCEdges()*4)+" : CD " + ((double)graph.getModel().getNumCDEdges()*5));
						
						
						++numMutationsCounter;
						graph.mutatePlayer();
						layout = new CircleLayout();
						layout.execute(graph);
						timeline.pause();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						timeline.play();
						
					
					} else {
						//System.out.println("Model: "+numModelsCounter+ "  --> #Cooperators: " + graph.getModel().getNumCooperators() + "  #Defector: " + graph.getModel().getNumDefectors() + " ----- #CC: " + graph.getModel().getNumCCEdges()+" #DD " + graph.getModel().getNumDDEdges()+" #CD " + graph.getModel().getNumCDEdges());
						
						modelComplete = false;
						for(Player player : graph.getModel().getAllPlayers()){
							player.setScore(0);
						}
						
						for(Edge edge : graph.getModel().getAllEdges()){
							pdGame.playGame(edge.getSource(), edge.getTarget());
						}
						int coopScore = 0;
						int defScore = 0;					
						for(Player player : graph.getModel().getAllPlayers()){
							if(player instanceof Cooperator){
								coopScore += player.getScore();
							} else {
								defScore += player.getScore();
							}
						}
//						outputEngine.saveStat(new Object[]{modelCounter, 
//								graph.getModel().getNumPlayers(), 
//								p_edge, 
//								numMutationsCounter, 
//								graph.getModel().getNumCooperators()-graph.getModel().getNumDefectors(), 
//								coopScore-defScore,
//								graph.getModel().getNumCCEdges(),
//								graph.getModel().getNumDDEdges(),
//								graph.getModel().getNumCDEdges()});
						reportStatus();
					}
					
					
					
				} else if (modelCounter < numModelsToBuild) {
					// build the next model
					// increment the numModelsCounter
					System.out.println();
					++modelCounter;
					// reset newPlayerSpawned
					newPlayerSpawned = true;
					// reset numNewPlayersNeeded
					// ********************************************increasing number of players per model
					//numNewPlayersNeeded = (modelCounter*10-5)%50;
					//numNewPlayersNeeded = 65;
					numNewPlayersNeeded = controlBar.getNumPlayersPerModel();
					//controlBar.setNumPlayersPerModel(numNewPlayersNeeded);
					//p_edge = (double)(((modelCounter+9)/10)*10)/numModelsToBuild;
					//p_edge = (double)(((modelCounter+19)/20)*20)/(numModelsToBuild*10);
					//p_edge = p_edges[((int)(double)(((modelCounter+9)/10)*10)/10)-1];
					//p_edge = (double)((((modelCounter+19))/20)*20)/(numModelsToBuild*2);
					controlBar.setP_edge_CC(p_edge);
					
					playerNameSeed = 0;
					numMutationsCounter = 0;
					graph.resetGraph();
					//controlBar.setRatios(0,0,0,0,0);

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
				controlBar.setNumEvolutions(numMutationsCounter);
				if (simulationComplete) {
					// outputEngine_1.shutCSVWriter();
					timeline.stop();
					//outputEngine.shutCSVWriter();
				}
			}
		};

		keyFrame = new KeyFrame(duration, onFinished);

	}
	
	void reportStatus(){
//		for(Player player : graph.getModel().getAllPlayers()){
//			player.setScore(0);
//		}
//		
//		for(Edge edge : graph.getModel().getAllEdges()){
//			pdGame.playGame(edge.getSource(), edge.getTarget());
//		}
		
//		int coopScore = 0;
//		int defScore = 0;					
//		for(Player player : graph.getModel().getAllPlayers()){
//			if(player instanceof Cooperator){
//				coopScore += player.getScore();
//			} else {
//				defScore += player.getScore();
//			}
//		}
		
		//int totalscorefromplayers = coopScore + defScore;
		
		System.out.println();
		System.out.println("Model: "+modelCounter +" --> Coops " +graph.getModel().getNumCooperators()+ " : Defs "+graph.getModel().getNumDefectors());
		
	
	
	}

	public static void main(String[] args) {
		launch(args);
	}
}
