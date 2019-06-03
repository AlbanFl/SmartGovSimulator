package smartgov.core.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import org.locationtech.jts.geom.GeometryFactory;

import smartgov.SmartGov;
import smartgov.core.agent.AbstractAgent;
import smartgov.core.agent.AbstractAgentBody;
import smartgov.core.environment.graph.OrientedGraph;
import smartgov.core.environment.graph.SinkNode;
import smartgov.core.environment.graph.SourceNode;
import smartgov.core.environment.graph.arc.Arc;
import smartgov.core.environment.graph.node.Node;
import smartgov.core.simulation.Scenario;

/**
 * Generic SmartGov environment.
 * The CoreEnvironment contains common elements that should be common to any SmartGov problems.
 * 
 * @author pbreugnot
 *
 * @param <Tnode> Node type
 * @param <Tarc> Arc type
 */

public class SmartGovContext extends AbstractContext {
	
	//Scenario
	private Scenario scenario;

	//Repast Static Variables
	public static final GeometryFactory GEOFACTORY = new GeometryFactory();
	
	public Map<String, AbstractAgent<?>> agents;

	public Map<String, Node> nodes;
	public Map<String, Arc> arcs;
	public OrientedGraph graph;
	private Map<String, SourceNode> sourceNodes;
	private Map<String, SinkNode> sinkNodes;
	
	//Manage human agent creation and allocation
	public static int AGENT_MAX; //Max agents in the simulation (determine by parameters)
	public Map<String, Queue<AbstractAgent<?>>> agentsStock; // Map SourceNodes ids to available agents
	
	//File names
	public static String outputFile;
	public static String stateFile;
	
	public SmartGovContext(String configFile) {
		super(configFile);
		agents = new HashMap<>();
		nodes = new HashMap<>();
		arcs = new HashMap<>();
		sourceNodes = new HashMap<>();
		sinkNodes = new HashMap<>();
		agentsStock = new HashMap<>();	
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}
	
	public Map<String, SourceNode> getSourceNodes() {
		return sourceNodes;
	}

	public Map<String, SinkNode> getSinkNodes() {
		return sinkNodes;
	}

	protected void resetSpecialList(){
		for(Queue<?> queue : agentsStock.values()){
				queue.clear();
		}
		agentsStock.clear();
	}
	
	@Override
	public void clear(){
		agents.clear(); //Agents are ordered by their id
		nodes.clear();
		arcs.clear();
		sourceNodes.clear();
		sinkNodes.clear();
		resetSpecialList();
	}

	@Override
	public Scenario loadScenario(String scenarioName) {
		SmartGov.logger.info("Loading Scenario : " + scenarioName);
		return null;
	}
}
