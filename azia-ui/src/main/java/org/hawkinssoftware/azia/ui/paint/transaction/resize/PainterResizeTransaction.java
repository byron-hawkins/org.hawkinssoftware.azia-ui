package org.hawkinssoftware.azia.ui.paint.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentResizeTransaction;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class })
public class PainterResizeTransaction implements UserInterfaceTransaction
{
	public static class TransactionRegistryListener implements TransactionRegistry.Listener
	{
		public static final TransactionRegistryListener INSTANCE = new TransactionRegistryListener();

		@Override
		public Class<? extends UserInterfaceTransaction> transactionInitiated(Class<? extends UserInterfaceTransaction> transactionType)
		{
			if (transactionType == ComponentResizeTransaction.class)
			{
				return PainterResizeTransaction.class;
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
		if (notification instanceof ComponentBoundsChangeDirective.Notification)
		{
			ComponentBoundsChangeDirective.Notification resize = (ComponentBoundsChangeDirective.Notification) notification;
			AbstractComponent component = resize.getComponent();
			PainterSizeChangeDirective painterSizeChange = new PainterSizeChangeDirective(component, resize.getBoundsChange().width,
					resize.getBoundsChange().height);
			transaction.add(painterSizeChange);
			session.postAction(painterSizeChange);
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
