package org.liris.smartgov.simulator.urban.osm.environment.graph;

import static org.hamcrest.MatcherAssert.assertThat; 
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;
import org.liris.smartgov.simulator.urban.osm.utils.OsmLoader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class OsmNodeTest {
	
	public static final File testNodes = new File(OsmNodeTest.class.getResource("../../nodes.json").getFile());

	public static Map<String, OsmNode> loadNodes(File file) throws JsonParseException, JsonMappingException, IOException {
		OsmLoader<OsmNode> loader = new OsmLoader<>();
		List<OsmNode> osmNodes = loader.loadOsmElements(
				file,
				OsmNode.class
				);
		
		Map<String, OsmNode> nodeMap = new HashMap<>();
		for(OsmNode node : osmNodes) {
			nodeMap.put(node.getId(), node);
		}
		
		return nodeMap;
	}
	@Test
	public void loadOsmNodesFromJson() throws JsonParseException, JsonMappingException, IOException {
		Collection<OsmNode> osmNodes = loadNodes(testNodes).values();
		
		assertThat(
				osmNodes,
				hasSize(1179)
				);
		
		for (OsmNode osmNode : osmNodes) {
			assertThat(
					osmNode.getId(),
					notNullValue()
					);
			assertThat(
					osmNode.getPosition().lon,
					notNullValue()
					);
			assertThat(
					osmNode.getPosition().lat,
					notNullValue()
					);
		}
	}
}
