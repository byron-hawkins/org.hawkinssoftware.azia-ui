package org.hawkinssoftware.azia.ui.paint.basic;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.paint.RegionPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugins;

public class PlainRegionPainter<RegionType extends BoundedEntity.PanelRegion> implements RegionPainter<RegionType>
{
	private BackgroundPlugin<RegionType> background = new BackgroundPlugin.Solid<RegionType>(new Color(0xDDDDDD));
	public BorderPlugins<RegionType> borderPlugins = new BorderPlugins<RegionType>();
	
	private RegionType region;

	@Override
	public RegionType getRegion()
	{
		return region;
	}

	@Override
	public void setRegion(RegionType region)
	{
		this.region = region;
	}

	@Override
	public void paint(RegionType region)
	{
		Canvas c = Canvas.get();
		
		background.paint(region);
		borderPlugins.paintAndNarrow(c, region);
	}
	
	public void setBackground(BackgroundPlugin<RegionType> background)
	{
		this.background = background;
	}
}
