package org.hawkinssoftware.azia.ui.component.transaction.clipboard;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

public class ClipboardChangeDirective extends UserInterfaceDirective
{
	public static class Notification extends UserInterfaceNotification
	{
		public final ClipboardContents clipboardContents;

		Notification(ClipboardContents clipboardContents)
		{
			this.clipboardContents = clipboardContents;
		}
	}

	public final ClipboardContents clipboardContents;

	public ClipboardChangeDirective(ClipboardContents clipboardContents)
	{
		super(ClipboardEventDispatch.getInstance());

		this.clipboardContents = clipboardContents;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification(clipboardContents);
	}
}
