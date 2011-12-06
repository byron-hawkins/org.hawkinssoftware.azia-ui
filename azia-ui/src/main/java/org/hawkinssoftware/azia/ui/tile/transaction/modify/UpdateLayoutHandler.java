package org.hawkinssoftware.azia.ui.tile.transaction.modify;

import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.ui.component.DesktopContainer;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.ApplyLayoutTransaction;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;

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

	@DomainRole.Join(membership = LayoutRegion.TileLayoutDomain.class)
	private class UpdateLayoutTask extends UserInterfaceTask
	{
		@Override
		protected boolean execute()
		{
			ApplyLayoutTransaction transaction = getTransaction(ApplyLayoutTransaction.class);

			// hack: need to read the transactional value of the window layout, not the actual value here
			LayoutRegion region = window.getLayoutEntity(tileKey);
			if (region != null)
			{
				transaction.addRegion(region);
				transaction.beginAssembly();

				// WIP: need to be able to do this:
				// RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(region));
			}
			else
			{
				Log.out(Tag.DEBUG, "Warning: can't update layout entity %s because it is not found in the window", tileKey);
			}

			return true;  
		}
	}
}
