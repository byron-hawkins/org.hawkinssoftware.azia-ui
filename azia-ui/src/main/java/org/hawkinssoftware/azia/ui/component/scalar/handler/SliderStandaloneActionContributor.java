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
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite.SliderCompositeDomain;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideSubregionSpanNotification;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideToPositionNotification;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { SliderCompositeDomain.class, DisplayBoundsDomain.class })
public class SliderStandaloneActionContributor implements UserInterfaceHandler
{
	private final SliderComposite<? extends AbstractSlider> host;
	private final RepaintInstanceDirective repaint;

	public SliderStandaloneActionContributor(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;
		repaint = new RepaintInstanceDirective(host.getComponent());
	}

	public void slideSubregionSpan(SlideSubregionSpanNotification notification, PendingTransaction transaction)
	{
		switch (notification.direction)
		{
			case DOWN:
			{
				int newKnobPosition = Math.max(0, host.getKnob().getBounds().getPosition(host.getAxis()) - host.getKnob().getBounds().getSpan(host.getAxis()));
				transaction.contribute(host.moveKnob(newKnobPosition));
				RepaintRequestManager.requestRepaint(repaint);
				break;
			}
			case UP:
			{
				int newKnobPosition = Math.min(host.getKnob().getBounds().getPosition(host.getAxis()) + host.getKnob().getBounds().getSpan(host.getAxis()),
						host.getMaxKnobPosition());
				transaction.contribute(host.moveKnob(newKnobPosition));
				RepaintRequestManager.requestRepaint(repaint);
			}
		}
	}

	public void slideToPosition(SlideToPositionNotification notification, PendingTransaction transaction)
	{
		transaction.contribute(host.moveKnob(notification.pixelPosition));
	}

	public void resizePosted(ComponentBoundsChangeDirective.Notification change, PendingTransaction transaction)
	{
		if (change.hasPosition(host.getAxis()))
		{
			int knobDelta = host.getKnob().getBounds().getPosition(host.getAxis()) - host.getBounds().getPosition(host.getAxis());
			int newKnobPosition = change.getPosition(host.getAxis()) + knobDelta;
			ComponentBoundsChangeDirective knobBoundsChange = ComponentBoundsChangeDirective.changePosition(host.getKnob().getComponent(), host.getAxis(),
					newKnobPosition);
			transaction.contribute(knobBoundsChange);
		}
	}
}