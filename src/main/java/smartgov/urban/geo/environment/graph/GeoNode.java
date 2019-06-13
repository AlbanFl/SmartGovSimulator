package smartgov.urban.geo.environment.graph;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import smartgov.core.environment.graph.Node;

/**
 * Generic class to represent a Node on a map.
 * 
 * @see Node
 * @author pbreugnot
 *
 * @param <Tarc> GeoArc type
 */
public class GeoNode extends Node {
	
	// Ignore the Coordinate field : uses the getPositionAsArray getter instead.
	@JsonIgnore
	private Coordinate position;
	
	public GeoNode(String id, Coordinate position){
		super(id);
		this.position = position;
	}
	
	public GeoNode(String id, Coordinate position, List<GeoArc> outgoingArcs, List<GeoArc> incomingArcs) {
		this(id, position);
		this.outgoingArcs.addAll(outgoingArcs);
		this.incomingArcs.addAll(incomingArcs);
	}
	
	public GeoNode(String id, double latitude, double longitude, List<GeoArc> outgoingArcs, List<GeoArc> incomingArcs){
		this(id,  new Coordinate(latitude, longitude), outgoingArcs, incomingArcs);
	}
	
	public Coordinate getPosition() {
		return position;
	}
	
	@JsonProperty("position")
	public double[] getPositionAsArray(){
		double[] coords = new double[2];
		coords[0] = position.x;
		coords[1] = position.y;
		return coords;
	}
}
