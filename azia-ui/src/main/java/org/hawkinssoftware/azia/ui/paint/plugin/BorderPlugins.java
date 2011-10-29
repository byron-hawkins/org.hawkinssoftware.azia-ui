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
package org.hawkinssoftware.azia.ui.paint.plugin;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @param <ContentType>
 *            the generic type
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class BorderPlugins<ContentType>
{
	private final List<BorderPlugin<ContentType>> plugins = new ArrayList<BorderPlugin<ContentType>>();

	public synchronized void insertPlugin(BorderPlugin<ContentType> plugin)
	{
		plugins.add(plugin);
	}

	public synchronized void insertPlugin(int index, BorderPlugin<ContentType> plugin)
	{
		plugins.add(index, plugin);
	}
	
	public void clearPlugins()
	{
		plugins.clear();
	}

	public int getPluginCount()
	{
		return plugins.size();
	}
	
	public synchronized void narrow(Canvas c)
	{
		for (BorderPlugin<ContentType> plugin : plugins)
		{
			c.pushBounds(plugin.getInset().getContainedBounds(c.size()));
		}
	}

	public synchronized int getCumulativeInset(Axis axis)
	{
		int inset = 0;
		switch (axis)
		{
			case H:
				for (BorderPlugin<ContentType> plugin : plugins)
				{
					inset += plugin.inset.left + plugin.inset.right;
				}
				break;
			case V:
				for (BorderPlugin<ContentType> plugin : plugins)
				{
					inset += plugin.inset.top + plugin.inset.bottom;
				}
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
		return inset;
	}

	public synchronized void paintAndNarrow(Canvas c, ContentType component)
	{
		for (BorderPlugin<ContentType> plugin : plugins)
		{
			plugin.paintAndNarrow(c, component);
		}
	}
}
