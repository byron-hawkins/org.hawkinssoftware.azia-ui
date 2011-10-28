package org.hawkinssoftware.azia.ui.component.transaction.window;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;

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
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}
}
