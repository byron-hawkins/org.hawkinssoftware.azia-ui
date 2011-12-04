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
package org.hawkinssoftware.azia.ui.component.transaction.window;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.DesktopContainer;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class WindowFocusAction extends UserInterfaceDirective
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Notification extends UserInterfaceNotification
	{
		public boolean isFocused()
		{
			return focused;
		}
		
		public DesktopContainer<?> getWindow()
		{
			return window;
		}
	}
	
	public final boolean focused;
	public final DesktopContainer<?> window;

	public WindowFocusAction(DesktopContainer<?> window, boolean focused)
	{
		super(window);
		this.focused = focused;
		this.window = window;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
