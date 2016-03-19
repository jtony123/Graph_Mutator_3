package graphcomponents;


/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */


import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Edge extends Group {

    protected Player source;
    protected Player target;

    
    Line line;

    public Edge(Player source, Player target) {

        this.source = source;
        this.target = target;

        //source.addPlayerChild(target);
        //target.addPlayerParent(source);
        
        line = new Line();

        line.startXProperty().bind( source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0));
        line.startYProperty().bind( source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0));

        line.endXProperty().bind( target.layoutXProperty().add( target.getBoundsInParent().getWidth() / 2.0));
        line.endYProperty().bind( target.layoutYProperty().add( target.getBoundsInParent().getHeight() / 2.0));
        line.setStrokeLineJoin(StrokeLineJoin.ROUND);
        
        //line.setStroke(getColor(source, target));
        getChildren().add( line);
        //getChildren().add( a1);

    }

    public Player getSource() {
        return source;
    }

    public Player getTarget() {
        return target;
    }
 

}
