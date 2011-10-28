package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class ChangePressedStateDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public boolean isPressed()
		{
			return isPressed;
		}

		public UserInterfaceActor getButton()
		{
			return getActor();
		}
	}

	private final boolean isPressed;

	public ChangePressedStateDirective(UserInterfaceActorDelegate actor, boolean isPressed)
	{
		super(actor);
		this.isPressed = isPressed;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
