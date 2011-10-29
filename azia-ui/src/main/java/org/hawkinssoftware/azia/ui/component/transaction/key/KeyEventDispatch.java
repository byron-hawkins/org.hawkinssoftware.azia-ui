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
package org.hawkinssoftware.azia.ui.component.transaction.key;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.ui.component.AbstractEventDispatch;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class KeyEventDispatch extends AbstractEventDispatch
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class KeyEventDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final KeyEventDomain INSTANCE = new KeyEventDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = KeyEventDomain.class)
	private class InitiateKeyEventTransaction extends UserInterfaceTask
	{
		private KeyboardInputEvent event;

		void setKeyEvent(KeyboardInputEvent event)
		{
			this.event = event;
		}

		@Override
		protected boolean execute()
		{
			try
			{
				KeyEventTransaction transaction = getTransaction(KeyEventTransaction.class);
				transaction.assemble(event);
				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				t.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class InitializationTask extends InstantiationTask.StandaloneInstantiationTask
	{
		public InitializationTask()
		{
			super(SynchronizationRole.AUTONOMOUS, KeyEventDispatch.class.getSimpleName());
		}

		@Override
		protected void executeInTransaction()
		{
			INSTANCE = new KeyEventDispatch();
		}
	}

	public static KeyEventDispatch getInstance()
	{
		return INSTANCE;
	}

	public static void initialize()
	{
		new InitializationTask().start();
	}

	protected static KeyEventDispatch INSTANCE;

	private final InitiateKeyEventTransaction task = new InitiateKeyEventTransaction();

	public void keyEvent(KeyboardInputEvent event)
	{
		task.setKeyEvent(event);

		try
		{
			TransactionRegistry.executeTask(task);
		}
		catch (ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to execute the key event task.");
		}
	}
}
