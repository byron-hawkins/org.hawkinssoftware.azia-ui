package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.router.CompositeRouter;

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
