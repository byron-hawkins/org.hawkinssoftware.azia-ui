package org.hawkinssoftware.azia.ui.component.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.TypeHierarchy;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.util.RNSUtils;

class RoutingTypeHierarchy
{
	private static class DeliverableType
	{
		static DeliverableType get(TypeHierarchy deliverableTypeHierarchy, RouterType type)
		{
			DeliverableType deliverableType = ALL.get(deliverableTypeHierarchy.qualifiedName);
			if (deliverableType == null)
			{
				getInstance().installDeliverable(deliverableTypeHierarchy, type);
			}
			return ALL.get(deliverableTypeHierarchy.qualifiedName);
		}

		static final Map<String, DeliverableType> ALL = new HashMap<String, DeliverableType>();

		final String classname;
		final DeliverableType parentType;
		final Set<DeliverableType> subTypes = new HashSet<DeliverableType>();

		private DeliverableType(String classname, DeliverableType parentType)
		{
			this.classname = classname;
			this.parentType = parentType;

			DeliverableType.ALL.put(classname, this);
		}

		DeliverableType createSubtype(String classname)
		{
			DeliverableType subtype = new DeliverableType(classname, this);
			subTypes.add(subtype);
			return subtype;
		}
	}

	private static class Router<RouterImplementationType extends AbstractComponentRouter>
	{
		final GeneratedRouter<RouterImplementationType> router;
		final DeliverableType deliverable;

		Router(GeneratedRouter<RouterImplementationType> router, DeliverableType deliverable)
		{
			this.router = router;
			this.deliverable = deliverable;
		}
	}

	private static class DeliverableSubtree<RouterImplementationType extends AbstractComponentRouter>
	{
		final GeneratedRouter<RouterImplementationType> router;
		final DeliverableType root;
		final Set<DeliverableType> subtree = new HashSet<DeliverableType>();

		DeliverableSubtree(GeneratedRouter<RouterImplementationType> router, DeliverableType root)
		{
			this.router = router;
			this.root = root;
			subtree.addAll(root.subTypes);
		}
	}

	private static class DeliverableSubtrees<RouterImplementationType extends AbstractComponentRouter>
	{
		final RouterType routerType;
		final List<DeliverableSubtree<RouterImplementationType>> subtrees = new ArrayList<DeliverableSubtree<RouterImplementationType>>();

		DeliverableSubtrees(RouterType type)
		{
			this.routerType = type;
		}

		void assignDeliverables()
		{
			for (int i = 0; i < subtrees.size(); i++)
			{
				for (int j = i + 1; j < subtrees.size(); j++)
				{
					if (subtrees.get(i).subtree.contains(subtrees.get(j).root))
					{
						subtrees.get(i).subtree.removeAll(subtrees.get(j).subtree);
					}
					else if (subtrees.get(j).subtree.contains(subtrees.get(i).root))
					{
						subtrees.get(j).subtree.removeAll(subtrees.get(i).subtree);
					}
				}
			}

			for (DeliverableSubtree<RouterImplementationType> subtree : subtrees)
			{
				InstrumentedRouterCache.getInstance().assignDeliverable(routerType, subtree.router, subtree.root.classname);
				for (DeliverableType deliverableType : subtree.subtree)
				{
					InstrumentedRouterCache.getInstance().assignDeliverable(routerType, subtree.router, deliverableType.classname);
				}
			}
		}
	}

	private static class HandlerType
	{
		private final List<Router<ComponentDirectiveRouter>> actionRouters = new ArrayList<Router<ComponentDirectiveRouter>>();
		private final List<Router<ComponentNotificationRouter>> noteRouters = new ArrayList<Router<ComponentNotificationRouter>>();

		HandlerType(RouterImplementationSet routers)
		{
			DeliverableSubtrees<ComponentDirectiveRouter> deliverableActionSubtrees = new DeliverableSubtrees<ComponentDirectiveRouter>(RouterType.DIRECTIVE);
			for (GeneratedRouter<ComponentDirectiveRouter> actionRouter : routers.getActionRouters())
			{
				DeliverableType deliverable = DeliverableType.get(actionRouter.getDefinition().getParameterType(), RouterType.DIRECTIVE);
				actionRouters.add(new Router<ComponentDirectiveRouter>(actionRouter, deliverable));
				deliverableActionSubtrees.subtrees.add(new DeliverableSubtree<ComponentDirectiveRouter>(actionRouter, deliverable));
			}
			deliverableActionSubtrees.assignDeliverables();

			DeliverableSubtrees<ComponentNotificationRouter> deliverableNoteSubtrees = new DeliverableSubtrees<ComponentNotificationRouter>(
					RouterType.NOTIFICATION);
			for (GeneratedRouter<ComponentNotificationRouter> noteRouter : routers.getNoteRouters())
			{
				DeliverableType deliverable = DeliverableType.get(noteRouter.getDefinition().getParameterType(), RouterType.NOTIFICATION);
				noteRouters.add(new Router<ComponentNotificationRouter>(noteRouter, deliverable));
				deliverableNoteSubtrees.subtrees.add(new DeliverableSubtree<ComponentNotificationRouter>(noteRouter, deliverable));
			}
			deliverableNoteSubtrees.assignDeliverables();
		}

		@SuppressWarnings("unchecked")
		void assignDeliverable(DeliverableType deliverableType, RouterType type)
		{
			int bestDowncastCount = Integer.MAX_VALUE;
			Router<?> bestAssignment = null;
			List<Router<?>> routers = (List<Router<?>>) ((type == RouterType.DIRECTIVE) ? actionRouters : noteRouters);
			for (Router<?> router : routers)
			{
				int downcastCount = 0;
				DeliverableType travesral = deliverableType;
				while ((travesral != null) && (travesral != router.deliverable))
				{
					downcastCount++;
					travesral = travesral.parentType;
				}
				if ((travesral != null) && (downcastCount < bestDowncastCount))
				{
					bestDowncastCount = downcastCount;
					bestAssignment = router;
				}
			}

			if (bestAssignment != null)
			{
				InstrumentedRouterCache.getInstance().assignDeliverable(type, bestAssignment.router, deliverableType.classname);
			}
		}
	}

	private static RoutingTypeHierarchy INSTANCE = new RoutingTypeHierarchy();

	static RoutingTypeHierarchy getInstance()
	{
		return INSTANCE;
	}

	private final List<HandlerType> handlerTypes = new ArrayList<HandlerType>();
	private final DeliverableType userInterfaceDirective = new DeliverableType(UserInterfaceDirective.class.getName(), null);
	private final DeliverableType userInterfaceNotification = new DeliverableType(UserInterfaceNotification.class.getName(), null);

	void installRouters(RouterImplementationSet routers)
	{
		handlerTypes.add(new HandlerType(routers));
	}

	void installDeliverable(TypeHierarchy deliverableType, RouterType type)
	{
		if (DeliverableType.ALL.containsKey(deliverableType.qualifiedName))
		{
			// it was jumpstarted already
			return;
		}

		Log.out(Tag.ROUTER_INIT, "Loaded %s: %s", type, RNSUtils.getPlainName(deliverableType.qualifiedName));

		List<TypeHierarchy> delta = new ArrayList<TypeHierarchy>();
		TypeHierarchy observedAction = deliverableType;
		DeliverableType entryPoint = null;
		while (observedAction != null)
		{
			DeliverableType existingType = DeliverableType.ALL.get(observedAction.qualifiedName);
			if (existingType != null)
			{
				entryPoint = existingType;
				break;
			}
			delta.add(observedAction);
			observedAction = observedAction.supertype;
		}

		if (entryPoint == null)
		{
			throw new RuntimeException("Unable to find the entry point for observed action type " + deliverableType.qualifiedName);
		}

		DeliverableType parent = entryPoint;
		for (int i = delta.size() - 1; i >= 0; i--)
		{
			parent = parent.createSubtype(delta.get(i).qualifiedName);

			for (HandlerType handler : handlerTypes)
			{
				handler.assignDeliverable(parent, type);
			}
		}
	}

	private void installDeliverable(Class<?> deliverableType, RouterType type)
	{
		Log.out(Tag.ROUTER_INIT, "Loaded %s: %s", type, RNSUtils.getPlainName(deliverableType));

		List<Class<?>> delta = new ArrayList<Class<?>>();
		Class<?> observedAction = deliverableType;
		DeliverableType entryPoint = null;
		while (observedAction != null)
		{
			DeliverableType existingType = DeliverableType.ALL.get(observedAction.getName());
			if (existingType != null)
			{
				entryPoint = existingType;
				break;
			}
			delta.add(observedAction);
			observedAction = observedAction.getSuperclass();
		}

		if (entryPoint == null)
		{
			throw new RuntimeException("Unable to find the entry point for observed action type " + deliverableType.getName());
		}

		DeliverableType parent = entryPoint;
		for (int i = delta.size() - 1; i >= 0; i--)
		{
			parent = parent.createSubtype(delta.get(i).getName());

			for (HandlerType handler : handlerTypes)
			{
				handler.assignDeliverable(parent, type);
			}
		}
	}
}
