package graphenvironment;

import java.util.ArrayList;

import graphcomponents.Cooperator;
import graphcomponents.Defector;
import graphcomponents.Edge;
import graphcomponents.Player;
import graphcomponents.PlayerLayer;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class Graph {

    private Model model;

    private Group canvas;

    private ZoomableScrollPane scrollPane;
    MouseGestures mouseGestures;

    PlayerLayer playerLayer;

    public Graph() {

        model = new Model();
        canvas = new Group();
        playerLayer = new PlayerLayer();
        canvas.getChildren().add(playerLayer);
        mouseGestures = new MouseGestures(this);
        scrollPane = new ZoomableScrollPane(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
    }

    public void resetGraph(){
    	model = new Model();
    	canvas.getChildren().remove(playerLayer);
    	playerLayer = new PlayerLayer();    	
        canvas.getChildren().add(playerLayer);    	
    }	
    
    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getPlayerLayer() {
        return this.playerLayer;
    }

    public Model getModel() {
        return model;
    }
//
//    public void beginUpdate() {
//    }

	public void addPlayerToGraph(int playerID, double p_edge) {

		//Model model = graph.getModel();
		//beginUpdate();
		Player player = null;
		double coinFlip = Math.random() * 2;
		if ((int) coinFlip == 0) {
			player = new Cooperator(String.valueOf(playerID));
			model.incrementNumCooperators();

		} else {
			player = new Defector(String.valueOf(playerID));
			model.incrementNumDefectors();
		}

		model.addNewPlayer(player);

		for (Player c : model.getAllPlayers()) {
			double createEdge = Math.random();
			if (createEdge < p_edge) {
				model.addEdge(player.getPlayerId(), c.getPlayerId());
			}
		}
		endUpdate();

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
		
		endUpdate();
	}
	
	
	public void mutatePlayer(){
		//Find the player to mutate ******queue holds only the defectors in descending order of edges,
//		Player playerToMutate = null;
//		for(Player player : model.getAllPlayers()){
//			if(player instanceof Defector){
//				playerToMutate = player;
//				
//				break;
//			}
//		}
		
		Player playerToMutate = getModel().getPlayerQueue().poll();
		//create a new cooperator
		Player player = new Cooperator(playerToMutate.getPlayerId());
		//System.out.println("Mutating player "+playerToMutate.getPlayerId());
    	// TODO: this is a really nasty way of doing this, change to hashmap
    	model.getAllPlayers().add(model.getAllPlayers().indexOf(playerToMutate), player);
    	model.getAllPlayers().remove(playerToMutate); 
    	
    	model.getAllEdges().removeAll(playerToMutate.getPlayersEdges());
    	
    	
		// remove old player from the graph
    	getPlayerLayer().getChildren().removeAll(playerToMutate.getPlayersEdges());
		getPlayerLayer().getChildren().remove(playerToMutate);
		getModel().disconnectFromGraphParent(playerToMutate);
		// add the new player to the graph
		getPlayerLayer().getChildren().add(player);
		getModel().attachOrphanToGraphParent(player);
		
    	getModel().getPlayerMap().put( player.getPlayerId(), player);
    	getModel().getEdgeMap().put(player, new ArrayList<Player>());
    	
		for(Player otherplayer : model.edgeMap.get(playerToMutate)){							
			//System.out.println("Connected to " + otherplayer);
			// adjust edge ratio counts
			if(otherplayer instanceof Defector){
        		//if the other player is a defector
				model.decrementNumDDEdges();
        		//--numDDEdges;
        	} else {
        		// the other player must be a cooperator
        		model.decrementNumCDEdges();
        		//--numCDEdges;
        	} 	
			otherplayer.getPlayersEdges().remove(playerToMutate);
			model.edgeMap.get(otherplayer).remove(playerToMutate);
			Edge edge = model.addEdge(player.getPlayerId(), otherplayer.getPlayerId());
			if(edge!=null){
				getPlayerLayer().getChildren().add(edge);
			}
			
		}
		
    	model.getAllEdges().addAll(model.addedEdges);
    	model.addedEdges.clear();
    	model.edgeMap.remove(playerToMutate);
    	
		model.incrementNumCooperators();//numCooperators;
		model.decrementNumDefectors();//--numDefectors;
    	//return null;
	}
    
    public void endUpdate() {

        // add components to graph pane
        getPlayerLayer().getChildren().addAll(model.getAddedEdges());
        getPlayerLayer().getChildren().addAll(model.getAddedPlayers());

        // remove components from graph pane
        //getPlayerLayer().getChildren().removeAll(model.getRemovedPlayers());
        getPlayerLayer().getChildren().removeAll(model.getRemovedEdges());
        
        // every player must have a parent, if it doesn't, then the graphParent is
        // the parent
        getModel().attachOrphansToGraphParent(model.getAddedPlayers());

        // remove reference to graphParent
        getModel().disconnectFromGraphParent(model.getRemovedPlayers());

        // merge added & removed players with all players
        getModel().merge();
        
        scrollPane.zoomToFit(false);

    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

}
