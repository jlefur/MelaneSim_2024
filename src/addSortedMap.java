
import java.util.*;
public class addSortedMap {

	public static void main(String[] args) {
		// Création d'un TreeMap trié par clé
		TreeMap<Double, Double> sortedMap = new TreeMap<>();
		sortedMap.put(1.2, 10.0);
		sortedMap.put(2.1, 15.0);
		sortedMap.put(3.5, 20.0);
		sortedMap.put(5.8, 25.0);
		sortedMap.put(7.3, 30.0);

		// Définir la somme cible
		double targetSum = 50.0;

		// Algorithme pour additionner les valeurs en partant de la clé la plus grande
		List<Double> selectedKeys = new ArrayList<>();
		double currentSum = 0.0;

		for (Map.Entry<Double, Double> entry : sortedMap.descendingMap().entrySet()) {
			if (currentSum >= targetSum) break; // Arrêter si la somme cible est atteinte
			selectedKeys.add(entry.getKey());
			currentSum += entry.getValue();
		}

		// Affichage des résultats
		System.out.println("Somme atteinte : " + currentSum);
		System.out.println("Clés sélectionnées : " + selectedKeys);

		// Vérification si l'objectif est atteint
		if (currentSum < targetSum) {
			System.out.println("Attention : Impossible d'atteindre la somme cible !");
		}
	}
}
