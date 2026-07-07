package melanesim.util;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;

public class bougeSouris {
	public static void main(String[] args) {
		// bouge la souris toutes les 5 minutes
		try {
			Robot robot = new Robot();
			Random random = new Random();
			while (true) {
				// Laisse passer une minute (60000 millisecondes)
				Thread.sleep(30000);

				// Obtient les coordonnées actuelles de la souris
				Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
				int x = mouseLocation.x;
				int y = mouseLocation.y;

				// Déplace la souris légèrement de façon aléatoire
				robot.mouseMove(x + random.nextInt(10), y + random.nextInt(10));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}