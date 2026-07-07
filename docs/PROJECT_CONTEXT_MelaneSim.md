# PROJECT_CONTEXT — MelaneSim

## 1. Rôle de ce document
Ce document constitue le **contexte projet officiel** à fournir à ChatGPT **avant toute discussion** liée à MelaneSim
(codage, erreurs, refactorisation, suggestions, explications).

Objectif : permettre à l’IA de **raisonner comme si elle connaissait déjà le projet**, sans réexpliquer l’historique.

---

## 2. Objectif du projet
MelaneSim est une application de **simulation multi-agents**.

Finalités principales :
- modéliser des entités autonomes (acteurs)
- simuler leurs interactions dans un environnement
- étudier des dynamiques émergentes (énergie, déplacements, règles)

---

## 3. Cadre technique (contraintes structurantes)

### 3.1 Framework de simulation
- **Repast Simphony 2.11.0**
- utilisé comme moteur de simulation principal
- impose :
  - un cycle de vie précis des agents
  - un ordonnancement (scheduling) spécifique
  - une structuration particulière de l’environnement et du contexte

Toute proposition de code, d’architecture ou de règle doit être **compatible avec Repast Simphony**
et respecter son modèle d’exécution.

### 3.2 Langage et environnement de développement
- Langage : **Java**
- IDE principal : **Eclipse 2024-03 (4.31.0)**

Les explications, solutions et corrections doivent tenir compte :
- des usages Eclipse (projets, classpath, build)
- des erreurs et contraintes typiques rencontrées dans cet environnement

---

## 4. Principes d’architecture

### 4.1 Philosophie générale
- architecture orientée **clarté, extensibilité et cohérence**
- priorité à la **lisibilité du modèle** plutôt qu’à l’optimisation prématurée
- séparation stricte entre :
  - modèle
  - règles
  - moteur de simulation

### 4.2 Principes retenus
- pas de constantes globales magiques
- comportements encapsulés
- préférer la composition à l’héritage excessif
- éviter les effets de bord implicites

---

## 5. Concepts clés du domaine

### 5.1 Acteurs
- entités autonomes
- possèdent un état (énergie, position, etc.)
- agissent selon des règles explicites

### 5.2 Environnement
- espace de simulation
- peut être continu ou discrétisé
- fournit des contraintes et ressources

### 5.3 Énergie
- ressource centrale du modèle
- gouverne la survie, l’action et les décisions
- gérée explicitement par des règles

---

## 6. Décisions techniques importantes

### 6.1 Bonnes pratiques retenues
- méthodes courtes et explicites
- noms de méthodes descriptifs
- calculs séparés de l’application des effets

### 6.2 Choix assumés
- clarté > micro-optimisation
- règles explicites même si verbeuses
- logique métier jamais cachée

---

## 7. Conventions de code

- une méthode = une responsabilité
- pas de valeurs numériques non documentées
- commentaires utilisés pour expliquer **le pourquoi**, pas le quoi
- structures reproductibles pour les comportements similaires

---

## 8. Contraintes à respecter par ChatGPT

Quand tu produis du code ou des explications :
- respecte strictement l’architecture existante
- ne propose pas de refonte globale sans justification explicite
- reste cohérent avec les décisions passées
- signale toute hypothèse nouvelle ou tout changement conceptuel

---

## 9. Glossaire (court et normatif)

**Acteur**  
Entité autonome de la simulation possédant un état et des comportements régis par des règles explicites (exemple: plankton, nekton, ship, whale...).

**Règle**  
Logique déterministe définissant une action ou une réaction d’un acteur.

**Environnement**  
Espace de simulation dans lequel évoluent les acteurs, soumis à des contraintes (température, courant, profondeur, etc.)

**Énergie**  
Ressource centrale gouvernant les actions, décisions et la survie des acteurs. Indicateur permettant de comparer les acteurs. 

**Moteur de simulation**  
Composant responsable de l’ordonnancement temporel et de l’exécution des règles.

---

## 10. Règle d’or

Toute réponse doit être formulée **comme si ChatGPT faisait partie de l’équipe de développement de MelaneSim depuis le début**.
