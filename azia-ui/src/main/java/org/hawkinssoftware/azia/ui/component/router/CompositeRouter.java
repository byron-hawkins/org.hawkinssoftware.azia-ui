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
package org.hawkinssoftware.azia.ui.component.router;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.ui.AziaUserInterfaceInitializer;
import org.hawkinssoftware.azia.ui.component.AbstractEventDispatch;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ClassLoadObservationDomain;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.MethodFilter;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ObservedType;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.moa.ExecutionPath;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.RNSUtils;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ExecutionPath.NoFrame
@VisibilityConstraint(extendedTypes = { VirtualComponent.class, AbstractEventDispatch.class })
@InvocationConstraint(types = { VirtualComponent.class, AbstractEventDispatch.class })
public final class CompositeRouter 
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private static class ActionMethodFilter implements ClassLoadObserver.MethodFilter
	{
		@Override
		public boolean acceptMethodName(String name)
		{
			return true;
		}

		@Override
		public int getParameterCount()
		{
			return 1;
		}

		@Override
		public String getParameterType(int index)
		{
			return UserInterfaceDirective.class.getName();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private static class NoteMethodFilter implements ClassLoadObserver.MethodFilter
	{
		@Override
		public boolean acceptMethodName(String name)
		{
			return true;
		}

		@Override
		public int getParameterCount()
		{
			return 2;
		}

		@Override
		public String getParameterType(int index)
		{
			switch (index)
			{
				case 0:
					return UserInterfaceNotification.class.getName();
				case 1:
					return PendingTransaction.class.getName();
			}
			return null;
		}
	}

	/**
	 * Listen to the ClassLoadObserver for introduction of any `UserInterfaceHandler to the JVM. On discovery,
	 * instrument a `RouterImplementationSet for it and install the set in the `RoutingTypeHierarchy.
	 * 
	 * @author b
	 */
	@ExecutionPath.NoFrame
	@DomainRole.Join(membership = ClassLoadObservationDomain.class)
	private static class ClassLoadListener implements ClassLoadObserver.FilteredObserver
	{
		private final String[] observedTypes = new String[] { UserInterfaceHandler.class.getName(), UserInterfaceDirective.class.getName(),
				UserInterfaceNotification.class.getName() };
		private final MethodFilter[] methodFilters = new MethodFilter[] { new ActionMethodFilter(), new NoteMethodFilter() };

		@Override
		public String[] getObservedTypenames()
		{
			return observedTypes;
		}

		@Override
		public MethodFilter[] getMethodFilters()
		{
			return methodFilters;
		}

		@Override
		public void matchingTypeObserved(ObservedType type)
		{
			if (type.observedTypes.contains(UserInterfaceHandler.class.getName()))
			{
				Log.out(Tag.ROUTER_INIT, "Instrumenting UIHandler: %s", RNSUtils.getPlainName(type.typeHierarchy.qualifiedName));

				try
				{
					RouterImplementationSet router = RouterImplementationFactory.getInstance().instrumentRouter(type);
					RoutingTypeHierarchy.getInstance().installRouters(router);
				}
				catch (Exception e)
				{
					Log.out(Tag.ROUTER_INIT, e, "Failed to instrument routers for handler type %s", type.typeHierarchy.qualifiedName);
				}
			}
			else if (type.observedTypes.contains(UserInterfaceDirective.class.getName()))
			{
				RoutingTypeHierarchy.getInstance().installDeliverable(type.typeHierarchy, RouterType.DIRECTIVE);
			}
			else if (type.observedTypes.contains(UserInterfaceNotification.class.getName()))
			{
				RoutingTypeHierarchy.getInstance().installDeliverable(type.typeHierarchy, RouterType.NOTIFICATION);
			}
		}
	}

	@InvocationConstraint(types = AziaUserInterfaceInitializer.class)
	public static void initialize()
	{
		ClassLoadObserver.observe(new ClassLoadListener());
	}

	private final List<UserInterfaceHandler> handlers = new ArrayList<UserInterfaceHandler>();

	public void routeAction(UserInterfaceDirective action)
	{
		for (UserInterfaceHandler handler : handlers)
		{
			GeneratedRouter<ComponentDirectiveRouter> router = InstrumentedRouterCache.getInstance().getActionRouter(handler.getClass(), action.getClass());
			if (router != null)
			{
				router.getImplementation().route(action, handler);
			}
		}
	}

	public void routeNote(UserInterfaceNotification note, PendingTransaction transaction)
	{
		for (UserInterfaceHandler handler : handlers)
		{
			GeneratedRouter<ComponentNotificationRouter> router = InstrumentedRouterCache.getInstance().getNoteRouter(handler.getClass(), note.getClass());
			if (router != null)
			{
				router.getImplementation().route(note, handler, transaction);
			}
		}
	}

	public void installHandler(UserInterfaceHandler handler)
	{
		handlers.add(handler);
	}

	public void removeHandler(UserInterfaceHandler handler)
	{
		handlers.remove(handler);
	}
}
