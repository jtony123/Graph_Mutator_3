package graphcomponents;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Player extends Pane {

	String playerId;
	
	int score = 0;
	int age = 0;
	Node view;
	List<Player> children = new ArrayList<>();
	List<Player> parents = new ArrayList<>();
	List<Edge> edges = new ArrayList<>();

	Shape shape;
	
	public Player(String playerId) {
		this.playerId = playerId;
	}

	public void associateEdge(Edge edge){
		edges.add(edge);
	}
	
	public List<Edge> getPlayersEdges(){
		return edges;
	}



	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	public void addToScore(int score) {
		this.score += score;
	}
	
	public void incrementAge(){
		++this.age;
	}
	
	public int getAge(){
		return this.age;
	}
	/**
	 * @param gameCost
	 */
	public void decrementPlayerScore(int gameCost) {
		this.score -= gameCost;		
	}

	public void addPlayerChild(Player player) {
		children.add(player);
	}

	public List<Player> getPlayerChildren() {
		return children;
	}

	public void addPlayerParent(Player player) {
		parents.add(player);
	}

	public List<Player> getPlayerParents() {
		return parents;
	}

	public void removePlayerChild(Player player) {
		children.remove(player);
	}

	public void setView(Node view) {

		this.view = view;
		getChildren().add(view);

	}

	public Node getView() {
		return this.view;
	}

	public String getPlayerId() {
		return playerId;
	}




}
