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
package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider.Direction;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite.ScrollPaneViewportDomain;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideSubregionSpanNotification;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideToPositionNotification;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ScrollPaneDomain.class, ScrollPaneViewportDomain.class, DisplayBoundsDomain.class })
public class ScrollPaneScrollbarContributor implements UserInterfaceHandler
{
	private final ScrollPaneComposite<?> host;
	private final Axis axis;

	public ScrollPaneScrollbarContributor(ScrollPaneComposite<?> host, Axis axis)
	{
		this.host = host;
		this.axis = axis;
	}

	/**
	 * @JTourBusStop 6, Declaring and respecting usage of a shared feature, Processing a click in the scrollbar track:
	 * 
	 *               This notification is sent by the scrollbar when the user clicks in the scrollbar track, indicating
	 *               that the scrollbar should move a full span in the click direction (or as far as the track bounds
	 *               permit). It might seem most natural to move the scrollbar directly, and adjust the viewport
	 *               position in response, since the distance to move is already known to be the span of the
	 *               scrollbar knob. But this would violate policy, so instead the shift is translated into the
	 *               dimensions of the viewport and forwarded to it as a typical MoveViewportOriginDirective.
	 */
	public void slideSubregionSpan(SlideSubregionSpanNotification slideSubbounds, PendingTransaction transaction)
	{
		int x = host.getViewport().getComponent().xViewport();
		int y = host.getViewport().getComponent().yViewport();
		int delta = host.getViewport().getBounds().getSpan(axis);
		if (slideSubbounds.direction == Direction.DOWN)
		{
			delta = -delta;
		}
		int maxPosition = host.getViewport().getPainter().getScrollableContentSize(axis) - host.getViewport().getBounds().getSpan(axis);
		switch (axis)
		{
			case H:
				x += delta;
				x = Math.max(Math.min(x, maxPosition), 0);
				break;
			case V:
				y += delta;
				y = Math.max(Math.min(y, maxPosition), 0);
				break;
		}
		MoveViewportOriginDirective moveOrigin = new MoveViewportOriginDirective(host.getViewport().getComponent(), x, y);
		transaction.contribute(moveOrigin);
	}

	/**
	 * @JTourBusStop 4, Declaring and respecting usage of a shared feature, Changing the viewport position according to
	 *               policy:
	 * 
	 *               This scroll pane handler observes the SlideToPositionNotification and translates it into a
	 *               MoveViewportOriginDirective. It does not directly move the scrollbars.
	 */
	public void positionSliding(SlideToPositionNotification slideToPosition, PendingTransaction transaction)
	{
		double percent = slideToPosition.pixelPosition / (double) host.getViewport().getBounds().getSpan(axis);
		int x = host.getViewport().getComponent().xViewport();
		int y = host.getViewport().getComponent().yViewport();
		switch (axis)
		{
			case H:
				x = (int) (percent * host.getViewport().getPainter().getScrollableContentSize(axis));
				break;
			case V:
				y = (int) (percent * host.getViewport().getPainter().getScrollableContentSize(axis));
				break;
		}
		MoveViewportOriginDirective moveOrigin = new MoveViewportOriginDirective(host.getViewport().getComponent(), x, y);
		transaction.contribute(moveOrigin);
	}
}
