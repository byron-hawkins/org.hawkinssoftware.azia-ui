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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite.ScrollPaneDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = ScrollPaneDomain.class)
public class ScrollPaneSizeDelegate implements BoundedEntity 
{
	private final ScrollPaneComposite<?> host;

	public ScrollPaneSizeDelegate(ScrollPaneComposite<?> host)
	{
		this.host = host;
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return host.getViewport().getPackedSize(axis);
	}

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		BoundedEntity.MaximumSize max = host.getViewport().getMaxSize(axis);
		if (max.exists())
		{
			max.setValue(max.getValue() + host.getScrollbar(axis.opposite()).getPackedSize(axis));
		}
		return max;
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return host.getComponent().getExpansion(axis);
	}
}
