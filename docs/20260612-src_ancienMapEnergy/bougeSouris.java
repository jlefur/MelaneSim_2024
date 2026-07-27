import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;

public class bougeSouris {
    private static final Random r = new Random();

    /**
     * Déplace légèrement la souris en ajoutant un petit décalage aléatoire.
     */
    public void saveScreen() {
        try {
            Robot robot = new Robot();
            // Coordonnées actuelles de la souris
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            int x = mouseLocation.x;
            int y = mouseLocation.y;

            // Déplacement de la souris avec un léger offset aléatoire (0..2)
            robot.mouseMove(x + r.nextInt(3), y + r.nextInt(3));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée : déplace la souris périodiquement.
     * Args (optionnels) :
     *   [0] intervalle en millisecondes (par défaut 5000)
     *   [1] durée totale en secondes (par défaut 60, 0 = infini)
     */
    public static void main(String[] args) {
        long intervalMs = 5000; // 5 secondes par défaut
        long totalSeconds = 60; // 1 minute par défaut
        if (args.length > 0) {
            try { intervalMs = Long.parseLong(args[0]); } catch (NumberFormatException ignored) {}
        }
        if (args.length > 1) {
            try { totalSeconds = Long.parseLong(args[1]); } catch (NumberFormatException ignored) {}
        }

        bougeSouris app = new bougeSouris();
        long start = System.currentTimeMillis();
        System.out.println("bougeSouris démarre. Intervalle=" + intervalMs + " ms, durée=" + (totalSeconds==0?"∞":totalSeconds + " s"));
        while (true) {
            app.saveScreen();
            try { Thread.sleep(intervalMs); } catch (InterruptedException ignored) {}
            if (totalSeconds > 0 && (System.currentTimeMillis() - start) / 1000 >= totalSeconds) {
                break;
            }
        }
        System.out.println("bougeSouris terminé.");
    }
}
