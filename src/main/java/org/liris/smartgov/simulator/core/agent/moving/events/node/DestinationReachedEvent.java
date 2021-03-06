package org.liris.smartgov.simulator.core.agent.moving.events.node;

import org.liris.smartgov.simulator.core.environment.graph.Node;

/**
 * Event triggered when an Agent reached his destination, defined in
 * its {@link org.liris.smartgov.simulator.core.agent.moving.plan.Plan Plan}. More exactly, the event
 * is triggered when pathComplete is set to true in the Plan.
 * 
 * @author pbreugnot
 *
 */
public class DestinationReachedEvent extends NodeReachedEvent {

	public DestinationReachedEvent(Node destination) {
		super(destination);
	}
	

}
