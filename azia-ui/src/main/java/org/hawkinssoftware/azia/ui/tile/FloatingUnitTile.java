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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective.Notification;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
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
public class FloatingUnitTile<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractUnitTile<KeyType> implements LayoutUnit.Floater<KeyType>
{
	private LayoutUnit.Floater.Edge edge = null;

	public FloatingUnitTile(KeyType key)
	{
		super(key);
	}

	@Override
	protected void accommodateBoundsChange(Notification notification, PendingTransaction transaction)
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

		TileBoundsChangeDirective newUnitBounds = new TileBoundsChangeDirective(unit, new EnclosureBounds(xCommand, yCommand, wCommand, hCommand));
		transaction.contribute(newUnitBounds);
	}

	@Override
	public LayoutUnit.Floater.Edge getEdge()
	{
		return edge;
	}

	public void setEdge(LayoutUnit.Floater.Edge edge)
	{
		this.edge = edge;
	}

	@Override
	public boolean isConfigured()
	{
		return super.isConfigured() && (edge != null);
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FIT;
	}
}
