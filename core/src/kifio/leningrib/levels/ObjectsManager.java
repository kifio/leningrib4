package kifio.leningrib.levels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ObjectsManager<T extends Actor> {

	protected List<T> gameObjects;
	protected Random random;

	public void dispose() {
		if (gameObjects != null) {
			Iterator<T> iterator = gameObjects.iterator();
		 	while (iterator.hasNext()) {
		 		iterator.next().remove();
		 		iterator.remove();
			}
		 	gameObjects = null;
		}

		random = null;
	}
}
