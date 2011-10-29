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
package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ChangeTextDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
