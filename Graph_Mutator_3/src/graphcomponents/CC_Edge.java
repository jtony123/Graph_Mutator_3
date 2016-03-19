/**
 * 
 */
package graphcomponents;

import javafx.scene.paint.Color;

/**
 * @author Anthony Jackson
 * @id 11170365
 *
 */
public class CC_Edge extends Edge{

	/**
	 * 
	 */
	public CC_Edge(Player source, Player target) {
		// TODO Auto-generated constructor stub
		super(source, target);
		line.setStroke(Color.DODGERBLUE);
	}

}
