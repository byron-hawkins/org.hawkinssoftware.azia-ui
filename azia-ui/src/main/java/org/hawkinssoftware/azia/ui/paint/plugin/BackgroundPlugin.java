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

import java.awt.Color;

import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;

/**
 * DOC comment task awaits.
 * 
 * @param <ContentType>
 *            the generic type
 * @author Byron Hawkins
 */
public class BackgroundPlugin<ContentType>
{
	protected void paint(Canvas c, ContentType content)
	{
	}

	public final void paint(ContentType content)
	{
		paint(Canvas.get(), content);
	}
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <ContentType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Solid<ContentType> extends BackgroundPlugin<ContentType>
	{
		public final Color color;

		public Solid(Color color)
		{
			this.color = color;
		}

		protected void paint(Canvas c, ContentType content)
		{
			c.pushColor(color);
			c.g.fillRect(0, 0, c.span().width, c.span().height);
		}
	}
}
