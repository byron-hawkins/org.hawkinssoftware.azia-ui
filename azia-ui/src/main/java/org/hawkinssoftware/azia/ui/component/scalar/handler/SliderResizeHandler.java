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
@DomainRole.Join(membership = { SliderCompositeDomain.class, DisplayBoundsDomain.class })
public class SliderResizeHandler implements UserInterfaceHandler
{
	private final SliderComposite<? extends AbstractSlider> host;

	public SliderResizeHandler(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;
	}

	public void resize(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction)
	{
		if (resize.hasPosition(host.getAxis().opposite()))
		{
			transaction.contribute(ComponentBoundsChangeDirective.changePosition(host.getKnob().getComponent(), host.getAxis().opposite(),
					resize.getPosition(host.getAxis().opposite())));
			transaction.contribute(new ComponentBoundsChangeDirective(host.getTrack().getComponent(), resize.getBoundsChange()));
		}
		if (resize.hasSpan(host.getAxis().opposite()))
		{
			transaction.contribute(ComponentBoundsChangeDirective.changeSpan(host.getKnob().getComponent(), host.getAxis().opposite(),
					resize.getSpan(host.getAxis().opposite())));
			transaction.contribute(new ComponentBoundsChangeDirective(host.getTrack().getComponent(), resize.getBoundsChange()));
		}
	}
}
