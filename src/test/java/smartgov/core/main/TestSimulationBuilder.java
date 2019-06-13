package smartgov.core.main;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import smartgov.core.agent.core.Agent;
import smartgov.core.agent.moving.MovingAgentBody;
import smartgov.core.agent.moving.Plan;
import smartgov.core.agent.moving.behavior.TestMovingBehavior;
import smartgov.core.environment.TestSmartGovContext;
import smartgov.core.environment.graph.Node;
import smartgov.core.simulation.TestScenario;

public class TestSimulationBuilder {

	@Test
	public void testLoadWorld() {
		SimulationBuilder simulationBuilder = new SimulationBuilder(TestSmartGovContext.loadTestContext());
		simulationBuilder.build();
		
		assertThat(
				simulationBuilder.getContext().getScenario() instanceof TestScenario,
				equalTo(true)
				);
		
		assertThat(
			simulationBuilder.getContext().nodes.keySet(),
			hasSize(5)
			);
		
		assertThat(
			simulationBuilder.getContext().arcs.keySet(),
			hasSize(10)
			);
		
		assertThat(
			simulationBuilder.getContext().agents.keySet(),
			hasSize(2)
			);
		
		assertThat(
			simulationBuilder.getContext().getGraph(),
			notNullValue()
				);
	}
	
	@Test
	public void testBehaviorInitialization() {
		SimulationBuilder simulationBuilder = new SimulationBuilder(TestSmartGovContext.loadTestContext());
		simulationBuilder.build();
		
		for(Agent agent : simulationBuilder.getContext().agents.values()) {
			assertThat(
					agent.getBehavior() instanceof TestMovingBehavior,
					equalTo(true)
					);
		}
		
		
		TestMovingBehavior behavior1 = (TestMovingBehavior) simulationBuilder.getContext().agents.get("1").getBehavior();
		assertThat(
				behavior1.getOrigin().getId(),
				equalTo("1")
				);
		
		assertThat(
				behavior1.getDestination().getId(),
				equalTo("5")
				);
		
		TestMovingBehavior behavior2 = (TestMovingBehavior) simulationBuilder.getContext().agents.get("2").getBehavior();
		assertThat(
				behavior2.getOrigin().getId(),
				equalTo("2")
				);
		
		assertThat(
				behavior2.getDestination().getId(),
				equalTo("4")
				);
	}
	
	@Test 
	public void testPlanInitialization() {
		SimulationBuilder simulationBuilder = new SimulationBuilder(TestSmartGovContext.loadTestContext());
		simulationBuilder.build();
		
		Plan plan1 = ((MovingAgentBody) simulationBuilder.getContext().agents.get("1").getBody()).getPlan();
		ArrayList<String> expectedIds = new ArrayList<>();
		for(Node node : plan1.getNodes()) {
			expectedIds.add(node.getId());
		}
		assertThat(
				expectedIds,
				equalTo(Arrays.asList("1", "2", "5"))
				);
		
		Plan plan2 = ((MovingAgentBody) simulationBuilder.getContext().agents.get("2").getBody()).getPlan();
		expectedIds = new ArrayList<>();
		for(Node node : plan2.getNodes()) {
			expectedIds.add(node.getId());
		}
		assertThat(
				expectedIds,
				equalTo(Arrays.asList("2", "3", "4"))
				);
	}
}
