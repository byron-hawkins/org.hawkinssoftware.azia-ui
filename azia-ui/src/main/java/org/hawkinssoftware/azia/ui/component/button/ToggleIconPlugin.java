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
package org.hawkinssoftware.azia.ui.component.button;

import java.awt.Image;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelContentPlugin;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ToggleIconPlugin extends LabelContentPlugin
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class CenteredImage
	{
		final Image image;
		final int x;
		final int y;
		final int width;
		final int height;

		public CenteredImage(Image image)
		{
			this.image = image;
			width = image.getWidth(null);
			height = image.getHeight(null);

			if (width < ToggleIconPlugin.this.width)
			{
				x = ((ToggleIconPlugin.this.width - width) / 2);
			}
			else
			{
				x = 0;
			}
			
			if (height < ToggleIconPlugin.this.height)
			{
				y = ((ToggleIconPlugin.this.height - height) / 2);
			}
			else
			{
				y = 0;
			}
		}

		void paint(Canvas c)
		{
			c.g.drawImage(image, x, y, width, height, null);
		}
	}

	private final int width;
	private final int height;
	private final CenteredImage upIcon;
	private final CenteredImage downIcon;

	public ToggleIconPlugin(int width, int height, Image upIcon, Image downIcon)
	{
		this.width = width;
		this.height = height;
		this.upIcon = new CenteredImage(upIcon);
		this.downIcon = new CenteredImage(downIcon);
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		switch (axis)
		{
			case H:
				return width;
			case V:
				return height;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public void paint(VirtualComponent component)
	{
		Canvas c = Canvas.get();
		if (component.getDataHandler(PushedStateHandler.KEY).isPushed())
		{
			downIcon.paint(c);
		}
		else
		{
			upIcon.paint(c);
		}
	}
}
