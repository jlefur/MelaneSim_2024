import numpy as np
import matplotlib.pyplot as plt
import os
from matplotlib.ticker import FormatStrFormatter

# Constantes
NB_LIGNES = 168
NB_COLONNES = 412

# Dossier contenant les fichiers .grd (le dossier courant)
FOLDER = "."

# Initialisation
grilles = []
valeurs_min = []
valeurs_max = []

# Lecture des 12 fichiers
for mois in range(1, 13):
    filename = os.path.join(FOLDER, f"chl-2021{mois:02d}.grd")
    if not os.path.exists(filename):
        print(f"[!] Fichier manquant : {filename}")
        continue

    with open(filename, "r", encoding="utf-8") as f:
        lignes = f.readlines()[5:]  # Ignorer les 5 premières lignes

    grille = []
    for ligne in lignes:
        # Nettoyage + découpage sur tabulation
        if ligne.strip() == "":
            continue
        row = [float(val.replace(",", ".")) if val.strip() != "" else np.nan
               for val in ligne.strip().split("\t")]
        grille.append(row)

    grille = np.array(grille)
    grilles.append((mois, grille))
    valeurs_min.append(np.nanmin(grille))
    valeurs_max.append(np.nanmax(grille))

# Déterminer min/max global pour l'échelle de couleurs uniforme
vmin = min(valeurs_min)
vmax = max(valeurs_max)
print(f"Échelle commune : min = {vmin:.1f}, max = {vmax:.1f}")

# Génération des images
for mois, grille in grilles:
    fig, ax = plt.subplots(figsize=(10, 6))
    contour = ax.contourf(grille, levels=20, cmap='viridis', vmin=vmin, vmax=vmax)
    ax.set_title(f"Grille mensuelle - 2021{mois:02d}", fontsize=16)
    ax.axis('off')

    # Barre de légende
    cbar = fig.colorbar(contour)
    # Forcer la plage visible de la légende
    cbar.set_label("Valeurs moyennes")
    cbar.set_ticks(np.linspace(vmin, vmax, 6))  # Par exemple : 6 graduations
    cbar.ax.yaxis.set_major_formatter(FormatStrFormatter('%.1f'))  # Un chiffre après la virgule
    cbar.set_label("Valeurs moyennes")
    cbar.ax.yaxis.set_major_formatter(FormatStrFormatter('%.1f'))

    # Sauvegarde en JPG
    output_filename = f"grille_2021_{mois:02d}.jpg"
    plt.savefig(output_filename, dpi=300, bbox_inches='tight')
    plt.close()
    print(f"✅ Image enregistrée : {output_filename}")
