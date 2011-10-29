/*
 * Copyright (c) 2011 HawkinsSoftware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Byron Hawkins of HawkinsSoftware
 */
package org.hawkinssoftware.azia.ui.component.transaction.clipboard;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ClipboardChangeDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
