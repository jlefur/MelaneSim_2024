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
		listerEcrans();
		Timer timer = new Timer();// Cr�er un objet Timer
		timer.schedule(new TimerTask() {// Planifier une t�che
			@Override
			public void run() {
				captureEcran2("test");
			}
		}, 0, 600000); // 0 pour d�marrer imm�diatement, 600000ms pour dix minutes
	}

	public static void captureEcran() {
		try {
			Robot robot = new Robot();
			Rectangle tailleEcran = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(tailleEcran);// Capturer l'�cran
			// Formatter la date et l'heure
			LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId
					.systemDefault());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
			String formattedDateTime = dateTime.format(formatter);

			String nomFichier = "fishingTraffic-" + formattedDateTime + ".jpg";// nom avec date et heure
			// Sauvegarder l'image sur le disque
			File fichier = new File(nomFichier);
			ImageIO.write(image, "JPG", fichier);
			System.out.println("Capture d'�cran sauvegard�e : " + fichier.getAbsolutePath());
		} catch (AWTException | IOException e) {
			System.err.println("Erreur lors de la capture d'�cran : " + e.getMessage());
		}
	}
	public static void captureEcranPlankton(String dateStamp) {
		try {
			Robot robot = new Robot();
			Rectangle tailleEcran = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage image = robot.createScreenCapture(tailleEcran);// Capturer l'�cran
			// Ensure the directory exists
			String directoryPath = "data_output" + File.separator + "screenSave";
			File directory = new File(directoryPath);
			if (!directory.exists()) directory.mkdirs();
			// Construct the file path
			String nomFichier = directoryPath + File.separator + dateStamp + ".jpg"; // nom avec date et heure
			// Sauvegarder l'image sur le disque
			File fichier = new File(nomFichier);
			ImageIO.write(image, "JPG", fichier);
		} catch (AWTException | IOException e) {
			System.err.println("Erreur lors de la capture d'�cran : " + e.getMessage());
		}
	}

	public static void listerEcrans() {
		GraphicsDevice[] ecrans = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		System.out.println("Écrans détectés : " + ecrans.length);

		for (int i = 0; i < ecrans.length; i++) {
			Rectangle zone = ecrans[i].getDefaultConfiguration().getBounds();
			System.out.println("Écran " + (i + 1) + " : " + zone.width + "x" + zone.height + " position (x=" + zone.x
					+ ", y=" + zone.y + ")");
		}
	}

	public static BufferedImage captureEcran(int indexEcran) throws AWTException {
		GraphicsDevice[] ecrans = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if (indexEcran < 1 || indexEcran > ecrans.length) {
			throw new IllegalArgumentException("Numéro d'écran invalide : " + indexEcran + ". Écrans disponibles : 1 à "
					+ ecrans.length);
		}
		Rectangle zone = ecrans[indexEcran - 1].getDefaultConfiguration().getBounds(); // Récupère la zone de l’écran choisi
		Robot robot = new Robot(); // IMPORTANT : utiliser Robot() sans argument pour capturer les coordonnées négatives
		return robot.createScreenCapture(zone);
	}

	public static void captureEcran2(String dateStamp) {
		try {
			// Récupère tous les écrans connectés
			GraphicsDevice[] ecrans = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			// Vérifie qu'il y a au moins 2 écrans
			if (ecrans.length < 2) {
				System.err.println("Deux écrans ne sont pas disponibles !");
				return;
			}
			GraphicsDevice deuxiemeEcran = ecrans[1];// Sélectionne le 2ᵉ écran (index 1)
			Rectangle zoneEcran2 = deuxiemeEcran.getDefaultConfiguration().getBounds();// Récupère la zone du 2ᵉ écran
			// Robot robot = new Robot(deuxiemeEcran); // Crée le robot associé au 2ᵉ écran
			// BufferedImage image = robot.createScreenCapture(zoneEcran2); // Capture du 2ᵉ écran
			BufferedImage image = captureEcran(1); // Capture du 2ᵉ écran
			// Robot robot = new Robot();
			// Rectangle tailleEcran = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			// BufferedImage image = robot.createScreenCapture(tailleEcran);// Capturer l'�cran
			// Ensure the directory exists
			String directoryPath = "data_output" + File.separator + "screenSave";
			File directory = new File(directoryPath);
			if (!directory.exists()) directory.mkdirs();
			// Construct the file path
			String nomFichier = directoryPath + File.separator + dateStamp + ".jpg"; // nom avec date et heure
			// Sauvegarder l'image sur le disque
			File fichier = new File(nomFichier);
			ImageIO.write(image, "JPG", fichier);
		} catch (AWTException | IOException e) {
			System.err.println("Erreur lors de la capture d'�cran : " + e.getMessage());
		}
	}
}