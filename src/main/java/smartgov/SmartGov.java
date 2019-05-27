/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package smartgov;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartgov.core.environment.SmartGovContext;
import smartgov.core.main.SimulationBuilder;
import smartgov.urban.osm.environment.OsmContext;

public class SmartGov {
	
	public static final Logger logger = LogManager.getLogger(SmartGov.class);
	
	private SimulationBuilder simulationBuilder;
	
	/**
	 * Config File with parameters for simulations.
	 */
	// public static String configFile = FilePath.inputFolder + "config.ini";
	
	public SmartGov(SmartGovContext context) {
		logger.info("Starting SmartGov");
		simulationBuilder = new SimulationBuilder(context);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String outputFolder = context.getFiles().getFile("outputFolder");
		
		File nodeOutput = new File(outputFolder + File.separator + "init_nodes.json");
		File arcOutput = new File(outputFolder + File.separator + "init_arcs.json");
		try {
			logger.info("Saving initial nodes to " + nodeOutput.getPath());
			objectMapper.writeValue(nodeOutput, context.nodes.values());
			
			logger.info("Saving initial arcs to " + arcOutput.getPath());
			objectMapper.writeValue(arcOutput, context.arcs.values());
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static void main(String[] args) {
        new SmartGov(new OsmContext(args[0]));
    }
    
    public SimulationBuilder getSimulationBuilder() {
    	return simulationBuilder;
    }
}
