package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import org.hawkinssoftware.azia.ui.tile.LayoutRegion;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

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
