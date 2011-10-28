package org.hawkinssoftware.azia.ui.paint.transaction.paint;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.PanelRegion;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;

public class PaintRegionNotification extends UserInterfaceNotification
{
	final BoundedEntity.PanelRegion region;
	final EnclosureBounds bounds;

	public PaintRegionNotification(PanelRegion region, EnclosureBounds bounds)
	{
		this.region = region;
		this.bounds = bounds;
	}
}
