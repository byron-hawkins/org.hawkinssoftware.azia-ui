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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.router.CompositeRouter;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public abstract class VirtualComponent implements UserInterfaceHandler.Host, UserInterfaceActorPreview.Host, ComponentDataHandler.Host,
		ChangeComponentStateDirective.Component, CompositionElement.Initializing
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class MouseContactHandler implements UserInterfaceHandler
	{
		public void mouseStateChange(EventPass pass, PendingTransaction transaction)
		{
			transaction.contribute(new Contact(VirtualComponent.this));
		}
	}

	private final CompositeRouter router = new CompositeRouter();

	private final Map<ComponentDataHandler.Key<?>, ComponentDataHandler> dataHandlers = new HashMap<ComponentDataHandler.Key<?>, ComponentDataHandler>();

	@InvocationConstraint(extendedTypes = VirtualComponent.class)
	public VirtualComponent()
	{
		router.installHandler(new MouseContactHandler());
	}

	@Override
	public void installHandler(UserInterfaceHandler handler)
	{
		router.installHandler(handler);

		if (handler instanceof ComponentDataHandler)
		{
			ComponentDataHandler dataHandler = (ComponentDataHandler) handler;
			ComponentDataHandler currentHandler = dataHandlers.get(dataHandler.key);
			if (currentHandler != null)
			{
				router.removeHandler(currentHandler);
			}
			dataHandlers.put(dataHandler.key, dataHandler);
		}
	}

	@Override
	public <HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler)
	{
		if (oldHandler != null)
		{
			removeHandler(oldHandler);
		}
		installHandler(newHandler);
	}

	@Override
	public void removeHandler(UserInterfaceHandler handler)
	{
		router.removeHandler(handler);

		if (handler instanceof ComponentDataHandler)
		{
			dataHandlers.remove(((ComponentDataHandler) handler).key);
		}
	}
	
	@Override
	public List<UserInterfaceActorPreview> getPreviews(UserInterfaceDirective action)
	{
		return router.getPreviews(action);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <HandlerType extends ComponentDataHandler> HandlerType getDataHandler(ComponentDataHandler.Key<HandlerType> key)
	{
		return (HandlerType) dataHandlers.get(key);
	}

	@Override
	public Collection<ComponentDataHandler> getDataHandlers()
	{
		return dataHandlers.values();
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
	public PaintableActor getActor()
	{
		return this;
	}

	@Override
	public void compositionCompleted()
	{
	}
}
