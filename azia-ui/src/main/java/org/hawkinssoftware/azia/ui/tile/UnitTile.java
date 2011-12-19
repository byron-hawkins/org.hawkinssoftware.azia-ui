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
package org.hawkinssoftware.azia.ui.tile;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * Contains zero or one units.
 * 
 * @author b
 */
@ValidateRead
@ValidateWrite
@InvocationConstraint(domains = TileLayoutDomain.class)
public class UnitTile<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractUnitTile<KeyType>
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public enum Layout
	{
		LEFT,
		CENTER,
		RIGHT,
		FILL,
		// applied in the same way as CENTER, but indicates a preference not to be surrounded by extraneous space
		FIT;
	}

	Layout xLayout;
	Layout yLayout;

	private final Map<LayoutUnit.Floater.Edge, List<LayoutUnit.Floater<KeyType>>> floaters = createFloaters();

	public UnitTile(KeyType key)
	{
		super(key);
	}

	public void addFloater(LayoutUnit.Floater<KeyType> floater)
	{
		floaters.get(floater.getEdge()).add(floater);
	}

	public void setLayoutPolicy(Axis axis, Layout policy)
	{
		switch (axis)
		{
			case H:
				this.xLayout = policy;
				break;
			case V:
				this.yLayout = policy;
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	protected void accommodateBoundsChange(TileBoundsChangeDirective.Notification notification, PendingTransaction transaction)
	{
		int xCommand = notification.getBounds().x;
		int yCommand = notification.getBounds().y;
		int wCommand = notification.getBounds().width;
		int hCommand = notification.getBounds().height;
		if (padding != null)
		{
			xCommand += padding.left;
			wCommand -= (padding.left + padding.right);
			yCommand += padding.top;
			hCommand -= (padding.top + padding.bottom);
		}

		if (unit != null)
		{
			int x, width;
			if (xLayout == Layout.FILL)
			{
				x = xCommand;
				width = wCommand;
			}
			else
			{
				width = unit.getPackedSize(Axis.H);
				switch (xLayout)
				{
					case CENTER:
					case FIT:
						x = xCommand + ((wCommand - width) / 2);
						break;
					case LEFT:
						x = xCommand;
						break;
					case RIGHT:
						x = xCommand + (wCommand - width);
						break;
					default:
						throw new UnknownEnumConstantException(xLayout);
				}
			}
			int y, height;
			if (yLayout == Layout.FILL)
			{
				y = yCommand;
				height = hCommand;
			}
			else
			{
				height = unit.getPackedSize(Axis.V);
				switch (yLayout)
				{
					case CENTER:
					case FIT:
						y = yCommand + ((hCommand - height) / 2);
						break;
					case LEFT:
						y = yCommand;
						break;
					case RIGHT:
						y = yCommand + (hCommand - height);
						break;
					default:
						throw new UnknownEnumConstantException(yLayout);
				}
			}
			TileBoundsChangeDirective newUnitBounds = new TileBoundsChangeDirective(unit, new EnclosureBounds(x, y, width, height));
			transaction.contribute(newUnitBounds);

			int xExtent = x + width;
			for (LayoutUnit.Floater<KeyType> leftFloater : floaters.get(LayoutUnit.Floater.Edge.LEFT))
			{
				int floaterWidth = leftFloater.getPackedSize(Axis.H);
				TileBoundsChangeDirective floaterBounds = new TileBoundsChangeDirective(leftFloater, new EnclosureBounds(x, y, floaterWidth, height));
				transaction.contribute(floaterBounds);
				x += floaterWidth;
			}
			for (LayoutUnit.Floater<KeyType> rightFloater : floaters.get(LayoutUnit.Floater.Edge.RIGHT))
			{
				int floaterWidth = rightFloater.getPackedSize(Axis.H);
				xExtent -= floaterWidth;
				TileBoundsChangeDirective floaterBounds = new TileBoundsChangeDirective(rightFloater, new EnclosureBounds(xExtent, y, floaterWidth, height));
				transaction.contribute(floaterBounds);
			}
		}
	}

	@Override
	public void actionPosted(UserInterfaceNotification notification, PendingTransaction transaction)
	{
		super.actionPosted(notification, transaction);

		if (notification instanceof PaintIncludeNotification)
		{
			for (LayoutUnit.Floater<KeyType> leftFloater : floaters.get(LayoutUnit.Floater.Edge.LEFT))
			{
				transaction.contribute(new PaintIncludeNotification(leftFloater));
			}
			for (LayoutUnit.Floater<KeyType> rightFloater : floaters.get(LayoutUnit.Floater.Edge.RIGHT))
			{
				transaction.contribute(new PaintIncludeNotification(rightFloater));
			}
		}
	}

	/**
	 * @JTourBusStop 4.3, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction propagated
	 *               through client components:
	 * 
	 *               ...and the loop continues through all the layout tiles...
	 */
	@Override
	protected void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		super.mouseStateChange(pass, transaction);

		if (bounds.contains(pass.event()))
		{
			for (LayoutUnit.Floater<KeyType> leftFloater : floaters.get(LayoutUnit.Floater.Edge.LEFT))
			{
				transaction.contribute(new Forward(leftFloater));
			}
			for (LayoutUnit.Floater<KeyType> rightFloater : floaters.get(LayoutUnit.Floater.Edge.RIGHT))
			{
				transaction.contribute(new Forward(rightFloater));
			}
		}
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		switch (axis)
		{
			case H:
				if (xLayout == Layout.FIT)
				{
					return BoundedEntity.Expansion.FIT;
				}
				else
				{
					return BoundedEntity.Expansion.FILL;
				}
			case V:
				if (yLayout == Layout.FIT)
				{
					return BoundedEntity.Expansion.FIT;
				}
				else
				{
					return BoundedEntity.Expansion.FILL;
				}
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	private Map<LayoutUnit.Floater.Edge, List<LayoutUnit.Floater<KeyType>>> createFloaters()
	{
		Map<LayoutUnit.Floater.Edge, List<LayoutUnit.Floater<KeyType>>> floaters = new EnumMap<LayoutUnit.Floater.Edge, List<LayoutUnit.Floater<KeyType>>>(
				LayoutUnit.Floater.Edge.class);
		for (LayoutUnit.Floater.Edge edge : LayoutUnit.Floater.Edge.values())
		{
			floaters.put(edge, new ArrayList<LayoutUnit.Floater<KeyType>>());
		}
		return floaters;
	}
}
