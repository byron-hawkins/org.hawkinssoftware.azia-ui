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

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
class InstrumentedRouterCache
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <RouterImplementationType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	private static class DeliverableAssignedRouter<RouterImplementationType extends AbstractComponentRouter>
	{
		private final Map<String, GeneratedRouter<RouterImplementationType>> instrumentedRoutersByDeliverableType = new HashMap<String, GeneratedRouter<RouterImplementationType>>();

		void assign(GeneratedRouter<RouterImplementationType> router, String actionType)
		{
			instrumentedRoutersByDeliverableType.put(actionType, router);
		}

		GeneratedRouter<RouterImplementationType> getRouter(String deliverableTypename)
		{
			return instrumentedRoutersByDeliverableType.get(deliverableTypename);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <RouterImplementationType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	private static class HandlerAssignedRouter<RouterImplementationType extends AbstractComponentRouter>
	{
		private final Map<String, DeliverableAssignedRouter<RouterImplementationType>> deliverableRoutersByHandlerType = new HashMap<String, DeliverableAssignedRouter<RouterImplementationType>>();

		DeliverableAssignedRouter<RouterImplementationType> getDeliverableRouter(String handlerClassname)
		{
			DeliverableAssignedRouter<RouterImplementationType> router = deliverableRoutersByHandlerType.get(handlerClassname);
			if (router == null)
			{
				router = new DeliverableAssignedRouter<RouterImplementationType>();
				deliverableRoutersByHandlerType.put(handlerClassname, router);
			}
			return router;
		}

		GeneratedRouter<RouterImplementationType> getRouter(String handlerTypename, String deliverableTypename)
		{
			DeliverableAssignedRouter<RouterImplementationType> router = deliverableRoutersByHandlerType.get(handlerTypename);
			if (router == null)
			{
				return null;
			}
			else
			{
				return router.getRouter(deliverableTypename);
			}
		}
	}

	private static InstrumentedRouterCache INSTANCE = new InstrumentedRouterCache();

	static InstrumentedRouterCache getInstance()
	{
		return INSTANCE;
	}

	private final HandlerAssignedRouter<ComponentDirectiveRouter> actionRouters = new HandlerAssignedRouter<ComponentDirectiveRouter>();
	private final HandlerAssignedRouter<ComponentNotificationRouter> noteRouters = new HandlerAssignedRouter<ComponentNotificationRouter>();

	GeneratedRouter<ComponentDirectiveRouter> getActionRouter(Class<? extends UserInterfaceHandler> handlerType,
			Class<? extends UserInterfaceDirective> actionType)
	{
		return actionRouters.getRouter(handlerType.getName(), actionType.getName());
	}

	GeneratedRouter<ComponentNotificationRouter> getNoteRouter(Class<? extends UserInterfaceHandler> handlerType,
			Class<? extends UserInterfaceNotification> noteType)
	{
		return noteRouters.getRouter(handlerType.getName(), noteType.getName());
	}

	@SuppressWarnings("unchecked")
	void assignDeliverable(RouterType type, GeneratedRouter<?> router, String deliverableTypename)
	{
		if (type == RouterType.DIRECTIVE)
		{
			actionRouters.getDeliverableRouter(router.getDefinition().getHandlerClassname()).assign((GeneratedRouter<ComponentDirectiveRouter>) router,
					deliverableTypename);
		}
		else
		{
			noteRouters.getDeliverableRouter(router.getDefinition().getHandlerClassname()).assign((GeneratedRouter<ComponentNotificationRouter>) router,
					deliverableTypename);
		}
	}
}
