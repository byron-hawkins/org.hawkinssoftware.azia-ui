package org.hawkinssoftware.azia.ui.tile.transaction.modify;

import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.ui.component.DesktopContainer;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.ApplyLayoutTransaction;

public class UpdateLayoutHandler<KeyType extends LayoutEntity.Key<KeyType>> implements UserInterfaceHandler
{
	private final KeyType tileKey;
	private final DesktopContainer<KeyType> window;

	private final UpdateLayoutTask updateTask = new UpdateLayoutTask();

	@SuppressWarnings("unchecked")
	public UpdateLayoutHandler(KeyType tileKey)
	{
		this.tileKey = tileKey;

		window = (DesktopContainer<KeyType>) CompositionRegistry.getWindow(this);
	}

	protected void executeUpdate() throws ConcurrentAccessException
	{
		updateTask.start();
	}

	private class UpdateLayoutTask extends UserInterfaceTask
	{
		@Override
		protected boolean execute()
		{
			ApplyLayoutTransaction transaction = getTransaction(ApplyLayoutTransaction.class);
			transaction.addRegion(window.getLayoutEntity(tileKey));
			transaction.beginAssembly();

			return true;
		}
	}
}
