package org.hawkinssoftware.azia.ui.paint.canvas;

import org.hawkinssoftware.azia.ui.component.EnclosureBounds;

public class Inset
{
	public static Inset homogenous(int span)
	{
		return new Inset(span, span, span, span);
	}

	public static Inset symmetrical(int leftRight, int topBottom)
	{
		return new Inset(leftRight, topBottom, leftRight, topBottom);
	}

	public static final Inset EMPTY = new Inset(0, 0, 0, 0);

	public final int left;
	public final int top;
	public final int right;
	public final int bottom;

	public Inset(int left, int top, int right, int bottom)
	{
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public EnclosureBounds getContainedBounds(Size size)
	{
		return new EnclosureBounds(left, top, size.width - (left + right), size.height - (top + bottom));
	}
}
