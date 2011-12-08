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

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

/**
 * Generic transaction contribution to change the pressed state of some kind of <code>AbstractComponent</code>; often
 * transformed into a contribution which is more particular to the pressed component.
 * 
 * @author Byron Hawkins
 */
public class ChangePressedStateDirective extends ChangeComponentStateDirective
{
	/**
	 * Notification corresponding to the enclosing <code>ChangePressedStateDirective</code>.
	 * 
	 * @author Byron Hawkins
	 */
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
