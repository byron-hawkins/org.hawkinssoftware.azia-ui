package org.hawkinssoftware.azia.ui.model.list;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
public class ListDataModelTransaction implements UserInterfaceTransaction
{
	private final List<AbstractDataAction> appendingActions = new ArrayList<AbstractDataAction>();
	private final Map<RowAddress, AbstractDataAction> modificationsByRow = new LinkedHashMap<RowAddress, AbstractDataAction>();
	private final List<UserInterfaceDirective> finalActions = new ArrayList<UserInterfaceDirective>();
	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();
	private Session session;

	@Override
	public void setSession(Session session)
	{
		this.session = session;
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
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty() && modificationsByRow.isEmpty() && appendingActions.isEmpty() && finalActions.isEmpty();
	}
}
