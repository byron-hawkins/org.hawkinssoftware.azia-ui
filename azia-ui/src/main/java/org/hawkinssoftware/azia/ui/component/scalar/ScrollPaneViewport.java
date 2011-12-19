/*
 * Copyright (c) 2011 HawkinsSoftware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Byron Hawkins of HawkinsSoftware
 */
package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ViewportStateChangeHandler;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * A simple scroll pane viewport having offset coordinates for the position of the pane content.
 * 
 * 
 * @author Byron Hawkins
 * 
 * @JTourBusStop 1, Declaring and respecting usage of a shared feature, Introducing the ScrollPaneViewport:
 * 
 *               The most fundamental behavior of a scroll pane is that it consists of a frame having some particular
 *               size, and has a virtual pane inside the frame having a potentially larger size. Some fraction of the
 *               virtual pane appears within the outer frame, and the particular region of the virtual pane which is
 *               visible is determined by the offset position of the viewport. Fields xViewport and yViewport represent
 *               that offset, which is typically referred to as the "viewport position".
 */
@ValidateRead
@ValidateWrite
public abstract class ScrollPaneViewport extends AbstractComponent
{
	/**
	 * Marker interface for the painter of a scrollpane viewport
	 */
	public interface Painter
	{
	}

	// these state fields can be delegated to a data handler
	protected int xViewport;
	protected int yViewport;

	private final ViewportStateChangeHandler stateHandler = new ViewportStateChangeHandler(this);

	@InvocationConstraint
	public ScrollPaneViewport()
	{
		installHandler(stateHandler);
	}

	@InvocationConstraint(domains = { DisplayBoundsDomain.class, RenderingDomain.class })
	public int xViewport()
	{
		return xViewport;
	}

	@InvocationConstraint(domains = { DisplayBoundsDomain.class, RenderingDomain.class })
	public int yViewport()
	{
		return yViewport;
	}

	/**
	 * @JTourBusStop 2, Declaring and respecting usage of a shared feature, Moving the viewport position:
	 * 
	 *               It would appear at first glance that moving the position of the viewport within the scroll pane is
	 *               as simple as invoking this method with new coordinates. But there are several details to consider.
	 * 
	 *               1. It is almost always expected that the scroll pane should be fully occupied with viewport
	 *               content, not having any blank areas. So if the scroll pane is 50x50 in size, and the viewport
	 *               content is 100x100, then the allowable range of positions is (-50, -50) to (0, 0). Callers to this
	 *               method are responsible for maintaining these limits.
	 * 
	 *               2. Most scroll panes have scroll bars, which visually indicate the bounds and current position of
	 *               the viewport, and allow the user to move the viewport position with the mouse. When the viewport
	 *               position changes, it is expected that the scrollbar knobs will also change position accordingly.
	 * 
	 *               3. When the size of the scroll pane or its internal viewport changes, it is expected that the size
	 *               and position of the scrollbar knobs will change accordingly.
	 * 
	 *               These behaviors can be encapsulated in a controller, but it will be subject to severe limitations
	 *               because it must make assumptions about usage, and those assumptions can never be universal enough.
	 *               A normalized implementation--having all individual operations available and isolated--must take the
	 *               form of a scattered aspect, relying on consumers to use it properly.
	 * 
	 * @JTourBusStop 8, Declaring and respecting usage of a shared feature, Conclusion:
	 * 
	 *               Moving the viewport origin is optimally implemented as an aspect-oriented feature synthesized
	 *               across many classes. This configuration exposes its internal mechanics, requiring consumers to
	 *               interact with the viewport position according to policy, lest they contradict its synthesis and
	 *               disorient the scroll pane. 
	 */
	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public void setViewportPosition(int x, int y)
	{
		xViewport = x;
		yViewport = y;
	}
}
