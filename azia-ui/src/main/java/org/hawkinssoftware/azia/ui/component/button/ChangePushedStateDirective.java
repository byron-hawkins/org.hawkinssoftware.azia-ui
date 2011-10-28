package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class ChangePushedStateDirective extends UserInterfaceDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public boolean isPushed()
		{
			return isPushed;
		}

		public UserInterfaceActor getButton()
		{
			return getActor();
		}
	}

	public final boolean isPushed;

	public ChangePushedStateDirective(UserInterfaceActorDelegate actor, boolean isPushed)
	{
		super(actor);
		this.isPushed = isPushed;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
