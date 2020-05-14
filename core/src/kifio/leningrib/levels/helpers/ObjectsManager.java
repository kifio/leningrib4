package kifio.leningrib.levels.helpers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class ObjectsManager<T extends Actor> {

	public Array<T> gameObjects = new Array<>();
	public Label[] speeches;

	public void dispose() {
		if (gameObjects != null) {
			Iterator<T> iterator = gameObjects.iterator();
		 	while (iterator.hasNext()) {
				T next = iterator.next();
				if (next != null) {
					next.clear();
		 			next.remove();
				}
		 		iterator.remove();
			}
		 	gameObjects = null;
		}

		if (speeches != null) {
			for (int i = 0; i < speeches.length; i++) {
				if (speeches[i] != null) {
					speeches[i].clear();
					speeches[i].remove();
					speeches[i] = null;
				}
			}
			speeches = null;
		}
	}
}
