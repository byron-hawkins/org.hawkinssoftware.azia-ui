package org.hawkinssoftware.azia.ui.component.transaction.clipboard;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;
import org.hawkinssoftware.azia.input.clipboard.ClipboardMonitor;
import org.hawkinssoftware.azia.ui.component.AbstractEventDispatch;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

public class ClipboardEventDispatch extends AbstractEventDispatch implements ClipboardMonitor.Listener
{
	public static class ClipboardEventDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final ClipboardEventDomain INSTANCE = new ClipboardEventDomain();
	}

	@DomainRole.Join(membership = ClipboardEventDomain.class)
	private class InitiateClipboardEventTransaction extends UserInterfaceTask
	{
		private ClipboardChangeDirective event;

		void setEvent(ClipboardChangeDirective event)
		{
			this.event = event;
		}

		@Override
		protected boolean execute()
		{
			try
			{
				ClipboardEventTransaction transaction = getTransaction(ClipboardEventTransaction.class);
				transaction.assemble(event);
				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				Log.out(Tag.CRITICAL, t, "Failed to initiate a clipboard event transaction.");
				return false;
			}
		}
	}

	private static class InitializationTask extends InstantiationTask.StandaloneInstantiationTask
	{
		ClipboardEventDispatch instance;

		public InitializationTask()
		{
			super(SynchronizationRole.AUTONOMOUS, ClipboardEventDispatch.class.getSimpleName());
		}

		@Override
		protected void executeInTransaction()
		{
			INSTANCE = new ClipboardEventDispatch();
			ClipboardMonitor.getInstance().addListener(INSTANCE);
		}
	}

	public static ClipboardEventDispatch getInstance()
	{
		return INSTANCE;
	}

	public static void initialize()
	{
		new InitializationTask().start();
	}

	protected static ClipboardEventDispatch INSTANCE;

	private final InitiateClipboardEventTransaction task = new InitiateClipboardEventTransaction();

	@Override
	@InvocationConstraint(types = ClipboardMonitor.Poll.class)
	public void newClipboardContent(ClipboardContents contents)
	{
		task.setEvent(new ClipboardChangeDirective(contents));

		try
		{
			TransactionRegistry.executeTask(task);
		}
		catch (ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to execute the clipboard change event task.");
		}
	}
}
