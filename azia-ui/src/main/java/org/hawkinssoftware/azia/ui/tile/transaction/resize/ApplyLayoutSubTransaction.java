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
package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.InstallNewLayoutDirective;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.ModifyLayoutTransaction;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
public class ApplyLayoutSubTransaction<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractApplyLayoutTransaction
{
	
	/**
	 * The listener interface for receiving transactionRegistry events. The class that is interested in processing a
	 * transactionRegistry event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addTransactionRegistryListener<code> method. When
	 * the transactionRegistry event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see TransactionRegistryEvent
	 */
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
