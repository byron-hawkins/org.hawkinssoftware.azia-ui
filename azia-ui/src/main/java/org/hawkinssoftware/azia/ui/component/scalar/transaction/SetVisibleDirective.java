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
package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class SetVisibleDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Notification extends UserInterfaceNotification
	{
		public boolean isVisible()
		{
			return visible;
		}
	}

	public final boolean visible;

	public SetVisibleDirective(UserInterfaceActorDelegate actor, boolean visible)
	{
		super(actor);
		this.visible = visible;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
