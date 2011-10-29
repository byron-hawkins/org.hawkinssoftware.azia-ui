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
package org.hawkinssoftware.azia.ui.component;

import java.awt.Rectangle;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.ScreenPosition;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class EnclosureBounds implements Axis.Bounds
{
	public static final EnclosureBounds EMPTY = new EnclosureBounds(0, 0, 0, 0);

	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public EnclosureBounds(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public EnclosureBounds(MutableBounds bounds)
	{
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}

	public EnclosureBounds(Rectangle rectangle)
	{
		this.x = rectangle.x;
		this.y = rectangle.y;
		this.width = rectangle.width;
		this.height = rectangle.height;
	}

	@Override
	public int getExtent(Axis axis)
	{
		switch (axis)
		{
			case H:
				return x + width;
			case V:
				return y + height;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public int getPosition(Axis axis)
	{
		switch (axis)
		{
			case H:
				return x;
			case V:
				return y;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public int getSpan(Axis axis)
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

	public boolean isAtOrigin()
	{
		return (x == 0) && (y == 0);
	}

	public boolean intersects(Rectangle rectangle)
	{
		return rectangle.intersects(x, y, width, height);
	}

	public boolean contains(ScreenPosition position)
	{
		return ((position.x() > x) && (position.y() > y) && (position.x() < (x + width)) && (position.y() < (y + height)));
	}

	@Override
	public String toString()
	{
		return "[x: " + x + " -> " + (x + width) + " (" + width + "), y: " + y + " -> " + (y + height) + " (" + height + ")]";
	}
}
