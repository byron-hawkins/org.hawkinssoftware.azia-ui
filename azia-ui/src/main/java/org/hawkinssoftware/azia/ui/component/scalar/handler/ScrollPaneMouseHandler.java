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
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPassTermination;
import org.hawkinssoftware.azia.ui.input.MouseAware.Forward;
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
@DomainRole.Join(membership = ScrollPaneDomain.class)
public class ScrollPaneMouseHandler implements MouseAware.MouseHandler
{
	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private ScrollPaneComposite<?> host;

	public ScrollPaneMouseHandler(ScrollPaneComposite<?> host)
	{
		this.host = host;
	}

	@Override
	public void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (host.getHorizontalScrollbar().getBounds().contains(pass.event()))
		{
			transaction.contribute(new Forward(host.getHorizontalScrollbar().getComponent()));
		}
		else if (host.getVerticalScrollbar().getBounds().contains(pass.event()))
		{
			transaction.contribute(new Forward((host.getVerticalScrollbar().getComponent())));
		}
		else
		{
			transaction.contribute(new Forward(host.getViewport().getComponent()));
		}
	}

	@Override
	public void mouseStateTerminated(EventPassTermination termination, PendingTransaction transaction)
	{
	}
}
