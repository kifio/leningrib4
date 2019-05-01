package kifio.leningrib;

import kifio.leningrib.screens.GameScreen;

public class Utils {

    // Функция принимает на вход координаты нажатия и возвращает координаты тайла
    public static float mapCoordinate(float coordiante) {
        int c = (int) coordiante / GameScreen.tileSize;
        return (float) (c * GameScreen.tileSize);
    }
}
