package org.hawkinssoftware.azia.ui.component.transaction.key;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;

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
		return false;
	}
}
