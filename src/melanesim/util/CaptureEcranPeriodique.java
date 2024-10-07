package melanesim.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class CaptureEcranPeriodique {
	public static void main(String[] args) {
		Timer timer = new Timer();// Créer un objet Timer
		timer.schedule(new TimerTask() {// Planifier une tâche
			@Override
			public void run() {
				captureEcran();
			}
		}, 0, 600000); // 0 pour démarrer immédiatement, 600000ms pour dix minutes
	}

	public static void captureEcran() {
		try {
			Robot robot = new Robot();
			Rectangle tailleEcran = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(tailleEcran);// Capturer l'écran
			// Formatter la date et l'heure
			LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId
					.systemDefault());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
			String formattedDateTime = dateTime.format(formatter);

			String nomFichier = "fishingTraffic-" + formattedDateTime + ".jpg";// nom avec date et heure
			// Sauvegarder l'image sur le disque
			File fichier = new File(nomFichier);
			ImageIO.write(image, "JPG", fichier);
			System.out.println("Capture d'écran sauvegardée : " + fichier.getAbsolutePath());
		} catch (AWTException | IOException e) {
			System.err.println("Erreur lors de la capture d'écran : " + e.getMessage());
		}
	}
	public static void captureEcranPlankton(String dateStamp) {
		try {
			Robot robot = new Robot();
			Rectangle tailleEcran = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(tailleEcran);// Capturer l'écran
			// Ensure the directory exists
			String directoryPath = "data_output" + File.separator + "screenSave";
			File directory = new File(directoryPath);
			if (!directory.exists()) directory.mkdirs();
			// Construct the file path
			String nomFichier = directoryPath + File.separator + "plancton2021-" + dateStamp + ".jpg"; // nom avec date et heure
			// Sauvegarder l'image sur le disque
			File fichier = new File(nomFichier);
			ImageIO.write(image, "JPG", fichier);
		} catch (AWTException | IOException e) {
			System.err.println("Erreur lors de la capture d'écran : " + e.getMessage());
		}
	}
}