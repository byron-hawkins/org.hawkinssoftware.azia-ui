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

import org.hawkinssoftware.azia.ui.tile.LayoutRegion;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * A simple DTO representing the bounds of a layout change that is in progress.
 * 
 * @author Byron Hawkins
 * 
 * @JTourBusStop 4, Defining the TileLayoutDomain and its scope, LayoutResizeEvent joins the TileLayoutDomain:
 * 
 *               Every class participating in the layout of tiles will join the TileLayoutDomain, even if it has a
 *               transitory and/or tangential role.
 */
@DomainRole.Join(membership = TileLayoutDomain.class)
public class LayoutResizeEvent
{
	private final LayoutRegion resizedBounds;

	public final int width;
	public final int height;

	public LayoutResizeEvent(LayoutRegion resizedBounds, int width, int height)
	{
		this.resizedBounds = resizedBounds;
		this.width = width;
		this.height = height;
	}

	public LayoutRegion getResizedBounds()
	{
		return resizedBounds;
	}
}
