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
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ScrollPaneDomain.class, RenderingDomain.class })
public class ScrollPanePaintHandler implements AbstractComposite.PaintHandler
{
	private final ScrollPaneComposite<?> host;

	public ScrollPanePaintHandler(ScrollPaneComposite<?> host)
	{
		this.host = host;
	}

	@Override
	public void paint(PaintIncludeNotification notification, PendingTransaction transaction)
	{
		if (host.getHorizontalScrollbar().isVisible())
		{
			transaction.contribute(new PaintIncludeNotification(host.getHorizontalScrollbar()));
		}
		if (host.getVerticalScrollbar().isVisible())
		{
			transaction.contribute(new PaintIncludeNotification(host.getVerticalScrollbar()));
		}
		transaction.contribute(new PaintIncludeNotification(host.getViewport()));
	}
}
