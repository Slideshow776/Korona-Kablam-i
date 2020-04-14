package no.sandramoen.koronakablami.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.sandramoen.koronakablami.KoronaKablamIGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Korona Kablam-i!";
		float scale = 2.0f;
		config.width = (int) (1440 / scale);
		config.height = (int) (2560 / scale);
		config.resizable = false;
		new LwjglApplication(new KoronaKablamIGame(), config);
	}
}
