package org.hawkinssoftware.azia.ui.component.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.AbstractApplyLayoutTransaction;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.TileBoundsChangeDirective.EnclosureEncountered;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ComponentResizeTransaction implements UserInterfaceTransaction
{
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
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}
}
