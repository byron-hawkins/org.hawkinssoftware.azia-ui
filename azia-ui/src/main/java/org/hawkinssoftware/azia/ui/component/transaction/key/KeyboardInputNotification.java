package org.hawkinssoftware.azia.ui.component.transaction.key;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;

public class KeyboardInputNotification extends UserInterfaceNotification
{
	public final KeyboardInputEvent event;

	KeyboardInputNotification(KeyboardInputEvent event)
	{
		this.event = event;
	}
}
