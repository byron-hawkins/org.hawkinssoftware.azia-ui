package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = TileLayoutDomain.class)
public abstract class AbstractApplyLayoutTransaction implements UserInterfaceTransaction
{ 
	protected final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();
	protected Session session;

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
		// no responses from this transaction
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
		// no responses from this transaction
	}

	@Override
	public void postDirectResponse(UserInterfaceDirective... actions)
	{
		for (UserInterfaceDirective action : actions)
		{
			transaction.add(action);
		}

		for (UserInterfaceDirective action : actions)
		{
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
