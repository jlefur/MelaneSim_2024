package presentation.display;

import java.awt.Color;
import java.awt.Font;

import data.C_Parameters;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantPNMC;
import data.constants.rodents.I_ConstantGerbil;
import data.constants.rodents.I_ConstantImagesNames;
import data.constants.rodents.I_ConstantStringRodents;
import melanesim.C_ContextCreator;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.StyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.Position;
import saf.v3d.scene.VSpatial;
import thing.A_Amniote;
import thing.A_Animal;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Plankton;
import thing.C_Ship_cargo;
import thing.C_StreamCurrent;
import thing.I_SituatedThing;
import thing.ground.A_SupportedContainer;
import thing.ground.C_SoilCellMarine;
import thing.rodents.A_HumanUrban;
import thing.rodents.C_Vegetation;

/** Style des agents "animaux". Définit une icône ou une ellipse pour chaque agent au lancement de la simulation en fonction de
 * son sexe et la fait varier suivant son âge.
 * @author A Realini 2011 */
public class C_StyleAgent implements StyleOGL2D<I_SituatedThing>, I_ConstantStringRodents, I_ConstantPNMC,
		I_ConstantNumeric, I_ConstantImagesNames {
	private float imageScale, ellipseScale;
	private final float CIRCLE_RADIUS = 5; // Rayon de l'ellipse
	private final int CIRCLE_SLICES = 10; // Nombres d'arêtes de l'ellipse (joue sur le rendu : sera plus ou moins rond)
	private C_IconSelector selectImg;
	private ShapeFactory2D factory;

	/** Initialise un gestionnaire d'images et enregistre les images qui seront utilis�es au cours de la simulation dans le
	 * factory */
	public void init(ShapeFactory2D factory) {
		this.factory = factory;
		selectImg = new C_IconSelector();
		factory.registerImage(TAGGED, selectImg.loadImage(TAGGED));
		if (C_Parameters.PROTOCOL.contains("PNMC")) initPNMC();
		else if (C_Parameters.PROTOCOL.equals(CHIZE)) initChize();
		else if (C_Parameters.PROTOCOL.equals(ENCLOSURE)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.equals(DODEL)) initDodel();
		else if (C_Parameters.PROTOCOL.equals(CAGES)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.equals(HYBRID_UNIFORM)) initEnclosMbour();
		else if (C_Parameters.PROTOCOL.contains(CENTENAL)) initCentenal();
		else if (C_Parameters.PROTOCOL.equals(DECENAL)) initDecenal();
		else if (C_Parameters.PROTOCOL.equals(MUS_TRANSPORT)) initMusTransport();
		else if (C_Parameters.PROTOCOL.equals(GERBIL)) initGerbil();
		else if (C_Parameters.PROTOCOL.equals(BANDIA)) initBandia();
		else if (C_Parameters.PROTOCOL.equals(DODEL2)) initDodel2();
		C_ContextCreator.protocol.setStyleAgent(this);
	}

	public void initPNMC() {
		this.ellipseScale = 2.f;
		this.imageScale = .07f;
		factory.registerImage(PLANKTON_ICON, selectImg.loadImage(PLANKTON_ICON));
		factory.registerImage(SHIP_CARGO_ICON, selectImg.loadImage(SHIP_CARGO_ICON));
		factory.registerImage(NORTH_ICON, selectImg.loadImage(NORTH_ICON));
		factory.registerImage(NORTH_EAST_ICON, selectImg.loadImage(NORTH_EAST_ICON));
		factory.registerImage(EAST_ICON, selectImg.loadImage(EAST_ICON));
		factory.registerImage(SOUTH_EAST_ICON, selectImg.loadImage(SOUTH_EAST_ICON));
		factory.registerImage(SOUTH_ICON, selectImg.loadImage(SOUTH_ICON));
		factory.registerImage(SOUTH_WEST_ICON, selectImg.loadImage(SOUTH_WEST_ICON));
		factory.registerImage(WEST_ICON, selectImg.loadImage(WEST_ICON));
		factory.registerImage(NORTH_WEST_ICON, selectImg.loadImage(NORTH_WEST_ICON));
	}

	public void initChize() {
		this.ellipseScale = .6f;
		this.imageScale = .2f;
		factory.registerImage(VOLE_FEMALE_CHILD, selectImg.loadImage(VOLE_FEMALE_CHILD));
		factory.registerImage(VOLE_FEMALE_ADULT, selectImg.loadImage(VOLE_FEMALE_ADULT));
		factory.registerImage(VOLE_MALE_CHILD, selectImg.loadImage(VOLE_MALE_CHILD));
		factory.registerImage(VOLE_MALE_ADULT, selectImg.loadImage(VOLE_MALE_ADULT));
		factory.registerImage(VOLE_PREGNANT, selectImg.loadImage(VOLE_PREGNANT));
	}

	public void initDodel() {
		this.ellipseScale = 1.4f;
		this.imageScale = .3f;
		factory.registerImage(MOUSE_FEMALE_CHILD, selectImg.loadImage(MOUSE_FEMALE_CHILD));
		factory.registerImage(MOUSE_FEMALE_ADULT, selectImg.loadImage(MOUSE_FEMALE_ADULT));
		factory.registerImage(MOUSE_MALE_CHILD, selectImg.loadImage(MOUSE_MALE_CHILD));
		factory.registerImage(MOUSE_MALE_ADULT, selectImg.loadImage(MOUSE_MALE_ADULT));
		factory.registerImage(MOUSE_PREGNANT, selectImg.loadImage(MOUSE_PREGNANT));
		factory.registerImage(MOUSE_DISPERSE, selectImg.loadImage(MOUSE_DISPERSE));
		factory.registerImage(VEHICLE_TAXI_DODEL, selectImg.loadImage(VEHICLE_TAXI_DODEL));
		factory.registerImage(DAY, selectImg.loadImage(DAY));
		factory.registerImage(NIGHT, selectImg.loadImage(NIGHT));
		factory.registerImage(DAWN, selectImg.loadImage(DAWN));
		factory.registerImage(TWILIGHT, selectImg.loadImage(TWILIGHT));
	}

	public void initDodel2() {
		initDodel();
		this.ellipseScale = 2.f;
		this.imageScale = .8f;
		factory.registerImage(BURROW, selectImg.loadImage(BURROW));
		factory.registerImage(MAN, selectImg.loadImage(MAN));
		factory.registerImage(WOMAN, selectImg.loadImage(WOMAN));
		factory.registerImage(BOY, selectImg.loadImage(BOY));
		factory.registerImage(GIRL, selectImg.loadImage(GIRL));
		factory.registerImage(CHILD, selectImg.loadImage(CHILD));
		factory.registerImage(ORNITHODOROS_ADULT, selectImg.loadImage(ORNITHODOROS_ADULT));
		factory.registerImage(ORNITHODOROS_NYMPH, selectImg.loadImage(ORNITHODOROS_NYMPH));
		factory.registerImage(ORNITHODOROS_LARVAE, selectImg.loadImage(ORNITHODOROS_LARVAE));
		factory.registerImage(ORNITHODOROS_EGG, selectImg.loadImage(ORNITHODOROS_EGG));
		factory.registerImage(INFECTED_MOUSE_MALE_ADULT, selectImg.loadImage(INFECTED_MOUSE_MALE_ADULT));
		factory.registerImage(INFECTED_MOUSE_FEMALE_ADULT, selectImg.loadImage(INFECTED_MOUSE_FEMALE_ADULT));
		factory.registerImage(INFECTED_YOUNG_MOUSE_MALE, selectImg.loadImage(INFECTED_YOUNG_MOUSE_MALE));
		factory.registerImage(INFECTED_YOUNG_MOUSE_FEMALE, selectImg.loadImage(INFECTED_YOUNG_MOUSE_FEMALE));
		factory.registerImage(ORNITHODOROS_INFECTED, selectImg.loadImage(ORNITHODOROS_INFECTED));
		factory.registerImage(DODEL2_FOOD, selectImg.loadImage(DODEL2_FOOD));
		factory.registerImage(MOUSE_HIDE, selectImg.loadImage(MOUSE_HIDE));
		factory.registerImage(CAT_ADULT_MALE, selectImg.loadImage(CAT_ADULT_MALE));
		factory.registerImage(CAT_ADULT_FEMALE, selectImg.loadImage(CAT_ADULT_FEMALE));
		factory.registerImage(CAT_JUVENILE_MALE, selectImg.loadImage(CAT_JUVENILE_MALE));
		factory.registerImage(CAT_JUVENILE_FEMALE, selectImg.loadImage(CAT_JUVENILE_FEMALE));
		factory.registerImage(CAT_YOUNG_MALE, selectImg.loadImage(CAT_YOUNG_MALE));
		factory.registerImage(CAT_YOUNG_FEMALE, selectImg.loadImage(CAT_YOUNG_FEMALE));
	}

	public void initGerbil() {
		this.ellipseScale = 1.f;
		// Change image scale depending on the map resolution JLF 03.2018
		float scaleForIcon = .2f;
		switch (((String) RunEnvironment.getInstance().getParameters().getValue("RASTER_FILE")).toLowerCase()) {
			case "zoom4" :
			case "zoom3" :
				scaleForIcon = .25f;
				break;
			case "me" :
				scaleForIcon = 16.f;
				break;
			case "pe" :
				scaleForIcon = 6.f;
				this.ellipseScale = 8.f;
				break;
			case "zoom1" :// lac de Guiers
				this.ellipseScale = 15.f;
				scaleForIcon = 1.5f;
				break;
			case "zoom2" :
				this.ellipseScale = 8.f;
				scaleForIcon = 3.f;
				break;
		}
		this.imageScale = scaleForIcon;
		factory.registerImage(GERBIL_MALE, selectImg.loadImage(GERBIL_MALE));
		factory.registerImage(GERBIL_FEMALE, selectImg.loadImage(GERBIL_FEMALE));
		factory.registerImage(GERBIL_PREGNANT, selectImg.loadImage(GERBIL_PREGNANT));
		factory.registerImage(GERBIL_IMMATURE, selectImg.loadImage(GERBIL_IMMATURE));
		factory.registerImage(GERBIL_DISPERSE, selectImg.loadImage(GERBIL_DISPERSE));
		factory.registerImage(GERBIL_HIDE, selectImg.loadImage(GERBIL_HIDE));
		factory.registerImage(GERBIL_UNDERGROUND, selectImg.loadImage(GERBIL_UNDERGROUND));
		factory.registerImage(BARNOWL_ICON, selectImg.loadImage(BARNOWL_ICON));
		factory.registerImage(SHRUBS_ICON, selectImg.loadImage(SHRUBS_ICON));
		factory.registerImage(TREES_ICON, selectImg.loadImage(TREES_ICON));
		factory.registerImage(GRASSES_ICON, selectImg.loadImage(GRASSES_ICON));
		factory.registerImage(BARREN_ICON, selectImg.loadImage(BARREN_ICON));
		factory.registerImage(CROPS_ICON, selectImg.loadImage(CROPS_ICON));
		factory.registerImage(DAY, selectImg.loadImage(DAY));
		factory.registerImage(NIGHT, selectImg.loadImage(NIGHT));
		factory.registerImage(DAWN, selectImg.loadImage(DAWN));
		factory.registerImage(TWILIGHT, selectImg.loadImage(TWILIGHT));
	}

	public void initCentenal() {
		this.ellipseScale = 2.4f;
		this.imageScale = 0.2f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_CAR, selectImg.loadImage(VEHICLE_CAR));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(RATTUS_PREGNANT, selectImg.loadImage(RATTUS_PREGNANT));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
	}

	public void initMusTransport() {
		this.ellipseScale = 3.5f;
		this.imageScale = 1.2f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_TAXI, selectImg.loadImage(VEHICLE_TAXI));
		factory.registerImage(MUS_IMAGE, selectImg.loadImage(MUS_IMAGE));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(RATTUS_PREGNANT, selectImg.loadImage(RATTUS_PREGNANT));
		factory.registerImage(MUS_PREGNANT, selectImg.loadImage(MUS_PREGNANT));
		factory.registerImage(MASTO_ERY_IMAGE, selectImg.loadImage(MASTO_ERY_IMAGE));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
		factory.registerImage(MUSTDIE, selectImg.loadImage(MUSTDIE));
	}

	public void initDecenal() {
		this.ellipseScale = 2.6f;
		this.imageScale = 0.4f;
		factory.registerImage(VEHICLE_BOAT, selectImg.loadImage(VEHICLE_BOAT));
		factory.registerImage(VEHICLE_TRAIN, selectImg.loadImage(VEHICLE_TRAIN));
		factory.registerImage(VEHICLE_TRUCK, selectImg.loadImage(VEHICLE_TRUCK));
		factory.registerImage(VEHICLE_TAXI, selectImg.loadImage(VEHICLE_TAXI));
		factory.registerImage(RATTUS_MATURE, selectImg.loadImage(RATTUS_MATURE));
		factory.registerImage(NEWBORN, selectImg.loadImage(NEWBORN));
		factory.registerImage(VEHICLE_LOADED, selectImg.loadImage(VEHICLE_LOADED));
		factory.registerImage(VEHICLE_PARKED, selectImg.loadImage(VEHICLE_PARKED));
	}

	public void initEnclosMbour() {
		this.imageScale = .15f;
		this.ellipseScale = 1.5f;
		factory.registerImage(MASTO_ERYTHROLEUCUS, selectImg.loadImage(MASTO_ERYTHROLEUCUS));
		factory.registerImage(MASTO_NATALENSIS, selectImg.loadImage(MASTO_NATALENSIS));
		factory.registerImage(MASTO_LAZARUS, selectImg.loadImage(MASTO_LAZARUS));
		factory.registerImage(MASTO_HYBRID, selectImg.loadImage(MASTO_HYBRID));
		factory.registerImage(MASTO_PREGNANT, selectImg.loadImage(MASTO_PREGNANT));
		factory.registerImage(UNKNOWN, selectImg.loadImage(UNKNOWN));
	}

	public void initBandia() {
		this.ellipseScale = 2.f;
		this.imageScale = .6f;
		factory.registerImage(MASTO_MALE, selectImg.loadImage(MASTO_MALE));
		factory.registerImage(MASTO_FEMALE, selectImg.loadImage(MASTO_FEMALE));
		factory.registerImage(MASTO_PREGNANT, selectImg.loadImage(MASTO_PREGNANT));
		factory.registerImage(MASTO_JUVENILE, selectImg.loadImage(MASTO_JUVENILE));
		factory.registerImage(MASTO_DISPERSE, selectImg.loadImage(MASTO_DISPERSE));
		factory.registerImage(MASTO_UNDERGROUND, selectImg.loadImage(MASTO_UNDERGROUND));
		factory.registerImage(LOADED_TRAP, selectImg.loadImage(LOADED_TRAP));
		factory.registerImage(EMPTY_TRAP, selectImg.loadImage(EMPTY_TRAP));
	}

	/** Attribue une nouvelle "image" à un agent ou la modifie si besoin est, sinon renvoie le spatial en paramètre sans le
	 * modifier.
	 * @param agent : l'agent à qui appartient l'icône
	 * @param spatial : représentation de l'agent (image ou forme géométrique) */
	@Override
	public VSpatial getVSpatial(I_SituatedThing agent, VSpatial spatial) {

		// 1. BACKGROUND specific case
		if (agent instanceof C_Background) {
			if (((C_Background) agent).hasToSwitchFace || spatial == null)
				spatial = factory.getNamedSpatial(selectImg.getNameOfImage(agent));
		}

		// 2. change spatial only is requested
		else if (((A_VisibleAgent) agent).hasToSwitchFace || spatial == null) {

			// 3. TAGGED agent specific case
			if (agent instanceof A_SupportedContainer && ((A_SupportedContainer) agent).isa_Tag()) {
				spatial = factory.getNamedSpatial(selectImg.getNameOfImage(agent));
			}

			// 4. fetch the icon if IMAGE is on
			else if (C_Parameters.IMAGE) {
				spatial = factory.getNamedSpatial(selectImg.getNameOfImage(agent));
			}

			// 5. Various case when IMAGE is off (design the shape)
			else {
				if (agent instanceof C_Vegetation) {
					spatial = factory.createCircle(CIRCLE_RADIUS * 0.5f, 3);// triangle shape
				} // mature=circle, immature=square
				if (agent instanceof C_Ship_cargo) return factory.createCircle(CIRCLE_RADIUS * 2.5f, 3);
				else
					if (agent instanceof A_Amniote && !((A_Amniote) agent).isSexualMature())
						spatial = factory.createRectangle((int) CIRCLE_RADIUS, (int) CIRCLE_RADIUS);
				else
						spatial = factory.createCircle(CIRCLE_RADIUS, CIRCLE_SLICES);

				if (agent instanceof A_Animal && ((A_Animal) agent).isHasToLeaveFullContainer()) {
					if (agent instanceof A_Amniote && !((A_Amniote) agent).isSexualMature())
						spatial = factory.createCircle(CIRCLE_RADIUS * 1.5f, 3);
					else
						spatial = factory.createCircle(CIRCLE_RADIUS * 2.f, 3);// triangle shape
				}
				if (agent instanceof A_Animal && ((A_Animal) agent).hasEnteredDomain) {
					spatial = factory.createCircle(CIRCLE_RADIUS * 2.f, 4);// diamond shape
					// TODO number in source 2018.05 JLF for displaying entering rodents
					if (Math.random() > .98) ((A_Animal) agent).hasEnteredDomain = false;
				}
			}
			((A_VisibleAgent) agent).hasToSwitchFace = false;
		}
		return spatial;
	}

	@Override
	public Color getColor(I_SituatedThing agent) {
		if (C_Parameters.IMAGE) return Color.white; // the color is not important
		else return C_IconSelector.getColor(agent);
	}

	@Override
	/** TODO number in source for image scales MS 2016, JLF 2021, 2024 */
	public float getScale(I_SituatedThing object) {

		// 1.- EITHER icons OR ellipses are displayed
		float sscale = this.imageScale;
		if (object instanceof C_Vegetation) {
			double energy = I_ConstantGerbil.INITIAL_VEGET_ENERGY + ((C_Vegetation) object).getEnergy_Ukcal() / 2;
			if (C_Parameters.IMAGE) sscale = (float) (energy * .005);
			else sscale = (float) (energy * .1);
		}

		// 2.- ICONS are displayed
		if (C_Parameters.IMAGE) {
			if (object instanceof C_StreamCurrent) {
				sscale = (float) (((C_StreamCurrent) object).getSpeedEast() * ((C_StreamCurrent) object).getSpeedEast()
						* sscale * STREAM_DISPLAY_SIZE * 1.5);
			}
			// plankton image reflects the number of plankton agent within their cell
			if (object instanceof C_Plankton) {
				if (((A_SupportedContainer) object).isa_Tag()) sscale = sscale * 17;
				else {
					C_SoilCellMarine cell = (C_SoilCellMarine) object.getCurrentSoilCell();
					sscale = (float) Math.max(Math.pow(cell.getPlanktonTotalChlorophyll(), 2.5)/450000,.1);
					// float size = (float) (this.imageScale * 10.);
				}
			}
			// plankton image reflects the number of plankton agent within their cell
			if (object instanceof C_Ship_cargo) {
				if (((A_SupportedContainer) object).isa_Tag()) sscale = sscale * 17;
				else sscale = sscale * 7;
			}
			if (object instanceof A_HumanUrban) {
				if (((A_HumanUrban) object).isa_Tag()) sscale = sscale * 5;
				if (!((A_Animal) object).getDesire().equals(REST)) sscale = sscale * 2;
				else sscale = sscale / 2;// taille humains
			}
		}

		// 3.- ELLIPSES are Displayed
		else if (object instanceof C_StreamCurrent) sscale = (float) 0.;// do not show current if no icons selected
		// // Show (badly) the relative importance of agents sensing
		// else if (object instanceof A_Animal) return (float) (this.ELLIPSE_SCALE *
		// ((A_Animal) object).getSensing_UmeterByTick()
		// / 10.);
		else sscale = this.ellipseScale;

		// 4.- double scale if agent is TAGGED
		// if (object instanceof A_SupportedContainer && ((A_SupportedContainer) object).isa_Tag()) sscale = sscale * 2;
		return sscale;
	}

	// OVERRIDEN & UNUSED METHODS //
	public float getRotation(I_SituatedThing object) {
		return 0;
	}

	public int getBorderSize(I_SituatedThing object) {
		return 0;
	}

	public Color getBorderColor(I_SituatedThing object) {
		return null;
	}

	public String getLabel(I_SituatedThing object) {
		if (C_Parameters.VERBOSE) {
			if (object instanceof A_Animal)
				return ((A_NDS) object).retrieveId() + "/" + ((A_Animal) object).getCell1Target();
			if (object instanceof A_NDS) return ((A_NDS) object).retrieveId();
			else return null;
		}
		else return " ";// Does not work if return null JLF 09.2017
	}

	public Font getLabelFont(I_SituatedThing object) {
		return new Font("lucida sans", Font.PLAIN, 12);// JLF 03.2018 font style does not seem to have effect
	}

	public float getLabelXOffset(I_SituatedThing object) {
		return 0;
	}

	public float getLabelYOffset(I_SituatedThing object) {
		return 0;
	}

	public ShapeFactory2D getFactory() {
		return factory;
	}

	public Position getLabelPosition(I_SituatedThing object) {
		return Position.SOUTH;
	}

	public Color getLabelColor(I_SituatedThing object) {
		// TODO number(/data) in source JLF 2019.02 : desires list
		String desires = "/FORAGE/FEED/REST/REPRODUCTION/SUCKLE/HIDE/NONE/WANDER/";
		if ((object instanceof A_Animal) && desires.contains(((A_Animal) object).getCell1Target())) return Color.RED;
		else return Color.BLUE;
	}
}
