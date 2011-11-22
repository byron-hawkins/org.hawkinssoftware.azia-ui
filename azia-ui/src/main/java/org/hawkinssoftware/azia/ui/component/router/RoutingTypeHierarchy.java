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

/**
 * Singleton hierarchy of <code>UserInterfaceDirective</code> and <code>UserInterfaceNotification</code> which maps each
 * instance to a set of compatible <code>AbstractComponentRouter</code>s and populates the
 * <code>InstrumentedRouterCache</code> accordingly. Every <code>UserInterfaceDirective</code> and
 * <code>UserInterfaceNotification</code> is mapped directly to a complete set of compatible routers, such that
 * <code>InstrumentedRouterCache</code> lookup never requires resolving implications or traversing.
 * 
 * @author Byron Hawkins
 */
class RoutingTypeHierarchy
{
	/**
	 * Represents one <code>UserInterfaceDirective</code> or <code>UserInterfaceNotification</code> in the hierarchy.
	 * 
	 * @author Byron Hawkins
	 */
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

	/**
	 * Represents a bytecode instrumented <code>AbstractComponentRouter</code> for a particular hierarchy of
	 * <code>UserInterfaceDirective</code> or <code>UserInterfaceNotification</code>.
	 * 
	 * @param <RouterImplementationType>
	 *            the particular kind of <code>AbstractComponentRouter</code>
	 * @author Byron Hawkins
	 */
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

	/**
	 * Represents a subtree in the hierarchy of <code>UserInterfaceDirective</code> or
	 * <code>UserInterfaceNotification</code>.
	 * 
	 * @param <RouterImplementationType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
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

	/**
	 * The set of subtrees of the <code>UserInterfaceDirective</code> or <code>UserInterfaceNotification</code>
	 * hierarchy which can be routed to a particular <code>UserInterfaceHandler</code>. For example, suppose a
	 * <code>ShapeHandler extends UserInterfaceHandler</code> has methods
	 * <code>addTrapezoid(TrapezoidAddNotification notification)</code> and
	 * <code>addQuadrilateral(QuadrilateralAddNotification notification)</code>. In this case, the
	 * <code>DeliverableSubtrees</code> will contain two overlapping entries: the subtree of
	 * <code>QuadrilateralAddNotification</code> and the subtree of <code>TrapezoidAddNotification</code>. When
	 * deliverables are assigned, each overlapping subtree is truncated at the root of the contained subtree, such that
	 * the most direct method is called for any deliverable type. In the example, this means every
	 * <code>QuadrilateralAddNotification</code> will be posted only to the <code>addQuadrilateral()</code> method
	 * unless it is more specifically a <code>TrapezoidAddNotification</code>, in which case the
	 * <code>addTrapezoid</code> method is used instead.
	 * 
	 * @param <RouterImplementationType>
	 *            <code>ComponentDirectiveRouter</code> or <code>ComponentNotificationRouter</code>
	 * @author Byron Hawkins
	 */
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

	/**
	 * Expanded representation of a single <code>UserInterfaceHandler</code>. In the initial construction, the entirety
	 * of the deliverable hierarchies--as presently known--will be assigned to the handler's routers. When a new
	 * deliverable is introduced into the JVM, it will be assigned to each handler's routers for which it is compatible.
	 * 
	 * @author Byron Hawkins
	 */
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
		TypeHierarchy observedType = deliverableType;
		DeliverableType entryPoint = null;
		while (observedType != null)
		{
			DeliverableType existingType = DeliverableType.ALL.get(observedType.qualifiedName);
			if (existingType != null)
			{
				entryPoint = existingType;
				break;
			}
			delta.add(observedType);
			observedType = observedType.supertype;
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
}
