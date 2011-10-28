package org.hawkinssoftware.azia.ui.tile;

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.InstallNewLayoutDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@VisibilityConstraint(voidInheritance = true)
@InvocationConstraint(domains = TileLayoutDomain.class)
public class TopTile<KeyType extends LayoutEntity.Key<KeyType>> implements LayoutEntity<KeyType>, LayoutRegion, BoundedEntity.LayoutRoot
{
	final Map<KeyType, LayoutEntity<KeyType>> entitiesByKey = new HashMap<KeyType, LayoutEntity<KeyType>>();

	private final KeyType key;

	EnclosureBounds bounds = EnclosureBounds.EMPTY;

	LayoutUnit<KeyType> unit;

	public TopTile(KeyType key)
	{
		this.key = key;
	}

	@Override
	public KeyType getKey()
	{
		return key;
	}
	
	@Override
	public boolean isConfigured()
	{
		return unit != null;
	}
	
	@Override
	public EnclosureBounds getBounds()
	{
		return bounds;
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		return BoundedEntity.Expansion.FILL;
	}

	public void addEntity(LayoutEntity<KeyType> entity)
	{
		entitiesByKey.put(entity.getKey(), entity);
	}

	public LayoutEntity<KeyType> getEntity(KeyType key)
	{
		return entitiesByKey.get(key);
	}

	public void setUnit(LayoutUnit<KeyType> unit)
	{
		this.unit = unit;
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
			if (unit != null)
			{
				TileBoundsChangeDirective.Notification change = (TileBoundsChangeDirective.Notification) notification;
				TileBoundsChangeDirective newUnitBounds = new TileBoundsChangeDirective(unit, change.getBounds());
				transaction.contribute(newUnitBounds);
			}
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
		if (action.getClass() == InstallNewLayoutDirective.class)
		{
			@SuppressWarnings("unchecked")
			InstallNewLayoutDirective<KeyType> replacement = (InstallNewLayoutDirective<KeyType>) action;
			unit = replacement.modified.unit;
			entitiesByKey.clear();
			entitiesByKey.putAll(replacement.modified.entitiesByKey);
		}
		else if (action instanceof TileBoundsChangeDirective)
		{
			bounds = ((TileBoundsChangeDirective) action).bounds;
		}

	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public TileBoundsChangeDirective createBoundsChangeDirective()
	{
		return new TileBoundsChangeDirective(unit, new EnclosureBounds(0, 0, bounds.width, bounds.height));
	}

	protected void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (bounds.contains(pass.event()))
		{
			transaction.contribute(new Contact(this));
			transaction.contribute(new Forward(unit));
		}
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return unit.getPackedSize(axis);
	}

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		return unit.getMaxSize(axis);
	}
}
