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
public class DD_Edge extends Edge{

		/**
		 * 
		 */
		public DD_Edge(Player source, Player target) {
			// TODO Auto-generated constructor stub
			super(source, target);
			line.setStroke(Color.RED);
		}

}
