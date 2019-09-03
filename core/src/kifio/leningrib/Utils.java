package kifio.leningrib;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.List;
import kifio.leningrib.screens.GameScreen;

public class Utils {

    // Функция принимает на вход координаты нажатия и возвращает координаты тайла
    public static float mapCoordinate(float coordiante) {
        int c = (int) coordiante / GameScreen.tileSize;
        return (float) (c * GameScreen.tileSize);
    }

    public static boolean isOverlapsWithActor(List<Actor> actors, int x, int y) {
        for (int k = 0; k < actors.size(); k++) {
            Actor a = actors.get(k);
            if ((int) a.getX() == x && (int) a.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOverlapsWithVector(Vector2[] points, int x, int y) {
        for (int k = 0; k < points.length; k++) {
            if ((int) points[k].x == x && (int) points[k].y == y) {
                return true;
            }
        }
        return false;
    }
}
