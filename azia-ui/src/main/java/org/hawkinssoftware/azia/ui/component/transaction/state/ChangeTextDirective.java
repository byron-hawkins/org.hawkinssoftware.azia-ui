package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class ChangeTextDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public String getText()
		{
			return text;
		}
	}

	public final String text;

	public ChangeTextDirective(UserInterfaceActorDelegate actor, String text)
	{
		super(actor);
		this.text = text;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
