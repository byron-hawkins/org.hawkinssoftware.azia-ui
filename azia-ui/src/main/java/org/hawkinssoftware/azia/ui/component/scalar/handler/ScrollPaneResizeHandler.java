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

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite.ScrollPaneViewportDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SetVisibleDirective;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = { TransactionParticipant.class, ScrollPaneDomain.class, ScrollPaneViewportDomain.class, DisplayBoundsDomain.class })
public class ScrollPaneResizeHandler implements AbstractComposite.ResizeHandler
{
	private final ScrollPaneComposite<?> host;

	public ScrollPaneResizeHandler(ScrollPaneComposite<?> host)
	{
		this.host = host;
	}

	@Override
	public void resize(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction)
	{
		SliderComposite<ScrollSlider> horizontalScrollbar = host.getHorizontalScrollbar();
		SliderComposite<ScrollSlider> verticalScrollbar = host.getVerticalScrollbar();
		ScrollPaneViewportComposite<?, ?> viewport = host.getViewport();

		/**
		 * @JTourBusStop 3, Declaring and respecting usage of a shared class fragment, ScrollPaneResizeHandler.resize()
		 *               queries ComponentEnclosure.getBounds():
		 * 
		 *               This local variable "bounds" is assigned the bounds of the scroll pane in which this handler is
		 *               installed.
		 */
		EnclosureBounds bounds = host.getBounds();

		int hbarWidth = verticalScrollbar.getPackedSize(Axis.H);
		int vbarWidth = horizontalScrollbar.getPackedSize(Axis.V);

		int contentWidth = viewport.getPainter().getScrollableContentSize(Axis.H);
		int contentHeight = viewport.getPainter().getScrollableContentSize(Axis.V);

		/**
		 * @JTourBusStop 3.1, Declaring and respecting usage of a shared class fragment,
		 *               ScrollPaneResizeHandler.resize() queries ComponentEnclosure.getBounds():
		 * 
		 *               Local variable "bounds" is processed according to some rules into 4 separate local variables.
		 *               An irresponsible implementation could use the original ComponentEnclosure (class field "host")
		 *               to store these processed values temporarily. Of course that would be ridiculous, but the point
		 *               of suggesting it is to demonstrate how the declared usage of ComponentEnclosure.getBounds() is
		 *               honored by this consumer.
		 */
		int xTransaction = (resize.getBoundsChange().x == null) ? bounds.x : resize.getBoundsChange().x;
		int yTransaction = (resize.getBoundsChange().y == null) ? bounds.y : resize.getBoundsChange().y;
		int wTransaction = (resize.getBoundsChange().width == null) ? bounds.width : resize.getBoundsChange().width;
		int hTransaction = (resize.getBoundsChange().height == null) ? bounds.height : resize.getBoundsChange().height;

		boolean vVisible = false;
		boolean hVisible = false;
		if ((contentWidth <= wTransaction) && (contentHeight <= hTransaction))
		{
		}
		else if (wTransaction <= contentWidth)
		{
			hVisible = true;
			vVisible = (contentHeight > (hTransaction - hbarWidth));
		}
		else if (hTransaction <= contentHeight)
		{
			vVisible = true;
			hVisible = (contentWidth > (wTransaction - vbarWidth));
		}
		else
		{
			hVisible = true;
			vVisible = true;
		}

		if (hVisible)
		{
			int width = wTransaction - (vVisible ? vbarWidth : 0);
			transaction.contribute(new ComponentBoundsChangeDirective(horizontalScrollbar.getComponent(), resize.getBoundsChange().x, yTransaction
					+ hTransaction - hbarWidth, width, null));
		}
		if (vVisible)
		{
			int height = hTransaction - (hVisible ? hbarWidth : 0);
			transaction.contribute(new ComponentBoundsChangeDirective(verticalScrollbar.getComponent(), xTransaction + wTransaction - vbarWidth, resize
					.getBoundsChange().y, null, height));
		}

		/**
		 * @JTourBusStop 3.2, Declaring and respecting usage of a shared class fragment,
		 *               ScrollPaneResizeHandler.resize() queries ComponentEnclosure.getBounds():
		 * 
		 *               This block assigns new bounds to a ComponentEnclosure, in this case the scrollpane viewport
		 *               (using the Azia transaction semantics, which will not be explained here). This assignment does
		 *               respect the usage expected by the ComponentEnclosure, because the assigned bounds carry no
		 *               details of this handler's internal calculation, but simply represent the new size for the
		 *               viewport.
		 */
		int viewportWidth = wTransaction - (vVisible ? vbarWidth : 0);
		int viewportHeight = hTransaction - (hVisible ? hbarWidth : 0);
		EnclosureBounds viewportbounds = new EnclosureBounds(xTransaction, yTransaction, viewportWidth, viewportHeight);
		transaction.contribute(new ComponentBoundsChangeDirective(viewport.getComponent(), viewportbounds));

		boolean needsToScroll = false;
		int yScroll = host.getViewport().getComponent().yViewport();
		int vContentEndPosition = contentHeight - yScroll;
		if (viewportHeight > vContentEndPosition)
		{
			int yNew = Math.max(0, contentHeight - viewportHeight);
			if (yNew != yScroll)
			{
				yScroll = yNew;
				needsToScroll = true;
			}
		}

		int xScroll = host.getViewport().getComponent().xViewport();
		int hContentEndPosition = contentWidth - xScroll;
		if (viewportWidth > hContentEndPosition)
		{
			int xNew = Math.max(0, contentWidth - viewportWidth);
			if (xNew != xScroll)
			{
				xScroll = xNew;
				needsToScroll = true;
			}
		}

		transaction.contribute(new SetVisibleDirective(horizontalScrollbar, hVisible));
		transaction.contribute(new SetVisibleDirective(verticalScrollbar, vVisible));
		if (needsToScroll)
		{
			MoveViewportOriginDirective scrollAlong = new MoveViewportOriginDirective(host.getViewport().getComponent(), xScroll, yScroll);
			transaction.contribute(scrollAlong);
		}
	}

	@Override
	public void apply(UserInterfaceDirective action)
	{
	}
}
