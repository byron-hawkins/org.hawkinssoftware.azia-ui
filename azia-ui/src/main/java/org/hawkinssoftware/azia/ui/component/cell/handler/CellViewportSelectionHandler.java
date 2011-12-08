package org.hawkinssoftware.azia.ui.component.cell.handler;

import org.hawkinssoftware.azia.core.action.GenericTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery.Property;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.cell.transaction.SetSelectedRowDirective;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.DataChangeNotification;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter.RowVisibilityType;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@DomainRole.Join(membership = { ModelListDomain.class, ScrollPaneViewportComposite.ScrollPaneViewportDomain.class, DisplayBoundsDomain.class })
public class CellViewportSelectionHandler implements UserInterfaceHandler, UserInterfaceActorDelegate, CompositionElement.Initializing,
		UserInterfaceActorPreview
{
	private ListDataModel model;
	private CellViewportComposite<ListModelPainter> viewport;

	@ValidateRead
	@ValidateWrite
	private int selectedRow = -1;

	private final UpdateSelectionTask updateTask = new UpdateSelectionTask();

	@Override
	public void compositionCompleted()
	{
		model = CompositionRegistry.getService(ListDataModel.class);
		viewport = CompositionRegistry.getComposite(ScrollPaneComposite.getGenericClass(ListModelPainter.class)).getViewport();

		viewport.installHandler(this);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent().getActor();
	}

	public int getSelectedRow()
	{
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(SelectedRowProperty.INSTANCE).getValue();
	}

	@Override
	public boolean affects(Property<?, ?> property)
	{
		return property.matches("getSelectedRow");
	}

	@SuppressWarnings("unchecked")
	public <T> T getPreview(UserInterfaceDirective action, T value)
	{
		return (T) (Integer) ((SetSelectedRowDirective) action).row;
	}

	public void dataChanging(DataChangeNotification change, PendingTransaction transaction)
	{
		// TODO: if rows are removed above the selected row, would be nice to walk the selection up the list

		try
		{
			updateTask.start();
		}
		catch (ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, "Failed to start the selection update task.");
		}
	}

	public void setSelectedRow(SetSelectedRowDirective selection)
	{
		selectedRow = selection.row;
	}

	public void selectedRowChanging(SetSelectedRowDirective.Notification notification, PendingTransaction transaction)
	{
		viewport.getCellPainter().repaint(viewport.createAddress(this.selectedRow, Section.SCROLLABLE));
		viewport.getCellPainter().repaint(viewport.createAddress(notification.row, Section.SCROLLABLE));

		if ((this.selectedRow >= 0) && viewport.getCellPainter().isRowVisible(this.selectedRow, RowVisibilityType.IGNORE_PARTIAL)
				&& !viewport.getCellPainter().isRowVisible(notification.row, RowVisibilityType.IGNORE_PARTIAL))
		{
			Axis.Span rowSpan = viewport.getCellPainter().getRowSpan(Section.SCROLLABLE, Axis.V, notification.row);
			int y;

			if (viewport.getComponent().yViewport() > rowSpan.position)
			{
				y = rowSpan.position;
			}
			else
			{
				y = rowSpan.position - (viewport.getBounds().height - rowSpan.span);
			}
			transaction.contribute(new MoveViewportOriginDirective(viewport.getComponent(), 0, y));
		}
	}

	@DomainRole.Join(membership = { ListDataModel.ModelListDomain.class, FlyweightCellDomain.class })
	private class UpdateSelectionTask extends UserInterfaceTask
	{
		@Override
		protected boolean execute()
		{
			int transactionSelectedRow = getSelectedRow();
			int transactionRowCount = model.getRowCount(Section.SCROLLABLE);
			if ((transactionSelectedRow < 0) && (transactionRowCount > 0))
			{
				transactionSelectedRow = 0;
			}
			if (transactionSelectedRow >= transactionRowCount)
			{
				if (transactionRowCount > 0)
				{
					transactionSelectedRow = model.getRowCount(Section.SCROLLABLE) - 1;
				}
				else
				{
					transactionSelectedRow = -1;
				}
			}
			Object transactionSelection = null;
			if (transactionSelectedRow >= 0)
			{
				transactionSelection = model.get(viewport.createAddress(transactionSelectedRow, Section.SCROLLABLE));
			}

			UserInterfaceTransactionQuery.setReadTransactionalChanges(false);
			int currentSelectedRow = getSelectedRow();
			Object currentSelection = null;
			if (currentSelectedRow >= 0)
			{
				currentSelection = model.get(viewport.createAddress(currentSelectedRow, Section.SCROLLABLE));
			}
			UserInterfaceTransactionQuery.setReadTransactionalChanges(true);

			Integer setSelection = null;
			if (transactionSelection == null)
			{
				if (currentSelection != null)
				{
					setSelection = -1;
				}
			}
			else if ((currentSelection == null) || (transactionSelection != currentSelection))
			{
				setSelection = transactionSelectedRow;
			}

			if (setSelection != null)
			{
				GenericTransaction transaction = getTransaction(GenericTransaction.class);
				transaction.addAction(new SetSelectedRowDirective(viewport.getComponent(), setSelection));
			}

			return true;
		}
	}

	private static class SelectedRowProperty extends UserInterfaceTransactionQuery.Property<CellViewportSelectionHandler, Integer>
	{
		private static final SelectedRowProperty INSTANCE = new SelectedRowProperty();

		private SelectedRowProperty()
		{
			super("getSelectedRow");
		}

		@Override
		protected Integer getCurrentValue(CellViewportSelectionHandler parentValue)
		{
			return parentValue.selectedRow;
		}
	}
}