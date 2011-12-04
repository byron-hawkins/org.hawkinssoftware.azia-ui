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
package org.hawkinssoftware.azia.ui.component.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.AbstractApplyLayoutTransaction;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective.EnclosureEncountered;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ComponentResizeTransaction implements UserInterfaceTransaction
{
	/**
	 * The listener interface for receiving transactionRegistry events. The class that is interested in processing a
	 * transactionRegistry event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addTransactionRegistryListener<code> method. When
	 * the transactionRegistry event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see TransactionRegistryEvent
	 */
	public static class TransactionRegistryListener implements TransactionRegistry.Listener
	{
		public static final TransactionRegistryListener INSTANCE = new TransactionRegistryListener();

		@Override
		public Class<? extends UserInterfaceTransaction> transactionInitiated(Class<? extends UserInterfaceTransaction> transactionType)
		{
			if (AbstractApplyLayoutTransaction.class.isAssignableFrom(transactionType))
			{
				return ComponentResizeTransaction.class;
			}
			else
			{
				return null;
			}
		}
	}

	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();
	private Session session;

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
		if (AbstractApplyLayoutTransaction.class.isAssignableFrom(introducedTransactionType))
		{
			session.requestSpecificNotification(introducedTransactionType, EnclosureEncountered.class);
		}
	}

	@Override
	public void addActionsOn(List<UserInterfaceDirective> actions, UserInterfaceActor actor)
	{
		for (int i = transaction.size()-1; i >= 0; i--)
		{
			UserInterfaceDirective action = transaction.get(i);
			if (action.getActor() == actor)
			{
				actions.add(action);
			}
		}
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
		if (notification instanceof EnclosureEncountered)
		{
			EnclosureEncountered encounter = (EnclosureEncountered) notification;
			ComponentBoundsChangeDirective componentSizeChange = new ComponentBoundsChangeDirective(encounter.enclosure.getComponent(), encounter.getBounds());
			transaction.add(componentSizeChange);
			session.postAction(componentSizeChange);
		}
	}

	@Override
	public void postDirectResponse(UserInterfaceNotification... notifications)
	{
		for (UserInterfaceNotification notification : notifications)
		{
			session.postNotification(notification);
		}
	}

	@Override
	public void postDirectResponse(UserInterfaceDirective... actions)
	{
		for (UserInterfaceDirective action : actions)
		{
			transaction.add(action);
			if (action.getActor() != null)
			{
				session.postAction(action);
			}
		}
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
	public void transactionRolledBack()
	{
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}
}
