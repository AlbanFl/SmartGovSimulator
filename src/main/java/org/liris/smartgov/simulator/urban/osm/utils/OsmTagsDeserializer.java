package org.liris.smartgov.simulator.urban.osm.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.liris.smartgov.simulator.urban.osm.environment.graph.tags.Highway;
import org.liris.smartgov.simulator.urban.osm.environment.graph.tags.Oneway;
import org.liris.smartgov.simulator.urban.osm.environment.graph.tags.OsmTag;
import org.liris.smartgov.simulator.urban.osm.environment.graph.tags.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


public class OsmTagsDeserializer extends StdDeserializer<Map<String, OsmTag>> {

	private static final long serialVersionUID = 1L;

	public OsmTagsDeserializer() {
		this(null);
	}
	
	protected OsmTagsDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Map<String, OsmTag> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Map<String, OsmTag> tags = new HashMap<>();
		
		JsonNode node = p.getCodec().readTree(p);
		Oneway oneway = Oneway.NO; // If no oneway tag is present, keep this value
		Highway highway = Highway.OTHER;
		Service service = Service.NONE;
		
		Iterator<String> keyIterator = node.fieldNames();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();
			if(key.equals("oneway")) {
				String onewayStr = node.get("oneway").asText();
				if (onewayStr.equals("-1")) {
					/* According to the OSM documentation (https://wiki.openstreetmap.org/wiki/Key:oneway),
					 * This is supposed to be a very rare case. It means that
					 * the current way is one-way, be in the opposite order of its node.
					 */
					oneway = Oneway.REVERSED;
				}
				else if (onewayStr.equals("yes")) {
					oneway = Oneway.YES;
				}
				else {
					/*
					 * Other values are considered as not one-way ("no", "reversible", "alternating",...)
					 */
					oneway = Oneway.NO;
				}
			}
			
			if(key.equals("highway")) {
				highway = Highway.fromOsmTag(node.get("highway").asText());
			}
			
			if(key.equals("service")) {
				service = Service.fromOsmTag(node.get("service").asText());
			}
		}
		tags.put("oneway", oneway);
		tags.put("highway", highway);
		tags.put("service", service);
		
		return tags;
	}

}
