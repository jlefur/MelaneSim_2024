#!/bin/bash

# Nom du fichier de sortie
output_file="commit_comments.txt"

# Initialiser le fichier
echo "Liste des commentaires des commits (toutes branches)" > "$output_file"
echo "====================================================" >> "$output_file"

# Récupérer toutes les branches
branches=$(git branch -r | grep -v '\->')

# Parcourir chaque branche
for branch in $branches; do
    echo "Branche: $branch" >> "$output_file"
    echo "----------------------------------------" >> "$output_file"

    # Récupérer les messages des commits pour la branche
    git log --pretty=format:"%h %ad %an - %s" --date=short "$branch" | while read -r line; do
        echo "$line" >> "$output_file"
    done

    echo "" >> "$output_file"
done

# Afficher le résultat
echo "Les commentaires des commits ont été enregistrés dans '$output_file'."
