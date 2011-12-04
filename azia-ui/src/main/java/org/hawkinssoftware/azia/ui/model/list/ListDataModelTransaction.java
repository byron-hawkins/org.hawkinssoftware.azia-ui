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
package org.hawkinssoftware.azia.ui.model.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.AbstractDataAction;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModificationAction;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.RemoveAction;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
public class ListDataModelTransaction implements UserInterfaceTransaction
{
	private final List<AbstractDataAction> appendingActions = new ArrayList<AbstractDataAction>();
	private final Map<RowAddress, AbstractDataAction> modificationsByRow = new LinkedHashMap<RowAddress, AbstractDataAction>();
	private final List<UserInterfaceDirective> finalActions = new ArrayList<UserInterfaceDirective>();
	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();
	private Session session;

	private final List<ListDataModel.Session> modelSessions = new ArrayList<ListDataModel.Session>();

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	public void addModelSession(ListDataModel.Session modelSession)
	{
		modelSessions.add(modelSession);
	}

	void addDataAction(AbstractDataAction dataAction)
	{
		if (dataAction instanceof ModificationAction)
		{
			ModificationAction dataModification = (ModificationAction) dataAction;
			ModificationAction existingAction = (ModificationAction) modificationsByRow.get(dataModification.getAddress());
			if (existingAction != null)
			{
				if ((existingAction instanceof RemoveAction) && !(dataModification instanceof RemoveAction))
				{
					throw new IllegalArgumentException("Attempt to modify a row that is already being removed in this transaction: "
							+ existingAction.getAddress());
				}
			}
			modificationsByRow.put(dataModification.getAddress(), dataAction);
		}
		else
		{
			appendingActions.add(dataAction);
		}

		session.postAction(dataAction);
	}

	void addFinalAction(UserInterfaceDirective action)
	{
		finalActions.add(action);
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
	}

	@Override
	public void addActionsOn(List<UserInterfaceDirective> actions, UserInterfaceActor actor)
	{
		List<UserInterfaceDirective> reverseActions = new ArrayList<UserInterfaceDirective>();
		reverseActions.addAll(modificationsByRow.values());
		reverseActions.addAll(appendingActions);
		reverseActions.addAll(finalActions);
		reverseActions.addAll(transaction);
		Collections.reverse(reverseActions);
		actions.addAll(reverseActions);
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
			session.postAction(action);
			transaction.add(action);
		}
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
	}

	@Override
	public void commitTransaction()
	{
		for (UserInterfaceDirective action : modificationsByRow.values())
		{
			action.commit();
		}
		for (UserInterfaceDirective action : appendingActions)
		{
			action.commit();
		}
		for (UserInterfaceDirective action : finalActions)
		{
			action.commit();
		}
		for (UserInterfaceDirective action : transaction)
		{
			action.commit();
		}

		closeModelSessions();
	}
	
	@Override
	public void transactionRolledBack()
	{
		closeModelSessions();
	}
	
	private void closeModelSessions()
	{
		for (ListDataModel.Session modelSession : modelSessions)
		{
			modelSession.close();
		}
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty() && modificationsByRow.isEmpty() && appendingActions.isEmpty() && finalActions.isEmpty();
	}
}
