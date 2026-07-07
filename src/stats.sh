#!/bin/bash

# Date de référence
DATE="2024-11-01"

# Trouver tous les fichiers .java modifiés depuis le 01/09/2023
FILES=$(find . -name "*.java" -type f -newermt "$DATE")

# Compter le nombre de fichiers
NUM_FILES=$(echo "$FILES" | wc -l)

# Compter le nombre total de lignes dans ces fichiers
NUM_LINES=$(echo "$FILES" | xargs cat | wc -l)

# Compter les lignes contenant "marin"
NUM_MARIN_LINES=$(echo "$FILES" | xargs grep -H "marin" | wc -l)

# Afficher les résultats
echo "Fichiers .java modifiés depuis le $DATE : $NUM_FILES"
echo "Nombre total de lignes dans ces fichiers : $NUM_LINES"
echo "Nombre de lignes contenant 'marin' : $NUM_MARIN_LINES"
