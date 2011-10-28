package org.hawkinssoftware.azia.ui.component.composition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.moa.ExecutionPath;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.RNSUtils;

@DomainRole.Join(membership = CompositionRegistry.CompositionInitializationDomain.class)
public final class CompositionRegistry
{
	@DomainRole.Join(membership = CompositionInitializationDomain.class)
	private static class Session
	{
		private final Session parentSession;

		private final Set<CompositionElement> elements = new HashSet<CompositionElement>();

		private final Class<? extends AbstractComposite<?, ?>> compositeType;
		private AbstractComposite<?, ?> composite = null;

		Session(Session parentSession, Class<? extends AbstractComposite<?, ?>> compositeType)
		{
			this.parentSession = parentSession;
			this.compositeType = compositeType;
		}

		@SuppressWarnings("unchecked")
		Session(Session parentSession, AbstractComposite<?, ?> composite)
		{
			this.parentSession = parentSession;
			this.composite = composite;
			this.compositeType = (Class<? extends AbstractComposite<?, ?>>) composite.getClass();
		}

		void register(CompositionElement element)
		{
			if (compositeType.isAssignableFrom(element.getClass()))
			{
				composite = (AbstractComposite<?, ?>) element;
				if (parentSession != null)
				{
					parentSession.elements.add(composite);
				}
			}
			else
			{
				elements.add(element);
			} 
		}
	}

	// TODO: when I change the hierarchy of a domain, every domain usage instance needs to be analyzed. Currently it
	// only checks the domain references, not all the implications of contraints having the domain in them.
	@VisibilityConstraint(extendedTypes = { CompositionRegistry.class, Composition.class, CompositionElement.class }, packages = VisibilityConstraint.MY_PACKAGE)
	public static class CompositionInitializationDomain extends AssemblyDomain
	{
		@DomainRole.Instance
		public static final CompositionInitializationDomain INSTANCE = new CompositionInitializationDomain();
	}

	@DomainRole.Join(membership = CompositionInitializationDomain.class)
	private static class SessionStack implements Iterable<Session>
	{
		private final List<Session> activeSessions = new ArrayList<Session>();
		private final List<Composition> closedSessions = new ArrayList<Composition>();

		private RepaintDirective.Host repaintHost;

		void push(Session session)
		{
			activeSessions.add(session);
		}

		Session peek()
		{
			return activeSessions.get(activeSessions.size() - 1);
		}

		boolean isEmpty()
		{
			return activeSessions.isEmpty();
		}

		Session pop()
		{
			return activeSessions.remove(activeSessions.size() - 1);
		}

		void close(Composition composition)
		{
			closedSessions.add(composition);
		}

		public Iterator<Session> iterator()
		{
			return new SessionIterator();
		}

		void setRepaintHost(RepaintDirective.Host repaintHost)
		{
			this.repaintHost = repaintHost;
		}

		@DomainRole.Join(membership = CompositionInitializationDomain.class)
		private class SessionIterator implements Iterator<Session>
		{
			private int index = activeSessions.size() - 1;

			@Override
			public boolean hasNext()
			{
				return index > 0;
			}

			@Override
			public Session next()
			{
				return activeSessions.get(index--);
			}

			@Override
			public void remove()
			{
				activeSessions.remove(index);
				if (index >= activeSessions.size())
				{
					index = activeSessions.size() - 1;
				}
			}
		}

	}

	private static final ThreadLocal<SessionStack> SESSIONS = new ThreadLocal<SessionStack>() {
		@Override
		protected SessionStack initialValue()
		{
			return new SessionStack();
		}
	};

	@SuppressWarnings("unchecked")
	@InvocationConstraint(domains = CompositionInitializationDomain.class)
	public static <CompositeType extends AbstractComposite<?, ?>> CompositeType getComposite(Class<CompositeType> compositeType)
	{
		CompositionElement element = ExecutionPath.getMostRecentCaller(CompositionElement.class);
		if (element == null)
		{
			throw new IllegalStateException("Attempt to find the containing instance of " + compositeType.getName()
					+ " with no CompositionElement on the call stack.");
		}

		AbstractComposite<?, ?> composite = getComposite(element);
		while (composite != null)
		{
			if (compositeType.isAssignableFrom(composite.getClass()))
			{
				return (CompositeType) composite;
			}
			else
			{
				composite = getComposite(composite);
			}
		}
		throw new IllegalArgumentException("Composite " + RNSUtils.getPlainName(compositeType) + " for element " + RNSUtils.getPlainName(element.getClass())
				+ " could not be found in the composition registry.");
	}

	// TODO: might want to flatten the recursion
	private static AbstractComposite<?, ?> getComposite(CompositionElement element)
	{
		SessionStack sessions = SESSIONS.get();
		if (!sessions.isEmpty())
		{
			if (sessions.peek().composite != null)
			{
				return sessions.peek().composite;
			}
		}

		for (Composition composition : INSTANCE.compositions.values())
		{
			if (composition.elements.contains(element))
			{
				return composition.composite;
			}
		}
		return null;
	}

	public static <ServiceType> ServiceType getService(Class<ServiceType> serviceType)
	{
		CompositionElement element = ExecutionPath.getMostRecentCaller(CompositionElement.class);
		if (element == null)
		{
			throw new IllegalStateException("Attempt to find composite service " + serviceType.getName() + " with no CompositionElement on the call stack.");
		}

		AbstractComposite<?, ?> composite = getComposite(element);
		while (composite != null)
		{
			ServiceType service = composite.getService(serviceType);
			if (service != null)
			{
				return service;
			}
			composite = getComposite(composite);
		}
		throw new IllegalArgumentException("Service " + serviceType.getName() + " is not provided by any composite in the hierarchy of "
				+ element.getClass().getName());
	}

	public static RepaintDirective.Host getRepaintHost(CompositionElement element)
	{
		for (Composition composition : INSTANCE.compositions.values())
		{
			if (composition.elements.contains(element))
			{
				return composition.repaintHost;
			}
		}
		return null;
	}

	public static void beginComposition(Class<? extends AbstractComposite<?, ?>> compositeType)
	{
		SessionStack sessions = SESSIONS.get();
		Session session;
		if (sessions.isEmpty())
		{
			session = new Session(null, compositeType);
		}
		else
		{
			session = new Session(sessions.peek(), compositeType);
		}
		sessions.push(session);
	}

	public static void endComposition()
	{
		SessionStack sessions = SESSIONS.get();
		if (sessions.peek().composite == null)
		{
			throw new IllegalStateException("Attempt to end a composition session without instantiating the composite "
					+ sessions.peek().compositeType.getName());
		}
		INSTANCE.compose(sessions);
	}

	public static void registerRepaintHost(RepaintDirective.Host repaintHost)
	{
		SessionStack sessions = SESSIONS.get();
		if (sessions.isEmpty())
		{
			throw new IllegalStateException("Attempt to register a repaint host with no composition session active.");
		}
		sessions.repaintHost = repaintHost;
	}

	static void register(CompositionElement element)
	{
		SessionStack sessions = SESSIONS.get();
		if (sessions.isEmpty())
		{
			PendingRegistration registration = new PendingRegistration(element);
			CompositionElement instantiator = registration.instantiator;
			while (instantiator != null)
			{
				for (Composition composition : INSTANCE.compositions.values())
				{
					if (composition.elements.contains(instantiator))
					{
						composition.add(registration.element);
						return;
					}
				}

				Log.out(Tag.NIT, "Seeking another instantiator for %s because instantiator %s is not registered.", element.getClass().getSimpleName(),
						instantiator.getClass().getSimpleName());

				instantiator = ExecutionPath.getPriorCaller(CompositionElement.class, instantiator);
			}

			Log.out(Tag.DEBUG, "Skipping registration of %s because no session is active and no instantiator could be found.", element.getClass()
					.getSimpleName());
		}
		else
		{
			sessions.peek().register(element);
		}
	}

	private static final CompositionRegistry INSTANCE = new CompositionRegistry();

	private final Map<AbstractComposite<?, ?>, Composition> compositions = new HashMap<AbstractComposite<?, ?>, Composition>();

	private void compose(SessionStack sessions)
	{
		Session session = sessions.pop();
		Composition composition = compositions.get(session.composite);
		if (composition == null)
		{
			composition = new Composition(session.composite, session.elements);
			compositions.put(session.composite, composition);

			if (sessions.isEmpty())
			{
				composition.notifyCompositionCompleted();
				composition.repaintHost = sessions.repaintHost;
				for (int i = sessions.closedSessions.size() - 1; i >= 0; i--)
				{
					sessions.closedSessions.get(i).notifyCompositionCompleted();
					sessions.closedSessions.get(i).repaintHost = sessions.repaintHost;
				}
				sessions.closedSessions.clear();
			}
			else
			{
				sessions.close(composition);
			}
		}
		else
		{
			for (CompositionElement element : session.elements)
			{
				composition.add(element);
			}
		}
	}

	@DomainRole.Join(membership = { CompositionInitializationDomain.class })
	private class Composition
	{
		private final AbstractComposite<?, ?> composite;
		private final Set<CompositionElement> elements;
		private final List<CompositionElement> notificationList = new ArrayList<CompositionElement>();
		private RepaintDirective.Host repaintHost;

		Composition(AbstractComposite<?, ?> composite, Set<CompositionElement> elements)
		{
			this.composite = composite;
			this.elements = elements;
		}

		void notifyCompositionCompleted()
		{
			notificationList.addAll(elements);
			for (CompositionElement element : notificationList)
			{
				if (element instanceof CompositionElement.Initializing)
				{
					((CompositionElement.Initializing) element).compositionCompleted();
				}
			}
		}

		void add(CompositionElement element)
		{
			elements.add(element);

			if (element instanceof CompositionElement.Initializing)
			{
				((CompositionElement.Initializing) element).compositionCompleted();
			}
		}
	}

	private static class PendingRegistration
	{
		final CompositionElement element;
		final CompositionElement instantiator;

		PendingRegistration(CompositionElement element)
		{
			this.element = element;

			CompositionElement foundInstantiator = ExecutionPath.getMostRecentCaller(CompositionElement.class);
			if (foundInstantiator == element)
			{
				foundInstantiator = ExecutionPath.getPriorCaller(CompositionElement.class, foundInstantiator);
			}
			instantiator = foundInstantiator;
		}
	}
}
