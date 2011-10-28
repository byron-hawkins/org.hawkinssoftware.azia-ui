package org.hawkinssoftware.azia.ui.tile.transaction.modify;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;

public class InstallNewLayoutDirective<KeyType extends LayoutEntity.Key<KeyType>> extends UserInterfaceDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public TopTile<?> getModification()
		{
			return modified;
		}
	}

	public final TopTile<KeyType> modified;

	InstallNewLayoutDirective(TopTile<KeyType> original, TopTile<KeyType> modified)
	{
		super(original);

		this.modified = modified;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
