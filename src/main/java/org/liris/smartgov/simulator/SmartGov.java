/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.liris.smartgov.simulator;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.SimulationBuilder;
import org.liris.smartgov.simulator.core.simulation.SimulationRuntime;
import org.liris.smartgov.simulator.core.simulation.events.SimulationStopped;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SmartGov {
	
	public static final Logger logger = LogManager.getLogger(SmartGov.class);
	
	protected SmartGovContext context;
	protected static SimulationBuilder simulationBuilder;
	protected static SimulationRuntime smartGovRuntime;
	
	
	public SmartGov(SmartGovContext context) {
		logger.info("Starting SmartGov");
		this.context = context;
		smartGovRuntime = new SimulationRuntime(context);
		simulationBuilder = new SimulationBuilder(context);
		simulationBuilder.build();
	
	}

	public SmartGov(SmartGovContext context, SimulationRuntime smartGovRuntime) {
		logger.info("Starting SmartGov");
		if (! smartGovRuntime.getContext().equals(context)) {
			throw new IllegalArgumentException("The contexts are not the same");
		}
		this.context = context;
		this.smartGovRuntime = smartGovRuntime;
		simulationBuilder = new SimulationBuilder(context);
		simulationBuilder.build();
	}
	
	public SmartGov(SmartGovContext context, SimulationRuntime smartGovRuntime, SimulationBuilder simulationBuilder) {
		if ( (! simulationBuilder.getContext().equals(context) ) ||  (! smartGovRuntime.getContext().equals(context) )) {
			throw new IllegalArgumentException("The contexts are not the same");
		}
		this.context = context;
		this.smartGovRuntime = smartGovRuntime;
		this.simulationBuilder = simulationBuilder;
		this.simulationBuilder.build();
	}

	
    public static void main(String[] args) {
        SmartGov smartGov = new SmartGov(new OsmContext(args[0]));
        getRuntime().addSimulationStoppedListener(new EventHandler<SimulationStopped>() {

			@Override
			public void handle(SimulationStopped event) {
				String outputFolder = smartGov.getContext().getFileLoader().load("outputFolder").getAbsolutePath();
				File agentOutput = new File(outputFolder + File.separator + "agents_" + getRuntime().getTickCount() +".json");
				File arcsOutput = new File(outputFolder + File.separator + "arcs_" + getRuntime().getTickCount() +".json");
				// File pollutionPeeks = new File(outputFolder + File.separator + "pollution_peeks_" + getRuntime().getTickCount() +".json");
				
				
				ObjectMapper objectMapper = new ObjectMapper();

				try {
					logger.info("Saving agents state to " + agentOutput.getPath());
					objectMapper.writeValue(agentOutput, smartGov.getContext().agents.values());
					
					logger.info("Saving arcs state to " + agentOutput.getPath());
					objectMapper.writeValue(arcsOutput, smartGov.getContext().arcs.values());
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        });
        
    	
		ObjectMapper objectMapper = new ObjectMapper();
		String outputFolder = null;
		
		try {
			outputFolder = smartGov.getContext().getFileLoader().load("outputFolder").getAbsolutePath();
		} catch (IllegalArgumentException e) {
			logger.warn("No outputFolder specified in the input configuration.");
		}
		
		if(outputFolder != null) {
			File nodeOutput = new File(outputFolder + File.separator + "init_nodes.json");
			File arcOutput = new File(outputFolder + File.separator + "init_arcs.json");

			try {
				// Using maps is simpler when processed in JS, but IDs are duplicated.
				logger.info("Saving initial nodes to " + nodeOutput.getPath());
				objectMapper.writeValue(nodeOutput, smartGov.getContext().nodes.values());
				
				logger.info("Saving initial arcs to " + arcOutput.getPath());
				objectMapper.writeValue(arcOutput, smartGov.getContext().arcs.values());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
        getRuntime().start(43200);
		// getRuntime().start(100);
    }
    
    public SmartGovContext getContext() {
    	return context;
    }
    
    public static SimulationBuilder getSimulationBuilder() {
    	return simulationBuilder;
    }
    
    public static SimulationRuntime getRuntime() {
    	return smartGovRuntime;
    }
}
