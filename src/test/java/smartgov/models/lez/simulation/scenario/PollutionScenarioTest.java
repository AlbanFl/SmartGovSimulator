package smartgov.models.lez.simulation.scenario;

import smartgov.SmartGov;
import smartgov.core.agent.AbstractAgent;
import smartgov.core.environment.graph.arc.Arc;
import smartgov.models.lez.agent.DeliveryDriver;
import smartgov.models.lez.environment.LezContext;
import smartgov.models.lez.environment.graph.PollutableOsmArc;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class PollutionScenarioTest {
	
	private SmartGov loadSmartGov() {
		return new SmartGov(new LezContext(this.getClass().getResource("pollution_scenario.properties").getFile()));
	}
	
	@Test
	public void testLoadPollutionScenario() {
		SmartGov smartGov = loadSmartGov();
		assertThat(
				smartGov.getContext().getScenario() instanceof PollutionScenario,
				equalTo(true)
				);
	}
	
	@Test
	public void testAgentBodiesType() {
		SmartGov smartGov = loadSmartGov();
		for(AbstractAgent agent : smartGov.getContext().agents.values()) {
			assertThat(
					agent.getBody() instanceof DeliveryDriver,
					equalTo(true)
					);
		}
	}
	
	@Test
	public void testArcsType() {
		SmartGov smartGov = loadSmartGov();
		for(Arc arc : smartGov.getContext().arcs.values()) {
			assertThat(
					arc instanceof PollutableOsmArc,
					equalTo(true)
					);
		}
	}
}
