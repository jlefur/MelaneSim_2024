#!/bin/bash

# Date de référence
DATE="2024-11-01"

# 1. Trouver tous les fichiers .java modifiés depuis la date
# 2. Exclure ceux qui contiennent "rodent"
FILES=$(find . -name "*.java" -type f -newermt "$DATE" \
  -exec grep -L "rodent" {} \;)

# 3. Afficher la liste des fichiers
echo "Liste des fichiers .java modifiés depuis le $DATE et ne contenant pas 'rodent' :"
echo "$FILES"
echo

# 4. Compter le nombre de fichiers
NUM_FILES=$(echo "$FILES" | wc -l)

# 5. Nombre total de lignes dans ces fichiers
NUM_LINES=$(echo "$FILES" | xargs cat | wc -l)

# 6. Nombre de lignes contenant "marin"
NUM_MARIN_LINES=$(echo "$FILES" | xargs grep -H "marin" | wc -l)

# 7. Affichage des résultats
echo "Nombre de fichiers : $NUM_FILES"
echo "Nombre total de lignes : $NUM_LINES"
echo "Nombre de lignes contenant 'marin' : $NUM_MARIN_LINES"
