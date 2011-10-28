package org.hawkinssoftware.azia.ui.paint.canvas;

import java.awt.Rectangle;

import org.hawkinssoftware.rns.core.moa.ExecutionPath;

@ExecutionPath.NoFrame
public class Size
{
	public static final Size EMPTY = new Size(0, 0);

	public final int width;
	public final int height;

	public Size(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void applyTo(Rectangle r)
	{
		r.width = width;
		r.height = height;
	}

	@Override
	public String toString()
	{
		return "[" + width + "x" + height + "]";
	}
}
