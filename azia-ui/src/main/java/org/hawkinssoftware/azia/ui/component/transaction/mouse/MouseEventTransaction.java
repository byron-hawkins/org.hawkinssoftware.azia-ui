package org.hawkinssoftware.azia.ui.component.transaction.mouse;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.Contact;
import org.hawkinssoftware.azia.ui.input.MouseAware.Forward;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.azia.ui.input.MouseAware.State.FrameConclusion;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseEventTransaction implements UserInterfaceTransaction.Iterative
{
	private class Notification extends MouseAware.EventPass
	{
		@Override
		public boolean wasInContact(MouseAware entity)
		{
			return mouseState.wasInContact(entity);
		}

		@Override
		public MouseInputEvent event()
		{
			return mouseState.event();   
		}
	}

	private class Termination extends MouseAware.EventPassTermination
	{
		@Override
		public MouseInputEvent event()
		{
			return mouseState.event();
		}
	}

	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();
	private final List<MouseAware> unvisitedForwards = new ArrayList<MouseAware>();
	private final Notification notification = new Notification();
	private final FrameConclusion conclusion = new FrameConclusion();

	private Session session;

	private TopTile<?> topTile;
	private MouseAware.State mouseState;

	private boolean closed = false;

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	// TODO: should this maybe not be specific to the TopTile, but the generic BoundedEntity.LayoutRoot?
	@InvocationConstraint(domains = MouseEventDomain.class)
	public void assemble(TopTile<?> topTile, MouseAware.State mouseState)
	{
		closed = false;

		this.topTile = topTile;
		this.mouseState = mouseState;

		session.postAction(topTile, notification);
	}

	@Override
	public boolean hasMoreIterations()
	{
		return !closed;
	}

	@Override
	public void iterate()
	{
		if (!unvisitedForwards.isEmpty())
		{
			for (MouseAware forward : unvisitedForwards)
			{
				session.postAction(forward, notification);
			}
			unvisitedForwards.clear();
		}
		else
		{
			mouseState.prepareFrameConclusion(notification, conclusion);

			for (MouseAware satellitePositionForward : conclusion.satellitePositionForwards)
			{
				// TODO: could use a separate notification for satellite forwards
				session.postAction(satellitePositionForward, notification);
			}

			Termination termination = new Termination();
			for (MouseAware terminationForward : conclusion.terminations)
			{
				session.postAction(terminationForward, termination);
			}

			mouseState.closeFrame(notification);
			closed = true;
		}
	}

	@Override
	public void postDirectResponse(UserInterfaceDirective... actions)
	{
		for (UserInterfaceDirective action : actions)
		{
			transaction.add(action);
			session.postAction(action);
		}
	}

	@Override
	public void postDirectResponse(UserInterfaceNotification... notifications)
	{
		for (UserInterfaceNotification notification : notifications)
		{
			if (notification.getClass() == Contact.class)
			{
				mouseState.contact(((Contact) notification).getEntity());
			}
			else if (notification.getClass() == Forward.class)
			{
				unvisitedForwards.add(((Forward) notification).getEntity());
			}
			else
			{
				session.postNotification(notification);
			}
		}
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
	}

	@Override
	public void commitTransaction()
	{
		for (UserInterfaceDirective action : transaction)
		{
			action.commit();
		}
	}

	@Override
	public boolean isEmpty() 
	{
		return transaction.isEmpty();
	}
}
