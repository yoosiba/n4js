/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.flowgraphs.analysis;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.n4js.flowgraphs.FlowEdge;
import org.eclipse.n4js.flowgraphs.model.ControlFlowEdge;
import org.eclipse.n4js.flowgraphs.model.Node;
import org.eclipse.n4js.flowgraphs.model.RepresentingNode;
import org.eclipse.n4js.n4JS.ControlFlowElement;

/**
 * see {@link GraphVisitorInternal}
 */
abstract public class GraphVisitor extends GraphVisitorInternal {
	final private Set<ControlFlowEdge> visitedEdgesInternal = new HashSet<>();
	final private Set<FlowEdge> visitedEdges = new HashSet<>();

	/** see {@link GraphVisitorInternal#GraphVisitorInternal(TraverseDirection )} */
	protected GraphVisitor(TraverseDirection direction) {
		this(null, direction);
	}

	/** see {@link GraphVisitorInternal#GraphVisitorInternal(ControlFlowElement, TraverseDirection)} */
	public GraphVisitor(ControlFlowElement container, TraverseDirection direction) {
		super(container, direction);
	}

	@Override
	final protected void visit(Node node) {
		if (node instanceof RepresentingNode) {
			ControlFlowElement cfe = node.getRepresentedControlFlowElement();
			visit(cfe);
		}
	}

	@Override
	final protected void visit(Node lastVisitNode, Node currentNode, ControlFlowEdge edge) {
		super.visit(lastVisitNode, currentNode, edge);
	}

	@Override
	final protected void initializeContainerInternal(ControlFlowElement curContainer) {
		visitedEdgesInternal.clear();
		visitedEdges.clear();
		initializeContainer(curContainer);
	}

	/**
	 * Called after {@link #initialize()} and before any visit-method is called.
	 *
	 * @param curContainer
	 *            containing {@link ControlFlowElement} of succeeding calls to visit-methods
	 */
	protected void initializeContainer(ControlFlowElement curContainer) {
		// overwrite me
	}

	/**
	 * Analog to {@link GraphVisitorInternal#visit(Node)}
	 *
	 * @param cfe
	 *            {@link ControlFlowElement} that is visited
	 */
	protected void visit(ControlFlowElement cfe) {
		// overwrite me
	}

}
