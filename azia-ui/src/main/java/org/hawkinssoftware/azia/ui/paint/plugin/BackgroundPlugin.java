package org.hawkinssoftware.azia.ui.paint.plugin;

import java.awt.Color;

import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;

public class BackgroundPlugin<ContentType>
{
	protected void paint(Canvas c, ContentType content)
	{
	}

	public final void paint(ContentType content)
	{
		paint(Canvas.get(), content);
	}
	
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
