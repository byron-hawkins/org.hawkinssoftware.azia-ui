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
package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.router.CompositeRouter;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public abstract class AbstractEventDispatch implements UserInterfaceHandler.Host, UserInterfaceActor
{
	private final CompositeRouter router = new CompositeRouter();

	@Override
	public void installHandler(UserInterfaceHandler handler)
	{
		router.installHandler(handler);
	}

	@Override
	public <HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler)
	{
		if (oldHandler != null)
		{
			router.removeHandler(oldHandler);
		}
		router.installHandler(newHandler);
	}

	@Override
	public void removeHandler(UserInterfaceHandler handler)
	{
		router.removeHandler(handler);
	}

	@Override
	public final void apply(UserInterfaceDirective action)
	{
		router.routeAction(action);
	}

	@Override
	public final void actionPosted(UserInterfaceNotification notification, PendingTransaction transaction)
	{
		router.routeNote(notification, transaction);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return this;
	}
}
