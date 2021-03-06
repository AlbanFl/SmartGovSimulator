package org.liris.smartgov.simulator.core.simulation;

import org.junit.Test;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.SmartGovTest;
import org.liris.smartgov.simulator.core.agent.moving.MovingAgentBody;
import org.liris.smartgov.simulator.core.agent.moving.plan.Plan;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.SimulationRuntime;
import org.liris.smartgov.simulator.core.simulation.events.SimulationPaused;
import org.liris.smartgov.simulator.core.simulation.events.SimulationStep;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TestSmartGovRuntime {
	
	private SimulationRuntime loadRuntime() {
		SmartGovTest.loadSmartGov();
		return SmartGov.getRuntime();
	}

	@Test
	public void testStartSimulation() throws InterruptedException {
		SimulationRuntime runtime = loadRuntime();
		
		assertThat(
				runtime,
				notNullValue()
				);
		
		runtime.start(10);
		
		while(runtime.isRunning()) {
			// Wait
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		assertThat(
				runtime.getTickCount(),
				equalTo(10)
				);
	}
	
	@Test
	public void testDynamicBehavior() throws InterruptedException {
		SmartGov smartGov = SmartGovTest.loadSmartGov();

		SmartGov.getRuntime().start(7);
		
		while(SmartGov.getRuntime().isRunning()) {
			// Wait
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		Plan newPlan1 = ((MovingAgentBody) smartGov.getContext().agents.get("1").getBody()).getPlan();
		ArrayList<String> expectedIds = new ArrayList<>();
		for(Node node : newPlan1.getNodes()) {
			expectedIds.add(node.getId());
		}
		assertThat(
				expectedIds,
				equalTo(Arrays.asList("5", "3", "1"))
				);
		
		Plan newPlan2 = ((MovingAgentBody) smartGov.getContext().agents.get("2").getBody()).getPlan();
		expectedIds = new ArrayList<>();
		for(Node node : newPlan2.getNodes()) {
			expectedIds.add(node.getId());
		}
		assertThat(
				expectedIds,
				equalTo(Arrays.asList("4", "1", "2"))
				);
		
	}
	
	@Test
	public void testPause() throws InterruptedException {
		SmartGovTest.loadSmartGov();
		
		StepCounter stepCounter = new StepCounter();

		SmartGov.getRuntime().addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				stepCounter.stepCount ++;
				if (event.getTick() == 5) {
					SmartGov.getRuntime().pause();
				}
				
			}
			
		});
		
		SmartGov.getRuntime().addSimulationPausedListener(new EventHandler<SimulationPaused>() {

			@Override
			public void handle(SimulationPaused event) {
				assertThat(
						event.getTick(),
						equalTo(5)
						);
				
				assertThat(
						SmartGov.getRuntime().getTickCount(),
						equalTo(5)
						);
				
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Assert that the simulation is really on pause.
				assertThat(
						SmartGov.getRuntime().getTickCount(),
						equalTo(5)
						);
				
				// Finally, resume.
				SmartGov.getRuntime().resume();
			}
			
		});
		
		SmartGov.getRuntime().start(10);
		
		
		while(SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		// Assert that the simulation ended
		assertThat(
				SmartGov.getRuntime().getTickCount(),
				equalTo(10));
		
		// Assert that we only performed 10 ticks during the all process
		assertThat(
				stepCounter.stepCount,
				equalTo(10)
				);
	}
	
	private class StepCounter {
		public int stepCount = 0;
	}
	
	@Test
	public void assertThatStartWhileRunningThrowsException() throws InterruptedException {
		SmartGovTest.loadSmartGov();

		ExceptionThrownChecker checker = new ExceptionThrownChecker();
		
		SmartGov.getRuntime().addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				if (event.getTick() == 5) {
					// Should throw an exception
					try {
						SmartGov.getRuntime().start(10);
					}
					catch (IllegalStateException e) {
						e.printStackTrace();
						checker.exceptionThrown = true;
					}
				}
				
			}
			
		});

		// Give enough time to the second start to be triggered while the simulation is running
		SmartGov.getRuntime().start(10000);
		
		while (!checker.exceptionThrown && SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		try {
			SmartGov.getRuntime().stop();
		}
		catch(IllegalStateException e) {
			// The simulation has already ended, everything is ok.
		}
		
		assertThat(
				checker.exceptionThrown,
				equalTo(true)
				);
	}
	
	@Test
	public void assertThatStartWhilePauseThrowsException() throws InterruptedException {
		SmartGovTest.loadSmartGov();

		ExceptionThrownChecker checker = new ExceptionThrownChecker();
		
		SmartGov.getRuntime().addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				if (event.getTick() == 5) {
					// Should throw an exception
					try {
						SmartGov.getRuntime().pause();
					}
					catch (IllegalStateException e) {
						e.printStackTrace();
						checker.exceptionThrown = true;
					}
				}
				
			}
			
		});
		
		SmartGov.getRuntime().addSimulationPausedListener(new EventHandler<SimulationPaused>() {

			@Override
			public void handle(SimulationPaused event) {
				try {
					SmartGov.getRuntime().start();
				}
				catch(IllegalStateException e) {
					e.printStackTrace();
					checker.exceptionThrown = true;
				}
			}
			
		});
		
		SmartGov.getRuntime().start(10);
		
		while (!checker.exceptionThrown && SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		SmartGov.getRuntime().stop();
		
		assertThat(
				checker.exceptionThrown,
				equalTo(true)
				);
		
	}
	
	@Test(expected = IllegalStateException.class)
	public void assertThatStepWhilePauseThrowsException() {
		SmartGovTest.loadSmartGov();

		SmartGov.getRuntime().step();
	
	}
	
	@Test
	public void assertThatStepWhileRunningThrowsException() throws InterruptedException {
		SmartGovTest.loadSmartGov();

		ExceptionThrownChecker checker = new ExceptionThrownChecker();
		
		SmartGov.getRuntime().addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				if (event.getTick() == 5) {
					// Should throw an exception
					try {
						SmartGov.getRuntime().step();
					}
					catch (IllegalStateException e) {
						e.printStackTrace();
						checker.exceptionThrown = true;
					}
				}
				
			}
			
		});

		// Give enough time to step() to be called while the simulation is running
		SmartGov.getRuntime().start(10000);
		
		while (!checker.exceptionThrown && SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		SmartGov.getRuntime().stop();
		
		assertThat(
				checker.exceptionThrown,
				equalTo(true)
				);
	
	}
	
	@Test
	public void testStepOnPause() throws InterruptedException {
		SmartGovTest.loadSmartGov();
		
		SmartGov.getRuntime().addSimulationStepListener(new EventHandler<SimulationStep>() {

			@Override
			public void handle(SimulationStep event) {
				if (event.getTick() == 5) {
					SmartGov.getRuntime().pause();
					SmartGov.getRuntime().step();
					SmartGov.getRuntime().step();
					assertThat(
							SmartGov.getRuntime().getTickCount(),
							equalTo(7)
							);
					SmartGov.getRuntime().resume();
				}
				
			}
			
		});
		
		SmartGov.getRuntime().start(10);
		
		while(!SmartGov.getRuntime().isRunning()) {
			TimeUnit.MICROSECONDS.sleep(10);
		}
		
		
	}
	
	private class ExceptionThrownChecker {
		public boolean exceptionThrown = false;
	}
	
	@Test
	public void testClock() throws InterruptedException {
		SmartGovTest.loadSmartGov();
		SmartGov.getRuntime().start(3 * 24 * 3600 + 15 * 3600 + 10 * 60 + 24);
		
		EventChecker endCheck = new EventChecker();
		
		SmartGov.getRuntime().addSimulationStoppedListener((event) -> {
			Date date = event.getDate();
			assertThat(
					date.getDay(),
					equalTo(3)
					);

			assertThat(
					date.getWeekDay(),
					equalTo(WeekDay.THURSDAY)
					);
			
			assertThat(
					date.getHour(),
					equalTo(15)
					);
			
			assertThat(
					date.getMinutes(),
					equalTo(10)
					);
			
			assertThat(
					date.getSeconds(),
					equalTo(24.)
					);
			endCheck.check();
		});
		SmartGov.getRuntime().waitUntilSimulatioEnd();
		
		TimeUnit.MICROSECONDS.sleep(100);
		
		assertThat(
				endCheck.hasBeenTriggered(),
				equalTo(true)
				);
		
	}
	
	private class EventChecker {
		private Boolean triggered = false;
		
		public void check() {
			this.triggered = true;
		}
		
		public Boolean hasBeenTriggered() {
			return triggered;
		}
	}
}
