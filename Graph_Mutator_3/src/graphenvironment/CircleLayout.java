package graphenvironment;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import java.util.List;
import java.util.Random;

import graphcomponents.Player;
import graphenvironment.Graph;


public class CircleLayout extends Layout {
	
    //Graph graph;

    Random rnd = new Random();

    public CircleLayout() {

        //this.graph = graph;

    }

    public void execute(Graph graph) {

        List<Player> players = graph.getModel().getAllPlayers();

      
        
        // plots the Players in a circular fashion.
        
        double mver = graph.getScrollPane().getHeight();
        double height = mver/2;
        
        //int height = 300;
        double r = ((double)players.size()*5)+100;
        //double r = 200;
        
        double mhoriz = graph.getScrollPane().getWidth();
        double offset= mhoriz/2;
        
        
        //int offset = 200;
        
        double theta = 360/(double)players.size()+.01;
        int i = 0;
        for (Player player : players){
        	double xd = r*Math.cos(Math.toRadians(theta*i));
        	double yd = r*Math.sin(Math.toRadians(theta*i));
        	double x = xd + offset;
        	double y = height-yd;
        	//System.out.println(player.getPlayerId()+ "x= " +x+",y= "+y+ " : " + theta*i);
        	player.relocate(x, y);
        	++i;
        }
    }

}

