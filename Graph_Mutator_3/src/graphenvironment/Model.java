package graphenvironment;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import graphcomponents.*;


public class Model {

    Player graphParent;

    List<Player> allPlayers;
    List<Player> addedPlayers;
    List<Player> removedPlayers;

    List<Edge> allEdges;
    List<Edge> addedEdges;
    List<Edge> removedEdges;

    Map<String,Player> playerMap; // <id,player>
    Map<Player,List<Player>> edgeMap;
    
    PriorityQueue<Player> playerQueue;
    
	int numCooperators = 0;
	int numDefectors = 0;
    
    int numCCEdges = 0;
    int numDDEdges = 0;
    int numCDEdges = 0;
    
	int coopScore = 0;
	int defScore = 0;
	double degree = 0;
    
    

    public Model() {

         graphParent = new Player( "_ROOT_");
         playerQueue = new PriorityQueue<Player>(10, edgeComparator);

         // clear model, create lists
         clear();
    }   
    
    
    /**
	 * @return the numCooperators
	 */
	public int getNumCooperators() {
		return numCooperators;
	}
	
	public void incrementNumCooperators() {
		++numCooperators;
	}
	
	public void decrementNumCooperators(){
		--numCooperators;
	}
	
	public double getPercentageCooperators(){
		if(numCooperators + numDefectors != 0){
			return (((double)numCooperators)/(numCooperators + numDefectors))*100;
		}
		return 0.0;
	}

	/**
	 * @return the numDefectors
	 */
	public int getNumDefectors() {
		return numDefectors;
	}
	
	public void incrementNumDefectors() {
		++numDefectors;
	}
	
	public void decrementNumDefectors(){
		--numDefectors;
	}
	
	public int getNumPlayers(){
		return numCooperators + numDefectors;
	}

	/**
	 * @return the edgeMap
	 */
	public Map<Player, List<Player>> getEdgeMap() {
		return edgeMap;
	}

	/**
	 * @param edgeMap the edgeMap to set
	 */
	public void setEdgeMap(Map<Player, List<Player>> edgeMap) {
		this.edgeMap = edgeMap;
	}

	
	/**
	 * @return the playerMap
	 */
	public Map<String, Player> getPlayerMap() {
		return playerMap;
	}

	public void clear() {

        allPlayers = new ArrayList<>();
        addedPlayers = new ArrayList<>();
        removedPlayers = new ArrayList<>();

        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();

        playerMap = new HashMap<>(); // <id,player>
        edgeMap = new HashMap<>();

    }

    public void clearAddedLists() {
        addedPlayers.clear();
        addedEdges.clear();
    }

    public List<Player> getAddedPlayers() {
        return addedPlayers;
    }

    public List<Player> getRemovedPlayers() {
        return removedPlayers;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }



    /**
	 * @return the coopScore
	 */
	public int getCoopScore() {
		return coopScore;
	}


	/**
	 * @return the defScore
	 */
	public int getDefScore() {
		return defScore;
	}


	/**
	 * @return the degree
	 */
	public double getDegree() {
		return degree;
	}


	/**
	 * @return the playerQueue
	 */
	public PriorityQueue<Player> getPlayerQueue() {
		return playerQueue;
	}


	/**
	 * @param playerQueue the playerQueue to set
	 */
	public void setPlayerQueue(PriorityQueue<Player> playerQueue) {
		this.playerQueue = playerQueue;
	}


	public void addNewPlayer (Player player){
    	addPlayer(player);
    }
    private void addPlayer( Player player) {
    	
        addedPlayers.add(player);

        playerMap.put( player.getPlayerId(), player);
        edgeMap.put(player, new ArrayList<Player>());
        

    }


    public Edge addEdge( String sourceId, String targetId) {

        Player sourcePlayer = playerMap.get( sourceId);
        Player targetPlayer = playerMap.get( targetId);
        Edge edge = null;
        
        if(!(sourcePlayer.equals(targetPlayer))){
        	// get this players edges
        	List<Player> targets = edgeMap.get(sourcePlayer);
        	// check if it already has an edge to the target, not sure if we need this
        	//if(!targets.contains(targetPlayer)){
        		// add the appropriate type of edge
        		if(sourcePlayer instanceof Cooperator && targetPlayer instanceof Cooperator){
            		edge = new CC_Edge(sourcePlayer, targetPlayer);
            		++numCCEdges;
            	} else if(sourcePlayer instanceof Defector && targetPlayer instanceof Defector){
            		edge = new DD_Edge(sourcePlayer, targetPlayer);
            		++numDDEdges;
            	} else {
            		edge = new CD_Edge(sourcePlayer, targetPlayer); 
            		++numCDEdges;
            	} 
        		addedEdges.add( edge);
        		targets.add(targetPlayer);
        		edgeMap.get(targetPlayer).add(sourcePlayer);
        		sourcePlayer.associateEdge(edge);
        		targetPlayer.associateEdge(edge);
           
        } 
        if(edge==null){
        	System.out.println("null edge");
        }
        return edge;
    }
       
    public Double getCC_CD_EdgeRatio(){
    	    	
//    	if(numCDEdges != 0){
//    		return (((double)(numCCEdges + numDDEdges))/2)/numCDEdges;
//    	}
//    	if(numCDEdges != 0){
//    		return ((double)(numCCEdges))/numCDEdges;
//    	}
    	if(numCCEdges + numDDEdges != 0 && numCDEdges != 0){
    		return (((double)numCDEdges)/(((double)(numCCEdges + numDDEdges))/2));
    	}
    	
    	return 0.5;	
    }
    
    public double getAverageCCDD(){
    	return ((double)(numCCEdges + numDDEdges))/2;
    }
    
    public double getCC_Percentage(){
    	
    	int total = numCCEdges + numDDEdges + numCDEdges;
    	if(total>0){
    		return (((double)numCCEdges)/total)*100;
    	}
    	return 0;
    }
    
   public double getDD_Percentage(){
    	
    	int total = numCCEdges + numDDEdges + numCDEdges;
    	if(total>0){
    		return (((double)numDDEdges)/total)*100;
    	}
    	return 0;
    }
   
   public double getCD_Percentage(){
   	
   	int total = numCCEdges + numDDEdges + numCDEdges;
   	if(total>0){
   		return (((double)numCDEdges)/total)*100;
   	}
   	return 0;
   }
    
    
    
    public void decrementNumDDEdges(){
    	--numDDEdges;
    }
    
    public void decrementNumCCEdges(){
    	--numCCEdges;
    }

    /**
	 * @return the numCCEdges
	 */
	public int getNumCCEdges() {
		return numCCEdges;
	}

	/**
	 * @return the numDDEdges
	 */
	public int getNumDDEdges() {
		return numDDEdges;
	}

	/**
	 * @return the numCDEdges
	 */
	public int getNumCDEdges() {
		return numCDEdges;
	}
	
	public void decrementNumCDEdges(){
		--numCDEdges;
	}

	/**
     * Attach all players which don't have a parent to graphParent 
     * @param playerList
     */
    public void attachOrphansToGraphParent( List<Player> playerList) {

        for( Player player: playerList) {
            if( player.getPlayerParents().size() == 0) {
                graphParent.addPlayerChild( player);
            }
        }

    }
    
    public void attachOrphanToGraphParent(Player player) {
        if( player.getPlayerParents().size() == 0) {
            graphParent.addPlayerChild( player);
        }        
    }

    /**
     * Remove the graphParent reference if it is set
     * @param playerList
     */
    public void disconnectFromGraphParent( List<Player> playerList) {

        for( Player player: playerList) {
            graphParent.removePlayerChild( player);
        }
    }
    
    public void disconnectFromGraphParent(Player player) {
            graphParent.removePlayerChild( player);        
    } 

    public void merge() {

        // players
        allPlayers.addAll( addedPlayers);
        allPlayers.removeAll( removedPlayers);

        addedPlayers.clear();
        removedPlayers.clear();
        
        // edges
        allEdges.addAll( addedEdges);
        allEdges.removeAll( removedEdges);

        addedEdges.clear();
        removedEdges.clear();

    }
    
    //Comparator anonymous class implementation
    public static Comparator<Player> edgeComparator = new Comparator<Player>(){
       
		@Override
		public int compare(Player p1, Player p2) {
			// TODO Auto-generated method stub
			return p2.getPlayersEdges().size() - p1.getPlayersEdges().size();
		}
    };



	/**
	 * 
	 */
	public void captureStatistics() {

		coopScore = 0;
		defScore = 0;
		degree = 0;
		for(Player player : allPlayers){
			degree += edgeMap.get(player).size();
			if(player instanceof Cooperator){
				coopScore += player.getScore();
			} else {
				defScore += player.getScore();
			}
		}
		degree = degree/allPlayers.size();		
	}
	
	public int getDistance(){
		return coopScore - defScore;
	}
    
}