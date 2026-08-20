package data.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.rodents.I_ConstantMusTransport;
import data.constants.rodents.I_ConstantStringRodents;
import melanesim.protocol.A_Protocol;

/** Utility to convert from geographic coordinates to meters and compute distances in meter from the raster origin? <br>
 * Calculation depends on the raster used
 * @author author Moussa Sall, oct.2015 <br>
 *         source:
 *         http://geodesie.ign.fr/contenu/fichiers/documentation/pedagogiques/TransformationsCoordonneesGeodesiques.pdf */
public class C_ConvertGeographicCoordinates implements I_ConstantStringRodents {
	//
	// CONSTANTS
	//
	// JLF 2016.08 used WGS84 system; see source p.4
	private static final double earthRadiusAtEquator_Umeter = 6378249.2;// 6378249.2 half of great diameter
	private static final double earthRadiusAtGreenwich_Umeter = 6356515.;// 6356515.; // half of little diameter
	//
	// FIELDS
	//
	public Coordinate rasterOrigin_Umeter;
	public Coordinate rasterOrigin_Uradian;
	public Coordinate rasterOrigin_Udegree;
	public Coordinate rasterEnd_Udegree;
	private double ellipsoidEccentricitySquared_Umeter2;
	//
	// CONSTRUCTOR
	//
	public C_ConvertGeographicCoordinates(Coordinate rasterOrigin_Udegree) {
		this.rasterOrigin_Udegree = rasterOrigin_Udegree;
		this.rasterOrigin_Uradian = new Coordinate(convertDegree_Uradian(rasterOrigin_Udegree.x),convertDegree_Uradian(
		        rasterOrigin_Udegree.y));
		this.ellipsoidEccentricitySquared_Umeter2 = (Math.pow(earthRadiusAtEquator_Umeter,2.0)-Math.pow(
		        earthRadiusAtGreenwich_Umeter,2.0))/Math.pow(earthRadiusAtEquator_Umeter,2.0);
		// Compute radius of the normal curvature
		this.rasterOrigin_Umeter = convertCoordinate_Umeter(this.rasterOrigin_Uradian);
	}
	/** Initialize the constructor with origin Coordinate and end Coordinate in degree<br>
	 * author M.Sall */
	private C_ConvertGeographicCoordinates(Coordinate rasterOrigin_Udegree,Coordinate endRaster_Udegree) {
		this(rasterOrigin_Udegree);
		this.rasterEnd_Udegree = endRaster_Udegree;
	}
	//
	// METHODS
	//
	/** computeRadiusOfNormalCurvature */
	private Coordinate convertCoordinate_Umeter(Coordinate coordinate_Uradian) {
		double tmp1 = Math.sqrt(1.0-this.ellipsoidEccentricitySquared_Umeter2*((1.0-Math.cos(2.0*coordinate_Uradian.y))
		        /2.0));
		double radius_Umeter = earthRadiusAtEquator_Umeter/tmp1; // raster origin radius
		return new Coordinate(//
		        radius_Umeter*Math.cos(coordinate_Uradian.y)*Math.cos(coordinate_Uradian.x), //
		        radius_Umeter*Math.cos(coordinate_Uradian.y)*Math.sin(coordinate_Uradian.x), //
		        radius_Umeter*(1.0-this.ellipsoidEccentricitySquared_Umeter2)*Math.sin(coordinate_Uradian.y));
	}
	/** Compute distance to origin of a geographic position in meters */
	private double distanceToRasterOrigin_Umeter(double longitude_Udegree, double latitude_Udegree) {
		Coordinate coordinate_Uradian = new Coordinate(convertDegree_Uradian(longitude_Udegree),convertDegree_Uradian(
		        latitude_Udegree));
		Coordinate coordinate_Umeter = convertCoordinate_Umeter(coordinate_Uradian);
		// compute distance using Pythagorean theorem
		return Math.sqrt(Math.pow((this.rasterOrigin_Umeter.x-coordinate_Umeter.x),2.0)+Math.pow(
		        (this.rasterOrigin_Umeter.y-coordinate_Umeter.y),2.0)+Math.pow((this.rasterOrigin_Umeter.z
		                -coordinate_Umeter.z),2.0));
	}
	private double convertDegree_Uradian(double value_Udegree) { return value_Udegree*Math.PI/180.0; }
	private double convertRadian_Udegree(double value_Uradian) { return value_Uradian*180.0/Math.PI; }

	/** UNUSED : return latitude and longitude geographic coordinates had with the cell conversion by the rule of
	 * three */
	private Coordinate convertCell_Udegree(Coordinate oneCell) {
		Coordinate coordinateCell_Udegree = new Coordinate();
		double worldRadius = earthRadiusAtEquator_Umeter/Math.sqrt(1.0-this.ellipsoidEccentricitySquared_Umeter2*((1.0
		        -Math.cos(2.0*this.rasterOrigin_Uradian.y))/2.0));
		// C is an intermediate variable used to simplify the expression
		double cosinus_C = Math.sqrt(1.0-((Math.sqrt(oneCell.x*oneCell.x+oneCell.y*oneCell.y))/worldRadius)*(Math.sqrt(
		        oneCell.x*oneCell.x+oneCell.y*oneCell.y))/worldRadius);
		double sinus_C = (Math.sqrt(oneCell.x*oneCell.x+oneCell.y*oneCell.y))/worldRadius;
		coordinateCell_Udegree.x = this.rasterOrigin_Uradian.x+Math.atan((oneCell.x*sinus_C)/((Math.sqrt(oneCell.x
		        *oneCell.x+oneCell.y*oneCell.y)*Math.cos(this.rasterOrigin_Uradian.y)*cosinus_C)-((oneCell.y*Math.sin(
		                this.rasterOrigin_Uradian.y)))*sinus_C));
		coordinateCell_Udegree.y = Math.asin((cosinus_C*Math.sin(this.rasterOrigin_Uradian.y))+(oneCell.y*Math.cos(
		        this.rasterOrigin_Uradian.y)*sinus_C)/(Math.sqrt(oneCell.x*oneCell.x+oneCell.y*oneCell.y)));
		coordinateCell_Udegree.x = this.convertRadian_Udegree(coordinateCell_Udegree.x);
		coordinateCell_Udegree.y = this.convertRadian_Udegree(coordinateCell_Udegree.y);
		return coordinateCell_Udegree;
	}
	/** Return the line and column of the corresponding cell */
	public Coordinate convertCoordinate_Ucs(double longitude_Udegree, double latitude_Udegree) {
		if(C_Parameters.UCS_WIDTH_Umeter==0.0)
		    A_Protocol.event("C_ConvertGeographicCoordinates.convertCoordinate_Ucs","Ucs worth 0 meter",isError);
		// Compute distance between the projection of Y axis and the origin point
		double coordinatey_Umeter = this.distanceToRasterOrigin_Umeter(this.rasterOrigin_Udegree.x,latitude_Udegree);
		double y = coordinatey_Umeter/C_Parameters.UCS_WIDTH_Umeter;
		// Compute distance between the projection of X axis and the origin point
		double coordinatex_Umeter = this.distanceToRasterOrigin_Umeter(longitude_Udegree,this.rasterOrigin_Udegree.y);
		double x = coordinatex_Umeter/C_Parameters.UCS_WIDTH_Umeter;
		return new Coordinate(x,y);
	}

	private Coordinate convertCellRuleOfThree_Udegree(Coordinate cell_Umeter,
	        ArrayList<Double> rasterLongitudeWest_LatitudeSouth_Udegree, Coordinate width_HeightOrigin_Umeter) {
		// compute values in degree of width and height
		Coordinate correspondingCoordWidthAndHeight_Udegree = new Coordinate((this.rasterEnd_Udegree.x
		        -this.rasterOrigin_Udegree.x),(this.rasterEnd_Udegree.y-this.rasterOrigin_Udegree.y));
		Coordinate coordinate_Udegree = new Coordinate();
		// compute values of the cell in degree
		coordinate_Udegree.x = rasterLongitudeWest_LatitudeSouth_Udegree.get(0)
		        +(correspondingCoordWidthAndHeight_Udegree.x*cell_Umeter.x/width_HeightOrigin_Umeter.x);
		coordinate_Udegree.y = rasterLongitudeWest_LatitudeSouth_Udegree.get(1)
		        +(correspondingCoordWidthAndHeight_Udegree.y*cell_Umeter.y/width_HeightOrigin_Umeter.y);
		return coordinate_Udegree;
	}
	// Main for Mus transportation
	public static void main(String[] args) {
		Coordinate musTransportOrigin = new Coordinate(I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree
		        .get(0),I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree.get(1));
		Coordinate musTransportEnd = new Coordinate(-11.32016,16.71801);
		C_ConvertGeographicCoordinates distance = new C_ConvertGeographicCoordinates(musTransportOrigin,
		        musTransportEnd);
		Map<String,Coordinate> cities_Ucell = new HashMap<String,Coordinate>() {
			private static final long serialVersionUID = 1L;
			{
				put("Touba-Mbacke",new Coordinate(91,30+110));// 110
				put("Thies",new Coordinate(34,30+109));
				put("Saint-Louis",new Coordinate(57,100+110));
				put("Richard-Toll",new Coordinate(103,125+110));
				put("Podor",new Coordinate(144,136+110));
				put("Pikine",new Coordinate(7,28+110));
				put("Mbour",new Coordinate(31,9+110));
				put("Matam",new Coordinate(240,79+110));
				put("Potou I",new Coordinate(56,83+110));
				put("Kebemer",new Coordinate(61,63+110));
				put("Diohine",new Coordinate(57,14+110));
			}
		};
		Object[] keys = cities_Ucell.keySet().toArray();
		for(int i = 0; i<cities_Ucell.size(); i++){
			double cellSize_Umeter = 1965;
			String name = (String)keys[i];
			Coordinate coordinate = new Coordinate(cities_Ucell.get(name).x*cellSize_Umeter,cities_Ucell.get(name).y
			        *cellSize_Umeter);
			// Coordinate result = distance.convertCell_Udegree(coordinate);
			Coordinate result = distance.convertCellRuleOfThree_Udegree(coordinate,
			        I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree,new Coordinate(351.*1950.,251.
			                *1950.));
			System.out.println(result.y+","+result.x+","+cities_Ucell.keySet().toArray()[i]);
		}
	}
}