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
import thing.ground.landscape.C_Landscape;

/** read an ASCII raster (compatible surfer) from MelaneSim and return a grid in SimMasto format
 * @see C_Landscape
 * @author Quentin Baduel, 2008, rev. JLF 2015, 2024 */
public class C_ReadRasterOcean {
	/** Download grid in ASCII text format : 1st line "DSAA" <br>
	 * FOR OCEAN RASTERS (compatible surfer): 2nd line number of columns number of rows / 3rd line min and max x / 4th line min
	 * and max y / 5th line: min and max of values / remaining: line 0: column 0 to j-1 (delimiter blank space) line i line i-1
	 * @param url
	 * @return matrix of affinities (or whatever) */
	public static int[][] txtRasterLoader(String url) {
		// on charge le fichier d'apr�s l'url donn�e en param�tre
		File fichier_raster = new File(url);
		String chaine = null;
		StringTokenizer st;
		int[][] matrice = null;
		try {
			// on crée un flux de lecture du fichier
			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(fichier_raster)));
			// on crée un lecteur de flux
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader lecteur = new BufferedReader(isr);
			lecteur.readLine();// DSAA
			int largeur, hauteur;
			st = new StringTokenizer(lecteur.readLine());
			largeur = Integer.parseInt((st.nextToken()));
			hauteur = Integer.parseInt((st.nextToken()));
			lecteur.readLine();
			lecteur.readLine();

			matrice = new int[largeur][hauteur];
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
					matrice[j][i] = Integer.parseInt((st.nextToken())); // OCEAN
					j++;
				}
				j = 0;
				i++;
			}
			lecteur.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matrice;
	}

}
