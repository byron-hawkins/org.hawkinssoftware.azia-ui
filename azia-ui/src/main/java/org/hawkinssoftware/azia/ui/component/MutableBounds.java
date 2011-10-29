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
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = DisplayBoundsDomain.class)
public class MutableBounds implements Axis.Bounds
{
	public int x;
	public int y;
	public int width;
	public int height;

	public MutableBounds()
	{
	}

	public MutableBounds(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public MutableBounds(Rectangle rectangle)
	{
		this.x = rectangle.x;
		this.y = rectangle.y;
		this.width = rectangle.width;
		this.height = rectangle.height;
	}

	public MutableBounds(EnclosureBounds bounds)
	{
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}

	public void setPosition(int value, Axis axis)
	{
		switch (axis)
		{
			case H:
				x = value;
				break;
			case V:
				y = value;
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public void setSpan(int value, Axis axis)
	{
		switch (axis)
		{
			case H:
				width = value;
				break;
			case V:
				height = value;
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public void setBounds(MutableBounds other)
	{
		x = other.x;
		y = other.y;
		width = other.width;
		height = other.height;
	}

	public boolean contains(ScreenPosition position)
	{
		return ((position.x() > x) && (position.y() > y) && (position.x() < (x + width)) && (position.y() < (y + height)));
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
				return width - x;
			case V:
				return height - y;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public String toString()
	{
		return "[x: " + x + " -> " + (x + width) + " (" + width + "), y: " + y + " -> " + (y + height) + " (" + height + ")]";
	}
}
