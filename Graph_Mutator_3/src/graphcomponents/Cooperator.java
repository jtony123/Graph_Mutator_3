package graphcomponents;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Cooperator extends Player {


	public Cooperator(String id) {
		
		super(id);
		shape = new Rectangle(20, 20);

		shape.setStroke(Color.DODGERBLUE);
		shape.setFill(Color.DODGERBLUE);

		setView(shape);

	}
	

	


}
