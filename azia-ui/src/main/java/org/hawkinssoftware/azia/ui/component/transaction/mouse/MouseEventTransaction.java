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
package org.hawkinssoftware.azia.ui.component.transaction.mouse;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
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

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseEventTransaction implements UserInterfaceTransaction.Iterative
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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

	@Override
	public void addActionsOn(List<UserInterfaceDirective> actions, UserInterfaceActor actor)
	{
		for (int i = transaction.size() - 1; i >= 0; i--)
		{
			UserInterfaceDirective action = transaction.get(i);
			if (action.getActor() == actor)
			{
				actions.add(action);
			}
		}
	}

	// TODO: should this maybe not be specific to the TopTile, but the generic BoundedEntity.LayoutRoot?
	/**
	 * @JTourBusStop 3.1, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction initiated:
	 * 
	 *               The transaction begins by posting a notification to the topTile.
	 */
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
		/**
		 * @JTourBusStop 4.12, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction
		 *               propagated through client components:
		 * 
		 *               The unvisited Forward instances are visited one by one.
		 */
		if (!unvisitedForwards.isEmpty())
		{
			for (MouseAware forward : unvisitedForwards)
			{
				session.postAction(forward.getActor(), notification);
			}
			unvisitedForwards.clear();
		}
		else
		{
			/**
			 * @JTourBusStop 5, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction
			 *               concludes:
			 * 
			 *               After all Forward instances have been visited by this MouseEventTransaction, a set of
			 *               UserInterfaceDirectives have been collected in this.transaction, but no field values
			 *               throughout the user interface components have yet been modified. At this point, the
			 *               transaction consists of a formula for executing the client responses to its initial
			 *               proposition, which was a mouse state change. A few conclusory steps are taken here, which
			 *               may result in additional directives added to the transaction, and then...
			 */
			mouseState.prepareFrameConclusion(notification, conclusion);

			for (MouseAware satellitePositionForward : conclusion.satellitePositionForwards)
			{
				// TODO: could use a separate notification for satellite forwards
				session.postAction(satellitePositionForward.getActor(), notification);
			}

			Termination termination = new Termination();
			for (MouseAware terminationForward : conclusion.terminations)
			{
				session.postAction(terminationForward.getActor(), termination);
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

	/**
	 * @JTourBusStop 4.11, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction propagated
	 *               through client components:
	 * 
	 *               The Forward instance arrives back at the MouseEventTransaction, and is appended to the list of
	 *               unvisitedForwards, which must all be visited before the transaction can be considered complete.
	 */
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

	/**
	 * @JTourBusStop 5.1, Virtual encapsulation in an Azia user interface transaction, MouseEventTransaction concludes:
	 * 
	 *               ...the transaction engine invokes commitTransaction(), which iteratively commits all the collected
	 *               directives. Each commit applies the directive's intended change of field value to the user
	 *               interface component instance assigned to it. 
	 */
	@Override
	public void commitTransaction()
	{
		for (UserInterfaceDirective action : transaction)
		{
			action.commit();
		}
	}

	@Override
	public void transactionRolledBack()
	{
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}
}
