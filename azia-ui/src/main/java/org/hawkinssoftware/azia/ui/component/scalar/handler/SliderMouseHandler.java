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
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite.SliderCompositeDomain;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideSubregionSpanNotification;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPassTermination;
import org.hawkinssoftware.azia.ui.input.MouseAware.Forward;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = SliderCompositeDomain.class)
public class SliderMouseHandler implements MouseAware.MouseHandler
{
	private final SliderComposite<? extends AbstractSlider> host;

	private final SlideSubregionSpanNotification downSubboundsAction;
	private final SlideSubregionSpanNotification upSubboundsAction;

	public SliderMouseHandler(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;

		downSubboundsAction = new SlideSubregionSpanNotification(host.getComponent(), AbstractSlider.Direction.DOWN);
		upSubboundsAction = new SlideSubregionSpanNotification(host.getComponent(), AbstractSlider.Direction.UP);
	}

	@Override
	public void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (host.contactsKnob(pass.event()))
		{
			transaction.contribute(new Forward(host.getKnob().getComponent()));
		}
		else
		{
			transaction.contribute(new Forward(host.getTrack().getComponent()));

			if (pass.event().changes().contains(MouseInputEvent.Change.LEFT_BUTTON) && pass.event().buttonsDown().contains(MouseInputEvent.Button.LEFT))
			{
				if (host.getAxis().extractPosition(pass.event()) <= host.getKnob().getBounds().getPosition(host.getAxis()))
				{
					transaction.contribute(downSubboundsAction);
				}
				else
				{
					transaction.contribute(upSubboundsAction);
				}
			}
		}
	}

	@Override
	public void mouseStateTerminated(EventPassTermination termination, PendingTransaction transaction)
	{
	}
}
