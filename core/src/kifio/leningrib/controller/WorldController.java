package kifio.leningrib.controller;

import kifio.leningrib.model.*;

public class WorldController {

	public Player player = new Player(); 

	public void movePlayerTo(float x, float y) {
		player.moveTo(x, y);
    }

}