package org.liris.smartgov.simulator.core.agent.moving.events;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.events.MoveEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.scenario.TestEventsContext;
import org.liris.smartgov.simulator.core.agent.moving.events.scenario.TestEventsScenario;
/*
 * Test that all arc relative events are triggered, thanks to the
 * shuttle test scenario.
 * 
 * To do so, we will focus on the shuttle 1, that goes from node 1 to 5.
 * 
 * In terms of arcs, it has this trajectory :
 * - 1 -> 2 -> 5
 * - 5 -> 3 -> 1
 * 
 * Here, we just do 1 -> 2 -> 5 -> 3
 */
public class TestMoveEvent {
	
	private TestEventsScenario runSimulation() throws InterruptedException {
		SmartGov smartGov = new SmartGov(new TestEventsContext());
		
		SmartGov.getRuntime().start(3);
		
		while(SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		return (TestEventsScenario) smartGov.getContext().getScenario();
	}
	
	@Test
	public void testMoveEventsAreTriggered() throws InterruptedException {
		
		assertThat(
			runSimulation().moveEvents,
			hasSize(3)
			);
	}
	
	@Test
	public void testMoveEventData() throws InterruptedException {
		
		TestEventsScenario result = runSimulation();
		
		List<String> oldArcIds = new ArrayList<>();
		List<String> newArcIds = new ArrayList<>();
		List<String> oldNodeIds = new ArrayList<>();
		List<String> newNodeIds = new ArrayList<>();
		
		for(MoveEvent event : result.moveEvents) {
			oldArcIds.add(event.getOldArc().getId());
			newArcIds.add(event.getNewArc().getId());
			oldNodeIds.add(event.getOldNode().getId());
			newNodeIds.add(event.getNewNode().getId());
		}
		
		/*
		 * A move event with a null old arc is sent at plan initialization 
		 */
		assertThat(
			oldArcIds,
			contains("1", "2", "4")
			);

		assertThat(
				newArcIds,
				contains("2", "4", "8")
				);
		
		assertThat(
				oldNodeIds,
				contains("1", "2", "5")
				);
		
		assertThat(
				newNodeIds,
				contains("2", "5", "3")
				);
	}
}
