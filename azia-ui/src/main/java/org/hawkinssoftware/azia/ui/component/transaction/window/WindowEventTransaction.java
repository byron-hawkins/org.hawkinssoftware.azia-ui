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
package org.hawkinssoftware.azia.ui.component.transaction.window;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class WindowEventTransaction implements UserInterfaceTransaction
{
	private Session session;

	private List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	public void assemble(UserInterfaceDirective initialCommand)
	{
		transaction.add(initialCommand);
		session.postAction(initialCommand);
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
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
		for (UserInterfaceNotification note : notifications)
		{
			session.postNotification(note);
		}
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
	public void transactionRolledBack()
	{
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}
}
