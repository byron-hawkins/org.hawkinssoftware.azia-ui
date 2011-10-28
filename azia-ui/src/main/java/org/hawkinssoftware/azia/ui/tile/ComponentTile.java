package org.hawkinssoftware.azia.ui.tile;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@InvocationConstraint(domains = TileLayoutDomain.class)
public class ComponentTile<KeyType extends LayoutEntity.Key<KeyType>> implements LayoutRegion, LayoutUnit<KeyType>
{
	private final KeyType key;

	ComponentEnclosure<?, ?> component;

	EnclosureBounds bounds = EnclosureBounds.EMPTY;

	public ComponentTile(KeyType key)
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
		return (component != null);
	}
	
	@Override
	public EnclosureBounds getBounds()
	{
		return bounds;
	}

	public void setComponent(ComponentEnclosure<?, ?> component)
	{
		this.component = component;
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
			transaction.contribute(((TileBoundsChangeDirective.Notification) notification).createEnclosureEncounter(component));
		}
		else if (notification instanceof EventPass)
		{
			mouseStateChange((EventPass) notification, transaction);
		}
		else if (notification instanceof PaintIncludeNotification)
		{
			transaction.contribute(new PaintIncludeNotification(component));
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

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		return component.getExpansion(axis);
	}

	protected void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (bounds.contains(pass.event()))
		{
			transaction.contribute(new Contact(this));
			transaction.contribute(new Forward(component.getComponent()));
		}
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return component.getPackedSize(axis);
	}

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		return component.getMaxSize(axis);
	}
}
