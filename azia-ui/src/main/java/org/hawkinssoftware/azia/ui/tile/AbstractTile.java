package org.hawkinssoftware.azia.ui.tile;

import java.util.Collections;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;

public abstract class AbstractTile<KeyType extends LayoutEntity.Key<KeyType>> implements LayoutEntity<KeyType>
{
	private final KeyType key;

	protected AbstractTile(KeyType key)
	{
		this.key = key;
	}

	@Override
	public final KeyType getKey()
	{
		return key;
	}

	@Override
	public boolean hasPreviews()
	{
		return false;
	}

	@Override
	public java.util.List<UserInterfaceActorPreview> getPreviews(UserInterfaceDirective action)
	{
		return Collections.emptyList();
	}
}
