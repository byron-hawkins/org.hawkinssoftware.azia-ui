package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.InstallNewLayoutDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.ModifyLayoutTransaction;

public class ApplyLayoutSubTransaction<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractApplyLayoutTransaction
{
	public static class TransactionRegistryListener implements TransactionRegistry.Listener
	{
		public static final TransactionRegistryListener INSTANCE = new TransactionRegistryListener();

		@Override
		public Class<? extends UserInterfaceTransaction> transactionInitiated(Class<? extends UserInterfaceTransaction> type)
		{
			if (type == ModifyLayoutTransaction.class)
			{
				return ApplyLayoutSubTransaction.class;
			}
			else
			{
				return null;
			}
		}
	}

	TopTile<KeyType> top = null;

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
		if (ModifyLayoutTransaction.class.isAssignableFrom(introducedTransactionType))
		{
			session.requestNotificationByTransactionType(introducedTransactionType);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
		if (notification instanceof InstallNewLayoutDirective.Notification)
		{
			top = (TopTile<KeyType>) ((InstallNewLayoutDirective<KeyType>.Notification) notification).getModification();
			TileBoundsChangeDirective initialChange = top.createBoundsChangeDirective();
			transaction.add(initialChange);
			session.postAction(initialChange);
		}
	}
}
