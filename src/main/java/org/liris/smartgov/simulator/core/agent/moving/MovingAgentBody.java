package org.liris.smartgov.simulator.core.agent.moving;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.liris.smartgov.simulator.core.agent.core.AgentBody;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.agent.moving.events.MoveEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.arc.ArcLeftEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.arc.ArcReachedEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.node.DestinationReachedEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.node.NodeReachedEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.node.OriginReachedEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.parking.EnterParkingAreaEvent;
import org.liris.smartgov.simulator.core.agent.moving.events.parking.LeaveParkingAreaEvent;
import org.liris.smartgov.simulator.core.agent.moving.plan.Plan;
import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.core.environment.graph.events.AgentArrival;
import org.liris.smartgov.simulator.core.environment.graph.events.AgentDeparture;
import org.liris.smartgov.simulator.core.environment.graph.events.AgentDestination;
import org.liris.smartgov.simulator.core.environment.graph.events.AgentOrigin;
import org.liris.smartgov.simulator.core.events.EventHandler;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract implementation of an AgentBody to represent agents moving in the simulation.
 * <p>
 * Handles MoverActions thanks to the dedicated functions to implement.
 * </p>
 *
 * <p>
 * Also provides lots of events relative to the MovingBehavior and move actions in general,
 * useful to make the simulation more dynamic.
 * </p>
 */
public abstract class MovingAgentBody extends AgentBody<MoverAction> {

	private Plan plan;
	
	// Listeners collections for each event type
	private Collection<EventHandler<MoveEvent>> agentMoveListeners;
	private Collection<EventHandler<NodeReachedEvent>> nodeReachedListeners;
	private Collection<EventHandler<ArcReachedEvent>> arcReachedListeners;
	private Collection<EventHandler<ArcLeftEvent>> arcLeftListeners;
	private Collection<EventHandler<OriginReachedEvent>> originReachedListeners;
	private Collection<EventHandler<DestinationReachedEvent>> destinationReachedListeners;
	private Collection<EventHandler<EnterParkingAreaEvent>> parkingEnteredListeners;
	private Collection<EventHandler<LeaveParkingAreaEvent>> parkingLeftListeners;

	/**
	 * MovingAgentBody constructor.
	 */	
	public MovingAgentBody() {
		this.agentMoveListeners = new ArrayList<>();
		this.nodeReachedListeners = new ArrayList<>();
		this.arcReachedListeners = new ArrayList<>();
		this.arcLeftListeners = new ArrayList<>();
		this.originReachedListeners = new ArrayList<>();
		this.destinationReachedListeners = new ArrayList<>();
		this.parkingEnteredListeners = new ArrayList<>();
		this.parkingLeftListeners = new ArrayList<>();
		
		this.plan = new Plan();
		
		this.plan.addNextNodeListener((event) ->
				buildAndTriggerEventsAtMove(
					event.getOldArc(),
					event.getOldNode(),
					event.getNewArc(),
					event.getNewNode())
				);
		
		this.plan.addFirstNodeListener((event) -> {
				// Node reached events
				triggerNodeReachedListeners(new NodeReachedEvent(event.getFirstNode()));
				event.getFirstNode().triggerAgentArrivalListeners(new AgentArrival((MovingAgent) getAgent()));
				
				// Origin event
				event.getFirstNode().triggerAgentOriginListeners(new AgentOrigin((MovingAgent) getAgent()));
				triggerOriginReachedListeners(new OriginReachedEvent(event.getFirstNode()));
				
				// Arc reached events
				triggerArcReachedListeners(new ArcReachedEvent(event.getFirstArc()));
				event.getFirstArc().triggerAgentArrivalListeners(new AgentArrival((MovingAgent) getAgent()));

			}
		);
	}

	/**
	 * Plan of this agent.
	 *
	 * @return plan of this agent
	 */
	public Plan getPlan() {
		return plan;
	}

	/**
	 * Update the plan of this agent body with the specified nodes.
	 *
	 * <p>
	 * All the nodes of the plan will be replaced, from origin to destination.
	 * </p>
	 *
	 * @see Plan#update
	 * @param nodes new nodes of agent body's plan
	 */
	public void updatePlan(List<? extends Node> nodes) {
		plan.update(nodes);
	}

	/**
	 * Handles the specified MoverActions, thanks to the implemented <code>handle</code>
	 * functions of this class.
	 *
	 * <p>
	 * Also triggers available events accordingly.
	 * </p>
	 *
	 * @param action action to do
	 */	
	public void doAction(MoverAction action){
		// TODO : other event listeners
		switch(action.getType()){
		case MOVE:
			/*
			 * Events are handled through the plan next node events. See this constructor.
			 */
			Arc oldArc = plan.getCurrentArc();
			Node oldNode = plan.getCurrentNode();
			handleMove();
			Arc newArc = plan.getCurrentArc();
			Node newNode = plan.getCurrentNode();
			// Move event, always triggered
			triggerOnMoveListeners(
				new MoveEvent(
					oldArc,
					newArc,
					oldNode,
					newNode
					)
				);
			break;
		case WAIT:
			handleWait();
			break;
		case ENTER:
			handleEnter(action.getParkingArea());
			triggerParkingEnteredListeners(new EnterParkingAreaEvent(action.getParkingArea()));
			break;
		case LEAVE:
			handleLeave(action.getParkingArea());
			triggerParkingLeftListeners(new LeaveParkingAreaEvent(action.getParkingArea()));
			break;
		default:
			handleWander();
		}
	}
	
	private void buildAndTriggerEventsAtMove(Arc oldArc, Node oldNode, Arc newArc, Node newNode) {
		
		// Arc relative events
		if(oldArc != newArc) {
			oldArc.triggerAgentDepartureListeners(new AgentDeparture((MovingAgent) getAgent()));
			triggerArcLeftListeners(new ArcLeftEvent(oldArc));

			newArc.triggerAgentArrivalListeners(new AgentArrival((MovingAgent) getAgent()));
			triggerArcReachedListeners(new ArcReachedEvent(newArc));

		}
		
		// Node relative events
		if(oldNode != newNode) {
			oldNode.triggerAgentDepartureListeners(new AgentDeparture((MovingAgent) getAgent()));

			newNode.triggerAgentArrivalListeners(new AgentArrival((MovingAgent) getAgent()));
			triggerNodeReachedListeners(new NodeReachedEvent(newNode));

		}
		
		// Has the agent reached its destination?
		if(plan.isComplete()) {
			/*
			 * When the agent has reached its destination, there is no next arc, so oldArc = newArc,
			 * but we still need to notify that the last arc has been left.
			 */
			oldArc.triggerAgentDepartureListeners(new AgentDeparture((MovingAgent) getAgent()));
			triggerArcLeftListeners(new ArcLeftEvent(oldArc));
			
			/*
			 * Destination reached listeners
			 */
			newNode.triggerAgentDestinationListeners(new AgentDestination((MovingAgent) getAgent()));
			triggerDestinationReachedListeners(new DestinationReachedEvent(newNode));
		}
	}

	/**
	 * Automatically called to perform a {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction#MOVE MOVE} action.
	 */	
	public abstract void handleMove();

	/**
	 * Automatically called to perform a {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction#WAIT WAIT} action.
	 */	
	public abstract void handleWait();

	/**
	 * Automatically called to perform a {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction#WANDER WANDER} action.
	 */	
	public abstract void handleWander();

	/**
	 * Automatically called to perform a {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction#ENTER ENTER} action
	 * in the specified ParkingArea.
	 *
	 * @param parkingArea parking area to enter in
	 */	
	public void handleEnter(ParkingArea parkingArea) {
		parkingArea.enter((MovingAgent) this.getAgent());
	}

	/**
	 * Automatically called to perform a {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction#LEAVE LEAVE} action
	 * in the specified ParkingArea.
	 *
	 * @param parkingArea parking area to leave from
	 */	
	public void handleLeave(ParkingArea parkingArea) {
		parkingArea.leave((MovingAgent) this.getAgent());
	}
	
	// Move listeners
	/**
	 * Adds a new handler for MoveEvents.
	 *
	 * Triggered each time a MOVE action is performed, just after {@link #handleMove} is called.
	 *
	 * @param moveListener move event handler to add
	 */
	public void addOnMoveListener(EventHandler<MoveEvent> moveListener) {
		agentMoveListeners.add((EventHandler<MoveEvent>) moveListener);
	}
	
	private void triggerOnMoveListeners(MoveEvent event) {
		for (EventHandler<MoveEvent> eventHandler : agentMoveListeners) {
			eventHandler.handle(event);
		}
	}

	/**
	 * EventHandlers for MoveEvents.
	 *
	 * @return current move listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<MoveEvent>> getAgentMoveListeners() {
		return agentMoveListeners;
	}

	// Node reached listeners
	/**
	 * Adds a new handler for NodeReachedEvents.
	 *
	 * <p> 
	 * Triggered each time the agent cross a node, or stop on it. Also triggered when the
	 * agent "reach" the origin node that correspond to its behavior. In consequence, in the
	 * case of an agent that reach its node destination and has this same node as origin for
	 * its new plan, listeners will be called twice on the same node : when the agent reach the
	 * node as its destination, and again when it reaches it as its origin (even if, technically,
	 * it has not moved).
	 * </p>
	 *
	 * <p>
	 * If what you want to implement is behaviors at origin and / or destination, please also consider
	 * {@link #addOnOriginReachedListener addOnOriginReachedListener} and
	 * {@link #addOnDestinationReachedListener addOnDestinationReachedListener}.
	 * </p>
	 *
	 * @param nodeReachedListener node reached event handler to add
	 */
	public void addOnNodeReachedListener(EventHandler<NodeReachedEvent> nodeReachedListener) {
		nodeReachedListeners.add((EventHandler<NodeReachedEvent>) nodeReachedListener);
	}
	
	private void triggerNodeReachedListeners(NodeReachedEvent event) {
		for (EventHandler<NodeReachedEvent> eventHandler : nodeReachedListeners) {
			eventHandler.handle(event);
		}
	}
	
	/**
	 * EventHandlers for NodeReachedEvents.
	 *
	 * @return current node reached listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<NodeReachedEvent>> getNodeReachedListeners() {
		return nodeReachedListeners;
	}

	// Arc reached listeners
	/**
	 * Adds a new handler for ArcReachedEvent.
	 *
	 * Triggered each time an agent reaches a new Arc.
	 *
	 * @param arcReachedListener new arc reached event handler
	 */
	public void addOnArcReachedListener(EventHandler<ArcReachedEvent> arcReachedListener) {
		arcReachedListeners.add((EventHandler<ArcReachedEvent>) arcReachedListener);
	}
	
	private void triggerArcReachedListeners(ArcReachedEvent event) {
		for (EventHandler<ArcReachedEvent> eventHandler : arcReachedListeners) {
			eventHandler.handle(event);
		}
	}
	
	/**
	 * EventHandlers for ArcReachedEvents.
	 *
	 * @return current arc reached event listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<ArcReachedEvent>> getArcReachedListeners() {
		return arcReachedListeners;
	}

	// Arc left listeners
	/**
	 * Adds a new handler for ArcLeftEvents.
	 *
	 * Called each time an agent leave an Arc.
	 *
	 * @param arcLeftListener new arc left event handler to add
	 */
	public void addOnArcLeftListener(EventHandler<ArcLeftEvent> arcLeftListener) {
		arcLeftListeners.add((EventHandler<ArcLeftEvent>) arcLeftListener);
	}
	
	private void triggerArcLeftListeners(ArcLeftEvent event) {
		for (EventHandler<ArcLeftEvent> eventHandler : arcLeftListeners) {
			eventHandler.handle(event);
		}
	}

	/**
	 * EventHandlers for ArcLeftEvents.
	 *
	 * @return current arc left event listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<ArcLeftEvent>> getArcLeftListeners() {
		return arcLeftListeners;
	}

	// Destination reached listeners
	/**
	 * Adds a new handler for OriginReachedEvent.
	 *
	 * Triggered at the very first step of the agent Plan, when {@link #updatePlan updatePlan()} is called,
	 * on the origin node of its {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MovingBehavior}. 
	 *
	 * @param originReachedListener new origin reached event handler to add
	 */
	public void addOnOriginReachedListener(EventHandler<OriginReachedEvent> originReachedListener) {
		originReachedListeners.add((EventHandler<OriginReachedEvent>) originReachedListener);
	}

	/**
	 * EventHandlers for OriginReachedEvents.
	 *
	 * @return current origin reached event listeners
	 */	
	private void triggerOriginReachedListeners(OriginReachedEvent event) {
		for (EventHandler<OriginReachedEvent> eventHandler : originReachedListeners) {
			eventHandler.handle(event);
		}
	}
		
	// Destination reached listeners
	/**
	 * Adds a new handler for DestinationReached.
	 *
	 * Triggered when the agent has reached the last node of its current {@link Plan}, that corresponds
	 * to the destination node of its {@link org.liris.smartgov.simulator.core.agent.moving.behavior.MovingBehavior}.
	 *
	 * @param destinationReachedListener new destination reached event
	 * handler to add
	 */
	public void addOnDestinationReachedListener(EventHandler<DestinationReachedEvent> destinationReachedListener) {
		destinationReachedListeners.add((EventHandler<DestinationReachedEvent>) destinationReachedListener);
	}
	
	private void triggerDestinationReachedListeners(DestinationReachedEvent event) {
		for (EventHandler<DestinationReachedEvent> eventHandler : destinationReachedListeners) {
			eventHandler.handle(event);
		}
	}

	/**
	 * EventHandlers for DestinationReachedEvents.
	 *
	 * @return current destination reached event listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<DestinationReachedEvent>> getDestinationReachedListeners() {
		return destinationReachedListeners;
	}
	
	// Parking entered listeners
	/**
	 * Adds a new handler for ParkingEntered.
	 *
	 * Triggered when the agent enters a ParkingArea.
	 *
	 * @param parkingEnteredListener new parking entered event
	 * handler to add
	 */
	public void addOnParkingEnteredListener(EventHandler<EnterParkingAreaEvent> parkingEnteredListener) {
		parkingEnteredListeners.add((EventHandler<EnterParkingAreaEvent>) parkingEnteredListener);
	}
	
	private void triggerParkingEnteredListeners(EnterParkingAreaEvent event) {
		for (EventHandler<EnterParkingAreaEvent> eventHandler : parkingEnteredListeners) {
			eventHandler.handle(event);
		}
	}

	/**
	 * EventHandlers for EnterParkingAreaEvents.
	 *
	 * @return current parking entered event listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<EnterParkingAreaEvent>> getParkingEnteredListeners() {
		return parkingEnteredListeners;
	}
	
	// Parking left listeners
	/**
	 * Adds a new handler for ParkingLeft.
	 *
	 * Triggered when the agent leaves a ParkingArea.
	 *
	 * @param parkingLeftListener new parking left event
	 * handler to add
	 */
	public void addOnParkingLeftListener(EventHandler<LeaveParkingAreaEvent> parkingLeftListener) {
		parkingLeftListeners.add((EventHandler<LeaveParkingAreaEvent>) parkingLeftListener);
	}
	
	private void triggerParkingLeftListeners(LeaveParkingAreaEvent event) {
		for (EventHandler<LeaveParkingAreaEvent> eventHandler : parkingLeftListeners) {
			eventHandler.handle(event);
		}
	}

	/**
	 * EventHandlers for LeaveParkingAreaEvents.
	 *
	 * @return current parking left event listeners
	 */
	@JsonIgnore
	public Collection<EventHandler<LeaveParkingAreaEvent>> getParkingLeftListeners() {
		return parkingLeftListeners;
	}
	
}
