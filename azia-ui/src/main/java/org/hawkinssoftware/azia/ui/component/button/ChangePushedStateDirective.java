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
package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ChangePushedStateDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
