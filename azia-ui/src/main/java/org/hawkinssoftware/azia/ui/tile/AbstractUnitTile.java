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
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective.Notification;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
public abstract class AbstractUnitTile<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractTile<KeyType> implements LayoutTile<KeyType>
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	protected static class Padding
	{
		int top;
		int right;
		int bottom;
		int left;

		Padding(int top, int right, int bottom, int left)
		{
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			this.left = left;
		}
	}

	EnclosureBounds bounds = EnclosureBounds.EMPTY;

	LayoutUnit<KeyType> unit;

	protected Padding padding = null;

	public AbstractUnitTile(KeyType key)
	{
		super(key);
	}

	protected abstract void accommodateBoundsChange(TileBoundsChangeDirective.Notification notification, PendingTransaction transaction);

	@Override
	public boolean isConfigured()
	{
		return true;
	}

	@Override
	public EnclosureBounds getBounds()
	{
		return bounds;
	}

	public void setUnit(LayoutUnit<KeyType> unit)
	{
		this.unit = unit;
	}

	public void setPadding(int top, int right, int bottom, int left)
	{
		if (padding == null)
		{
			padding = new Padding(top, right, bottom, left);
		}
		else
		{
			padding.top = top;
			padding.right = right;
			padding.bottom = bottom;
			padding.left = left;
		}
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
			transaction.contribute(new PaintIncludeNotification(unit));
		}
	}

	@Override
	public void apply(UserInterfaceDirective action)
	{
		if (action instanceof TileBoundsChangeDirective)
		{
			bounds = ((TileBoundsChangeDirective) action).bounds;
		}
	}

	protected void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (bounds.contains(pass.event()))
		{
			transaction.contribute(new Contact(this));
			transaction.contribute(new Forward(unit));
		}
	}

	private int pad(int size, Axis axis)
	{
		if (padding == null)
		{
			return size;
		}
		switch (axis)
		{
			case H:
				return size + padding.left + padding.right;
			case V:
				return size + padding.top + padding.bottom;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return pad(unit.getPackedSize(axis), axis);
	}

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		BoundedEntity.MaximumSize max = unit.getMaxSize(axis);
		if (max.exists())
		{
			max.setValue(pad(max.getValue(), axis));
		}
		return max;
	}
}
