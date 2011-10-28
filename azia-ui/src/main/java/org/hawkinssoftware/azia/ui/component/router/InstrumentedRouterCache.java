package org.hawkinssoftware.azia.ui.component.router;

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;

class InstrumentedRouterCache
{
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
