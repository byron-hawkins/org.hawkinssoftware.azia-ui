package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

public class SetVisibleDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public boolean isVisible()
		{
			return visible;
		}
	}

	public final boolean visible;

	public SetVisibleDirective(UserInterfaceActorDelegate actor, boolean visible)
	{
		super(actor);
		this.visible = visible;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
