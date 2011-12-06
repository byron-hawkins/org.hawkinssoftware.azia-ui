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

import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.ScreenPosition;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;
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
	
	private int getCurrentPosition(Axis axis)
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
	public int getPosition(Axis axis)
	{
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(PositionProperty.oriented(axis)).getValue();
	}
	
	private int getCurrentSpan(Axis axis)
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
	public int getSpan(Axis axis)
	{
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(SpanProperty.oriented(axis)).getValue();
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

	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	private static class SpanProperty extends UserInterfaceTransactionQuery.Property<EnclosureBounds, Integer>
	{
		private static SpanProperty oriented(Axis axis)
		{
			switch (axis)
			{
				case H:
					return HORIZONTAL_INSTANCE;
				case V:
					return VERTICAL_INSTANCE;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		private static final SpanProperty HORIZONTAL_INSTANCE = new SpanProperty(Axis.H);
		private static final SpanProperty VERTICAL_INSTANCE = new SpanProperty(Axis.V);

		private final Axis axis;

		SpanProperty(Axis axis)
		{
			super("getSpan");

			this.axis = axis;
		}

		@Override
		public Integer getCurrentValue(EnclosureBounds parentValue)
		{
			return parentValue.getCurrentSpan(axis);
		}
	}

	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	private static class PositionProperty extends UserInterfaceTransactionQuery.Property<EnclosureBounds, Integer>
	{
		private static PositionProperty oriented(Axis axis) 
		{
			switch (axis)
			{
				case H:
					return HORIZONTAL_INSTANCE;
				case V:
					return VERTICAL_INSTANCE;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		} 

		private static final PositionProperty HORIZONTAL_INSTANCE = new PositionProperty(Axis.H);
		private static final PositionProperty VERTICAL_INSTANCE = new PositionProperty(Axis.V);

		private final Axis axis;

		PositionProperty(Axis axis)
		{
			super("getSpan");

			this.axis = axis;
		}

		@Override
		public Integer getCurrentValue(EnclosureBounds parentValue)
		{
			return parentValue.getCurrentPosition(axis);
		}
	}
}
