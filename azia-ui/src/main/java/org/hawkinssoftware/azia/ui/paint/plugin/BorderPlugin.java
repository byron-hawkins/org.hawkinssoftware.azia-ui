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

import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;

/**
 * DOC comment task awaits.
 * 
 * @param <ContentType>
 *            the generic type
 * @author Byron Hawkins
 */
public abstract class BorderPlugin<ContentType>
{
	protected Inset inset;

	protected BorderPlugin(Inset inset)
	{
		this.inset = inset;
	}

	public void paintBorder(ContentType component)
	{
	}

	public Inset getInset()
	{
		return inset;
	}

	public void paintAndNarrow(Canvas c, ContentType component)
	{
		// don't push a canvas frame here, b/c it would be popped on the way back to the caller, losing the "narrow"

		paintBorder(component);
		c.pushBounds(inset.getContainedBounds(c.size()));
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <ContentType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Empty<ContentType extends AbstractComponent> extends BorderPlugin<ContentType>
	{
		public Empty(Inset inset)
		{
			super(inset);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <ContentType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Solid<ContentType> extends BorderPlugin<ContentType>
	{
		public static void paintBorder(Canvas c, Inset inset)
		{
			c.g.fillRect(0, 0, c.size().width, inset.top);
			c.g.fillRect(c.size().width - inset.right, 0, inset.right, c.size().height);
			c.g.fillRect(0, 0, inset.left, c.size().height);
			c.g.fillRect(0, c.size().height - inset.bottom, c.size().width, inset.bottom);
		}
		
		public static final Inset HAIRLINE = new Inset(1, 1, 1, 1);

		private final Color color;

		public Solid(Color color)
		{
			this(color, HAIRLINE);
		}

		public Solid(Color color, Inset inset)
		{
			super(inset);

			this.color = color;
		}

		@Override
		public void paintBorder(ContentType component)
		{
			Canvas c = Canvas.get();

			c.pushColor(color);
			paintBorder(c, inset);
		}
	}
}
