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
package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = TileLayoutDomain.class)
public class TileBoundsChangeDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Notification extends UserInterfaceNotification
	{
		public EnclosureBounds getBounds()
		{
			return bounds;
		}

		public EnclosureEncountered createEnclosureEncounter(ComponentEnclosure<?, ?> component)
		{
			return new EnclosureEncountered(component);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class EnclosureEncountered extends Notification
	{
		public final ComponentEnclosure<?, ?> enclosure;

		public EnclosureEncountered(ComponentEnclosure<?, ?> component)
		{
			this.enclosure = component;
		}
	}

	public final EnclosureBounds bounds;

	public TileBoundsChangeDirective(LayoutRegion region)
	{
		super(region);
		
		bounds = region.getBounds();
	}
	
	public TileBoundsChangeDirective(LayoutRegion region, EnclosureBounds bounds)
	{
		super(region);

		this.bounds = bounds;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
