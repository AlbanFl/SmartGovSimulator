package smartgov.urban.osm.environment;

import smartgov.core.environment.SmartGovContext;
import smartgov.core.simulation.Scenario;
import smartgov.urban.osm.environment.graph.Road;
import smartgov.urban.osm.environment.graph.sinkSourceNodes.SinkNode;
import smartgov.urban.osm.environment.graph.sinkSourceNodes.SourceNode;
import smartgov.urban.osm.simulation.scenario.lowLayer.RandomTrafficScenario;
import smartgov.urban.osm.simulation.scenario.lowLayer.ScenarioVisualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoreEnvironment extension that adds OSM specific features to the simulation environment.
 * 
 * @author pbreugnot
 *
 */
public class OsmContext extends SmartGovContext {
	
	public List<Road> roads;
	
	private Map<String, SourceNode> sourceNodes;
	private Map<String, SinkNode> sinkNodes;
	
	public OsmContext(String configFile) {
		super(configFile);
		roads = new ArrayList<>();
		sourceNodes = new HashMap<>();
		sinkNodes = new HashMap<>();
	}
	
	public Map<String, SourceNode> getSourceNodes() {
		return sourceNodes;
	}

	public Map<String, SinkNode> getSinkNodes() {
		return sinkNodes;
	}
	
	@Override
	public void clear(){
		super.clear();
		roads.clear();
		sourceNodes.clear();
		sinkNodes.clear();
	}
	
	@Override
	public Scenario loadScenario(String scenarioName) {
		super.loadScenario(scenarioName);
		switch (scenarioName) {
			case ScenarioVisualization.name:
				return new ScenarioVisualization();
			case RandomTrafficScenario.name:
				return new RandomTrafficScenario();
			default:
				return null;
		}
	}
}
