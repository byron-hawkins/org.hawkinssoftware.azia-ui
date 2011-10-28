package org.hawkinssoftware.azia.ui.paint;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;

public interface RegionPainter<RegionType extends BoundedEntity.PanelRegion>
{
	RegionType getRegion();
	
	void setRegion(RegionType region);
	
	void paint(RegionType region);
}
