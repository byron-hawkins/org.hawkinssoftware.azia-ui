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
package org.hawkinssoftware.azia.ui.component.transaction.key;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class KeyEventTransaction implements UserInterfaceTransaction
{
	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();

	private Session session;

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	public void assemble(KeyboardInputEvent event)
	{
		KeyboardInputNotification notification = new KeyboardInputNotification(event);
		session.postAction(KeyEventDispatch.getInstance(), notification);
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
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
			session.postNotification(notification);
		}
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
	}

	/**
	 * @JTourBusStop 4.12, ReCopyHandler participates in mouse and keyboard transactions, KeyEventTransaction commits:
	 * 
	 *               When the ReCopyCommand was contributed in response to the KeyboardInputNotification, it was routed
	 *               to this.transaction, which is a simple list of actions. Each is committed in sequence.
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
		return false;
	}
}
