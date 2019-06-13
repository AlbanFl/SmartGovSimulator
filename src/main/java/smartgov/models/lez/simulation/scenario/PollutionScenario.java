package smartgov.models.lez.simulation.scenario;

import java.util.LinkedList;
import java.util.Queue;

import smartgov.models.lez.agent.DeliveryDriver;
import smartgov.models.lez.agent.vehicle.DeliveryVehicle;
import smartgov.models.lez.agent.vehicle.DeliveryVehicleFactory;
import smartgov.models.lez.copert.inputParser.CopertInputReader;
import smartgov.models.lez.copert.inputParser.CopertProfile;
import smartgov.models.lez.copert.tableParser.CopertParser;
import smartgov.models.lez.environment.graph.PollutableOsmArc;
import smartgov.urban.osm.agent.OsmAgentBody;
import smartgov.urban.osm.environment.OsmContext;
import smartgov.urban.osm.environment.graph.OsmArc;
import smartgov.urban.osm.environment.graph.OsmNode;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.simulation.scenario.lowLayer.ScenarioLowAgents;

public class PollutionScenario extends ScenarioLowAgents {
	
	public static final String name = "Pollution";
	
	private Queue<DeliveryVehicle> vehiclesStock;

	public PollutionScenario(OsmContext environment) {
		super(environment);
		// Load the copert table
		CopertParser copertParser = new CopertParser(environment.getFiles().getFile("copert_table"));
		
		// Load input profiles
		CopertInputReader reader = new CopertInputReader();
		CopertProfile copertProfile = reader.parseInputFile(environment.getFiles().getFile("copert_profile"));
		
		// Create a vehicle factory
		DeliveryVehicleFactory vehicleFactory = new DeliveryVehicleFactory(copertProfile, copertParser);
		vehiclesStock = new LinkedList<>();
		
		// Feed the stock with delivery vehicles
		int vehicleNumber = Integer.parseInt((String) environment.getConfig().get("AgentNumber"));
		vehiclesStock.addAll(vehicleFactory.create(vehicleNumber));
	}

	/**
	 * Override the createArc method of the OsmScenario, so that PollutableOsmArcs will
	 * be created instead of normal OsmArcs by the OsmJSONReader.
	 * 
	 */
	@Override
	public OsmArc createArc(
			String id,
			Road road,
			OsmNode startNode,
			OsmNode targetNode,
			int lanes,
			String type) {
		return new PollutableOsmArc(
				id,
				road,
				startNode,
				targetNode,
				lanes,
				type
				);
	}
	
	@Override
	public OsmAgentBody createAgentBody(
			OsmContext environment) {
		return new DeliveryDriver(
				vehiclesStock.poll(),
				environment);
	}
	
		
}
