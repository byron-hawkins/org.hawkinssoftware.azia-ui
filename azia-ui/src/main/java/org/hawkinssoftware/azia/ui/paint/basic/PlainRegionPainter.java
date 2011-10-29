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
package org.hawkinssoftware.azia.ui.paint.basic;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.paint.RegionPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugins;

/**
 * DOC comment task awaits.
 * 
 * @param <RegionType>
 *            the generic type
 * @author Byron Hawkins
 */
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
