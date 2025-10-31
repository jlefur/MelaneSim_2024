package thing;

import java.util.Random;
import data.converters.C_ConvertTimeAndSpace;
import melanesim.C_ContextCreator;
import thing.dna.I_DiploidGenome;

public class C_Ship_cargo extends A_Animal {
	//
	// FIELD
	//
	private String destination = new java.util.Random().nextBoolean() ? "NORTH" : "SOUTH"; // au choix à l'initialisation
	//
	// CONSTRUCTOR
	//
	public C_Ship_cargo(I_DiploidGenome genome) {
		super(genome);
		this.initParameters();
		this.setMyName("Cargo-" + this.myId);
		this.setNewTarget();
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public void step_Utick() {
		this.energy_Ukcal = 500.;// TODO number in source 2025 energie des cargos
		if (this.target == null) this.setNewTarget();
		else {
			computeNextMoveToTarget();
			if (this.isArrived(this.speed_UmeterByTick)) {
				this.setHasToSwitchFace(true);
				this.nextMove_Umeter.x = 0.0;
				this.nextMove_Umeter.y = 0.0;
				A_VisibleAgent.myLandscape.moveToLocation(this, this.target.getCoordinate_Ucs());
				this.setNewTarget();
			}
			else {
				this.computeNextMoveToTarget();
				// add a slight random to moves
				this.nextMove_Umeter.x += ((C_ContextCreator.randomGeneratorForMovement.nextDouble()
						* speed_UmeterByTick) - (C_ContextCreator.randomGeneratorForMovement.nextDouble()
								* speed_UmeterByTick)) / 10.;
				this.nextMove_Umeter.y += ((C_ContextCreator.randomGeneratorForMovement.nextDouble()
						* speed_UmeterByTick) - (C_ContextCreator.randomGeneratorForMovement.nextDouble()
								* speed_UmeterByTick)) / 10.;
				this.actionMove();
			}
		}
	}
	/** Initialize speed and sensing using time and space conversion<br>
	 * override retrieve speed from genome @see NB: can be used standalone if users parameters are changed during simulation<br>
	 * sensing conversion is bypassed<br>
	 * @author lefur 2025.10 */
	@Override
	public void initParameters() {
		double speed_UmeterByDay = 1852. * 24. * 10.;// speed = 10 milles/heure (10 noeuds) TODO number in source 2025 speed = 10
														// noeuds
		this.speed_UmeterByTick = speed_UmeterByDay / C_ConvertTimeAndSpace.oneDay_Utick;
	}
	//
	// SPECIFIC METHOD
	//
	// NORTH: 53,267 -> 96, 267
	// SOUTH: 225, 0 -> 272, 0
	// NOUMEA: 231, 94 -> 272, 0
	public void setNewTarget() {
		int min, max, column;
		Random rand = new Random();
		if (this.destination.equals("NORTH")) {
			this.destination = "SOUTH";
			min = 225;
			max = 272;// TODO number in source 2025.10 jlf
			column = rand.nextInt(max - min + 1) + min;
			this.setTarget(myLandscape.getGrid()[column][1]);// TODO number in source 2025.10 jlf
		}
		else {
			this.destination = "NORTH";
			min = 53;
			max = 96;// TODO number in source 2018.03 jlf
			column = rand.nextInt(max - min + 1) + min;
			this.setTarget(myLandscape.getGrid()[column][266]);// TODO number in source 2025.10 jlf
		}
		if (rand.nextDouble() < .1) {
			this.destination = "NOUMEA";
			this.setTarget(myLandscape.getGrid()[231][94]);
		}

	}
	//
	// GETTER
	//
	public String getDestination() {
		return destination;
	}
}