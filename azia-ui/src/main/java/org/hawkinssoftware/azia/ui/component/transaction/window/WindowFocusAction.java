package org.hawkinssoftware.azia.ui.component.transaction.window;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class WindowFocusAction extends UserInterfaceDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public boolean isFocused()
		{
			return focused;
		}
	}
	
	public final boolean focused;

	public WindowFocusAction(UserInterfaceActorDelegate actor, boolean focused)
	{
		super(actor);
		this.focused = focused;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
