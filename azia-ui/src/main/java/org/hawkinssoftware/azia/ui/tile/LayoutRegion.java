package org.hawkinssoftware.azia.ui.tile;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@VisibilityConstraint(domains = LayoutRegion.TileLayoutDomain.class, inherit = true)
@DomainRole.Join(membership = LayoutRegion.TileLayoutDomain.class)
public interface LayoutRegion extends MouseAware, BoundedEntity.PanelRegion
{
	public static class TileLayoutDomain extends DisplayBoundsDomain
	{
		@DomainRole.Instance
		public static final TileLayoutDomain INSTANCE = new TileLayoutDomain();
	}
	
	EnclosureBounds getBounds();
}
