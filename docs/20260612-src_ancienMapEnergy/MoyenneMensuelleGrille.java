		import java.io.*;
		import java.nio.file.*;
		import java.time.LocalDate;
		import java.time.format.DateTimeFormatter;
		import java.util.*;

import data.constants.I_ConstantPNMC;

		public class MoyenneMensuelleGrille implements I_ConstantPNMC {

		    private static final int NB_LIGNES = 145;
		    private static final int NB_COLONNES = 223;
		    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		    public static void main(String[] args) throws IOException {
		        String cheminFichier = RASTER_PATH_MELANESIA + "20230525-microNecton_mnkc_epi_2021.csv"; // Remplace par ton fichier
		        Map<Integer, List<double[][]>> grillesParMois = new HashMap<>();

		        // === LECTURE DU FICHIER ===
		        try (BufferedReader reader = Files.newBufferedReader(Paths.get(cheminFichier))) {
		            String ligne;
		            while ((ligne = reader.readLine()) != null) {
		                String premiereColonne = ligne.split(";")[0].trim();
		                if (premiereColonne.matches("\\d{2}/\\d{2}/\\d{4}")) {
		                    LocalDate date = LocalDate.parse(premiereColonne, DATE_FORMAT);
		                    int mois = date.getMonthValue();

		                    double[][] grille = new double[NB_LIGNES][NB_COLONNES];

		                    for (int i = 0; i < NB_LIGNES; i++) {
		                        String[] valeurs = reader.readLine().split(";", -1); // -1 pour garder les vides
		                        for (int j = 0; j < NB_COLONNES; j++) {
		                            String cell = valeurs[j].trim();
		                            if (cell.equalsIgnoreCase("NaN") || cell.isEmpty()) {
		                                grille[i][j] = Double.NaN;
		                            } else {
		                                grille[i][j] = Double.parseDouble(cell);
		                            }
		                        }
		                    }

		                    grillesParMois.computeIfAbsent(mois, k -> new ArrayList<>()).add(grille);
		                }
		            }
		        }

		        // === CALCUL DES MOYENNES ===
		        for (int mois = 1; mois <= 12; mois++) {
		            List<double[][]> grilles = grillesParMois.get(mois);
		            if (grilles == null || grilles.isEmpty()) continue;

		            double[][] moyenne = new double[NB_LIGNES][NB_COLONNES];
		            int[][] compteurs = new int[NB_LIGNES][NB_COLONNES];

		            for (double[][] grille : grilles) {
		                for (int i = 0; i < NB_LIGNES; i++) {
		                    for (int j = 0; j < NB_COLONNES; j++) {
		                        double val = grille[i][j];
		                        if (!Double.isNaN(val)) {
		                            moyenne[i][j] += val;
		                            compteurs[i][j]++;
		                        }
		                    }
		                }
		            }

		            // Moyenne finale
		            for (int i = 0; i < NB_LIGNES; i++) {
		                for (int j = 0; j < NB_COLONNES; j++) {
		                    if (compteurs[i][j] > 0) {
		                        moyenne[i][j] /= compteurs[i][j];
		                    } else {
		                        moyenne[i][j] = Double.NaN;
		                    }
		                }
		            }

		            // === ÉCRITURE DU FICHIER CSV MOYENNE ===
		            String nomFichier = RASTER_PATH_MELANESIA + "microNectonCopernicus/" + String.format( "moyenne_mois_%02d.csv", mois);
		            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(nomFichier))) {
		                for (int i = 0; i < NB_LIGNES; i++) {
		                    StringBuilder sb = new StringBuilder();
		                    for (int j = 0; j < NB_COLONNES; j++) {
		                        if (j > 0) sb.append(";");
		                        double val = moyenne[i][j];
		                        sb.append(Double.isNaN(val) ? "NaN" : String.format(Locale.ROOT, "%.4f", val));
		                    }
		                    writer.write(sb.toString());
		                    writer.newLine();
		                }
		            }

		            System.out.println("✅ Fichier écrit : " + nomFichier);
		        }
		    }
		}
