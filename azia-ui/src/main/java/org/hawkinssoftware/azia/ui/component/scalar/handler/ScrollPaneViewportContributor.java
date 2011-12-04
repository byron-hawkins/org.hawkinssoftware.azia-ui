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

import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite.ScrollPaneViewportDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeKnobPositionNotification;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeKnobSpanDirective;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeViewportContentBoundsDirective;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ScrollPaneDomain.class, ScrollPaneViewportDomain.class, DisplayBoundsDomain.class })
public class ScrollPaneViewportContributor implements UserInterfaceHandler
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { TransactionParticipant.class, ScrollPaneDomain.class, ScrollPaneViewportDomain.class, DisplayBoundsDomain.class })
	private class OrientedMetrics
	{
		Axis axis;
		int contentSpan;
		int viewportSpan;
		int viewportContentPosition;

		void assignCurrentValues(Axis axis)
		{
			this.axis = axis;
			contentSpan = host.getViewport().getPainter().getScrollableContentSize(axis);
			viewportSpan = host.getViewport().getBounds().getSpan(axis);
			viewportContentPosition = (axis == Axis.H) ? host.getViewport().getComponent().xViewport() : host.getViewport().getComponent().yViewport();
		}

		private void reboundKnob(PendingTransaction transaction)
		{
			double visibilityRatio = viewportSpan / (double) contentSpan;
			// FIXME: scrollbarSpan is dependent on opposite bar visibility

			SliderComposite<ScrollSlider> slider = host.getScrollbar(axis);
			if (!slider.isVisible())
			{
				return;
			}
			int scrollbarSpan = slider.getBounds().getSpan(axis);

			int knobSpan = (int) (scrollbarSpan * visibilityRatio);
			transaction.contribute(new ChangeKnobSpanDirective(host.getScrollbar(axis).getComponent(), knobSpan));

			int knobRange = scrollbarSpan - knobSpan;
			double positionRatio = viewportContentPosition / (double) (contentSpan - viewportSpan);
			int knobPosition = (int) (knobRange * positionRatio);
			knobPosition = Math.max(Math.min(knobPosition, scrollbarSpan - knobSpan), 0);
			transaction.contribute(new ChangeKnobPositionNotification(host.getScrollbar(axis).getComponent(), knobPosition));

			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(host.getScrollbar(axis).getComponent()));
		}
	}

	private final ScrollPaneComposite<?> host;

	private final OrientedMetrics orientedMetrics = new OrientedMetrics();

	public ScrollPaneViewportContributor(ScrollPaneComposite<?> host)
	{
		this.host = host;
	}

	public void viewportMoving(MoveViewportOriginDirective.Notification move, PendingTransaction transaction)
	{
		UserInterfaceTransactionQuery.setReadTransactionalChanges(true);
		
		orientedMetrics.assignCurrentValues(Axis.H);
		orientedMetrics.viewportContentPosition = move.x();
		orientedMetrics.reboundKnob(transaction);
		orientedMetrics.assignCurrentValues(Axis.V);
		orientedMetrics.viewportContentPosition = move.y();
		orientedMetrics.reboundKnob(transaction);
	}

	public void viewportResizing(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction)
	{
		UserInterfaceTransactionQuery.setReadTransactionalChanges(true);
		
		orientedMetrics.assignCurrentValues(Axis.H);
		orientedMetrics.viewportSpan = (resize.getBoundsChange().width == null) ? host.getBounds().width : resize.getBoundsChange().width;
		orientedMetrics.reboundKnob(transaction);
		orientedMetrics.assignCurrentValues(Axis.V);
		orientedMetrics.viewportSpan = (resize.getBoundsChange().width == null) ? host.getBounds().height : resize.getBoundsChange().height;
		orientedMetrics.reboundKnob(transaction);
	}

	public void viewportContentResizing(ChangeViewportContentBoundsDirective.Notification resize, PendingTransaction transaction)
	{
		UserInterfaceTransactionQuery.setReadTransactionalChanges(true);
		
		orientedMetrics.assignCurrentValues(Axis.H);
		orientedMetrics.contentSpan = resize.getBounds().width;
		orientedMetrics.reboundKnob(transaction);
		orientedMetrics.assignCurrentValues(Axis.V);
		orientedMetrics.contentSpan = resize.getBounds().height;
		orientedMetrics.reboundKnob(transaction);
	}
}
