package data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import data.constants.I_ConstantPNMC_particules;
import melanesim.protocol.A_Protocol;

/** Extension of C_ReadRaster to read surfer compatible matrix of double values (e.g., ocean environment)
 * @see C_ReadRaster
 * @author JLF 2024 */
public class C_ReadRasterDouble implements I_ConstantPNMC_particules {

	/** Download grid in ASCII text format : 1st line "DSAA" / 2nd line number of columns number of rows / 3rd line min and max x
	 * / 4th line min and max y / 5th line: min and max of values / remaining: line 0: column 0 to j-1 (delimiter blank space) to
	 * line i-1
	 * @param url
	 * @return matrix of affinities (or whatever) */
	public static double[][] doubleRasterLoader(String url) {
		File fichier_raster = new File(url);
		String chaine = null;
		StringTokenizer st;
		double[][] matrice = null;
		try {
			// on crée un flux de lecture du fichier
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fichier_raster)));
			// on crée un lecteur de flux
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader lecteur = new BufferedReader(isr);
			lecteur.readLine();// DSAA
			int largeur = 0, hauteur = 0;
			st = new StringTokenizer(lecteur.readLine());
			largeur = Integer.parseInt((st.nextToken()));
			hauteur = Integer.parseInt((st.nextToken()));
			lecteur.readLine();
			lecteur.readLine();
			matrice = new double[largeur][hauteur];
			lecteur.readLine();
			int i = 0;
			int j = 0;
			// tant qu'il y a des lignes à lire :
			while ((chaine = lecteur.readLine()) != null) {
				// on récupère une ligne ...
				st = new StringTokenizer(chaine);
				// ... tant qu'elle a des éléments
				while (st.hasMoreElements()) {
					// on lit l'entier correspondant et on l'enregistre dans la matrice.
					matrice[j][i] = Double.parseDouble(st.nextToken()); // OCEAN
					// matrice[j][hauteur - i - 1] = Integer.parseInt((st.nextToken())); // RODENTS
					j++;
				}
				j = 0;
				i++;
			}
			lecteur.close();
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_ReadRasterDouble.doubleRasterLoader(): matrix read: ", largeur + " x " + hauteur
						+ " From :  " + url + " lue", isNotError);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrice;
	}
}
