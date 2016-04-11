package graphcomponents;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class Defector extends Player{
	
    public Defector ( String id) {

        super( id);
        double width = 20;
        double height = 20;
        
        shape = new Polygon( width / 2, 0, width, height, 0, height);
               
        shape.setStroke(Color.RED);
        shape.setFill(Color.RED);
        

        setView( shape);
    }
    
}
