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

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.MoveInlineDivisionDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective.Notification;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@InvocationConstraint(domains = TileLayoutDomain.class)
public class PairTile<KeyType extends LayoutEntity.Key<KeyType>> implements LayoutRegion, LayoutUnit<KeyType>, LayoutTile<KeyType>
{
	private final KeyType key;

	EnclosureBounds bounds = EnclosureBounds.EMPTY;

	LayoutTile<KeyType> first;
	LayoutTile<KeyType> second;

	final Axis axis;
	BoundedEntity.Expansion crossExpansion;

	private int inlineDivision = 0;

	public PairTile(KeyType key, Axis axis)
	{
		this.key = key;
		this.axis = axis;
	}

	@Override
	public KeyType getKey()
	{
		return key;
	}

	@Override
	public boolean isConfigured()
	{
		return ((first != null) && (second != null) && (crossExpansion != null));
	}

	@Override
	public EnclosureBounds getBounds()
	{
		return bounds;
	}

	public void setFirst(LayoutTile<KeyType> first)
	{
		this.first = first;
	}

	public void setSecond(LayoutTile<KeyType> second)
	{
		this.second = second;
	}

	public void setCrossExpansion(BoundedEntity.Expansion crossExpansion)
	{
		this.crossExpansion = crossExpansion;
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return this;
	}

	@Override
	public void actionPosted(UserInterfaceNotification notification, PendingTransaction transaction)
	{
		if (notification instanceof TileBoundsChangeDirective.Notification)
		{
			accommodateBoundsChange((Notification) notification, transaction);
		}
		else if (notification instanceof EventPass)
		{
			mouseStateChange((EventPass) notification, transaction);
		}
		else if (notification instanceof PaintIncludeNotification)
		{
			transaction.contribute(new PaintIncludeNotification(first));
			transaction.contribute(new PaintIncludeNotification(second));
		}
	}

	@Override
	public void apply(UserInterfaceDirective action)
	{
		if (action instanceof TileBoundsChangeDirective)
		{
			bounds = ((TileBoundsChangeDirective) action).bounds;
		}
		else if (action instanceof MoveInlineDivisionDirective)
		{
			inlineDivision = ((MoveInlineDivisionDirective) action).inlineDivision;
		}
	}

	private void accommodateBoundsChange(TileBoundsChangeDirective.Notification notification, PendingTransaction transaction)
	{
		int inlineExtent;
		int crosslineExtent;
		switch (axis)
		{
			case H:
				inlineExtent = notification.getBounds().width;
				crosslineExtent = notification.getBounds().height;
				break;
			case V:
				inlineExtent = notification.getBounds().height;
				crosslineExtent = notification.getBounds().width;
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}

		int firstSpan;
		if (first.getExpansion(axis) == second.getExpansion(axis))
		{
			firstSpan = inlineExtent / 2;
		}
		else if (first.getExpansion(axis) == BoundedEntity.Expansion.FIT)
		{
			firstSpan = first.getPackedSize(axis);
		}
		else
		{
			firstSpan = inlineExtent - second.getPackedSize(axis);
		}

		TileBoundsChangeDirective newFirstBounds;
		TileBoundsChangeDirective newSecondBounds;

		switch (axis)
		{
			case H:
				newFirstBounds = new TileBoundsChangeDirective(first, new EnclosureBounds(notification.getBounds().x, notification.getBounds().y, firstSpan,
						crosslineExtent));
				newSecondBounds = new TileBoundsChangeDirective(second, new EnclosureBounds(notification.getBounds().x + firstSpan, notification.getBounds().y,
						inlineExtent - firstSpan, crosslineExtent));
				break;
			case V:
				newFirstBounds = new TileBoundsChangeDirective(first, new EnclosureBounds(notification.getBounds().x, notification.getBounds().y,
						crosslineExtent, firstSpan));
				newSecondBounds = new TileBoundsChangeDirective(second, new EnclosureBounds(notification.getBounds().x, notification.getBounds().y + firstSpan,
						crosslineExtent, inlineExtent - firstSpan));
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}

		transaction.contribute(newFirstBounds);
		transaction.contribute(newSecondBounds);
		transaction.contribute(new MoveInlineDivisionDirective(this, notification.getBounds().getPosition(axis) + firstSpan));
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		if (axis == this.axis)
		{
			if ((first.getExpansion(axis) == BoundedEntity.Expansion.FILL) || (second.getExpansion(axis) == BoundedEntity.Expansion.FILL))
			{
				return BoundedEntity.Expansion.FILL;
			}
			else
			{
				return BoundedEntity.Expansion.FIT;
			}
		}
		else
		{
			return crossExpansion;
		}
	}

	protected void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (bounds.contains(pass.event()))
		{
			transaction.contribute(new Contact(this));

			switch (axis)
			{
				case H:
					if (pass.event().x() < inlineDivision)
					{
						transaction.contribute(new Forward(first));
					}
					else
					{
						transaction.contribute(new Forward(second));
					}
					break;
				case V:
					if (pass.event().y() < inlineDivision)
					{
						transaction.contribute(new Forward(first));
					}
					else
					{
						transaction.contribute(new Forward(second));
					}
					break;
				default:
					throw new UnknownEnumConstantException(axis);

			}
		}
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		if (axis == this.axis)
		{
			return first.getPackedSize(axis) + second.getPackedSize(axis);
		}
		else
		{
			return Math.max(first.getPackedSize(axis), second.getPackedSize(axis));
		}
	}

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		BoundedEntity.MaximumSize maxFirst = first.getMaxSize(axis);
		if (!maxFirst.exists())
		{
			maxFirst = new BoundedEntity.MaximumSize(first.getPackedSize(axis));
		}
		BoundedEntity.MaximumSize maxSecond = second.getMaxSize(axis);
		if (!maxSecond.exists())
		{
			maxSecond = new BoundedEntity.MaximumSize(second.getPackedSize(axis));
		}

		if (axis == this.axis)
		{
			return new BoundedEntity.MaximumSize(maxFirst.getValue() + maxSecond.getValue());
		}
		else
		{
			if (maxFirst.getValue() > maxSecond.getValue())
			{
				return maxFirst;
			}
			else
			{
				return maxSecond;
			}
		}
	}
}
