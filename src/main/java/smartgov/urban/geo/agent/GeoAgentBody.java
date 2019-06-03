package smartgov.urban.geo.agent;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.math.Vector2D;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import smartgov.core.agent.AbstractAgentBody;
import smartgov.core.agent.Plan;
import smartgov.core.agent.mover.AbstractMover;
import smartgov.core.environment.graph.arc.Arc;
import smartgov.core.environment.graph.node.Node;
import smartgov.core.main.SimulationBuilder;
import smartgov.core.output.coordinate.CoordinateSerializer;
import smartgov.urban.geo.agent.event.GeoMoveEvent;
/**
 * A generic abstract child of an AbstractAgentBody, built to be represented on a map.
 * 
 * @author pbreugnot
 *
 * @param <Node> GeoNode type
 * @param <Arc> GeoArc type
 * @param <Tmover> Mover used to move agents in the structure.
 * @param <Tsensor> Sensor used to sense data in the environment.
 */
public abstract class GeoAgentBody<Tagent extends GeoAgent<?, ?, ?, ?>, Tmover extends AbstractMover> extends AbstractAgentBody<Tagent> {

	@JsonIgnore
	protected Vector2D direction;

	protected double speed; //In meters per second
	
	@JsonIgnore
	protected Coordinate destination;
	@JsonSerialize(using=CoordinateSerializer.class)
	protected Coordinate position;

	@JsonIgnore
	protected Tmover mover;

	public GeoAgentBody() {
		super();
		speed = 0.0;
		direction = new Vector2D();
		plan = new Plan();
	}

	public Coordinate getPosition() {
		return position;
	}

	public void setPosition(Coordinate position) {
		this.position = position;
	}

	public void setSpeed(double speed) {
		if(Double.isNaN(speed)){
			this.speed = 0.0;
		} else {
			this.speed = speed;
		}
	}

	public double getSpeed() {
		return speed;
	}

	public void setDestination(Coordinate destination) {
		this.destination = destination;
	}

	public Vector2D getDirection() {
		return direction;
	}

	public void setDirection(Vector2D direction) {
		this.direction = direction;
	}
	
	public Tmover getMover() {
		return mover;
	}

	@Override
	public GeoMoveEvent move() {
		Coordinate oldCoordinate = getPosition();
		Arc oldArc = plan.getCurrentArc();
		Node oldNode = plan.getCurrentNode();
		
		// Distance to cross in one tick
		double distance = getSpeed() * SimulationBuilder.TICK_DURATION;
		setPosition(this.mover.moveOn(distance));
		
		Coordinate newCoordinate = getPosition();
		Arc newArc = plan.getCurrentArc();
		Node newNode = plan.getCurrentNode();
		
		return new GeoMoveEvent(
				oldCoordinate,
				newCoordinate,
				oldArc,
				newArc,
				oldNode,
				newNode,
				distance
				);
	}

	@Override
	public void enter() {

	}

	@Override
	public void leave() {

	}

	@Override
	public void moveTo() {
		// TODO: to do.
	}

	@Override
	public void idle() {
		// TODO Auto-generated method stub

	}
}
