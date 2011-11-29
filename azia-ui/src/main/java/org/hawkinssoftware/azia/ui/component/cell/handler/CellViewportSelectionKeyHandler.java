package org.hawkinssoftware.azia.ui.component.cell.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.cell.transaction.SetSelectedRowDirective;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { ListDataModel.ModelListDomain.class, DisplayBoundsDomain.class })
public class CellViewportSelectionKeyHandler implements UserInterfaceHandler, CompositionElement.Initializing
{
	private CellViewportComposite<ListModelPainter> viewport;
	private ListDataModel model;
	private CellViewportSelectionHandler selection;

	public CellViewportSelectionKeyHandler()
	{
		KeyEventDispatch.getInstance().installHandler(this);
	}

	@Override
	public void compositionCompleted()
	{
		viewport = CompositionRegistry.getComposite(ScrollPaneComposite.getGenericClass(ListModelPainter.class)).getViewport();
		model = CompositionRegistry.getService(ListDataModel.class);
		selection = CompositionRegistry.getService(CellViewportSelectionHandler.class);
	}

	public void keyEvent(KeyboardInputNotification event, PendingTransaction transaction)
	{
		if (!(ComponentRegistry.getInstance().getFocusHandler().windowHasFocus(this) && (ComponentRegistry.getInstance().getFocusHandler().getFocusedComponent(this) == viewport)))
		{
			return;
		}

		if (event.event.state != KeyboardInputEvent.State.DOWN)
		{
			return;
		}

		int selectedRow = selection.getSelectedRow();

		switch (event.event.key)
		{
			case UP:
				if (selectedRow > 0)
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), selectedRow - 1));
				}
				break;
			case DOWN:
				if (selectedRow < (model.getRowCount(Section.SCROLLABLE) - 1))
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), selectedRow + 1));
				}
				break;
			case PAGE_UP:
				if (selectedRow > 0)
				{
					Axis.Span selectedRowSpan = viewport.getCellPainter().getRowSpan(Section.SCROLLABLE, Axis.V, selectedRow);
					int previousPageTop = Math.max(0, selectedRowSpan.position - viewport.getBounds().height);
					int previousPageRow = viewport.getCellPainter().getRowAtPosition(previousPageTop);
					if (viewport.getCellPainter().getRowSpan(Section.SCROLLABLE, Axis.V, previousPageRow).position < previousPageTop)
					{
						previousPageRow++;
					}
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), previousPageRow));
				}
				break;
			case PAGE_DOWN:
				if ((selectedRow >= 0) && selectedRow < (model.getRowCount(Section.SCROLLABLE) - 1))
				{
					Axis.Span selectedRowSpan = viewport.getCellPainter().getRowSpan(Section.SCROLLABLE, Axis.V, selectedRow);
					int nextPageTop = Math.min(viewport.getCellPainter().getScrollableContentSize(Axis.V), selectedRowSpan.position
							+ viewport.getBounds().height);
					int nextPageRow = viewport.getCellPainter().getRowAtPosition(nextPageTop);
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), Math.min(model.getRowCount(Section.SCROLLABLE), nextPageRow)));
				}
				break;
			case HOME:
				if (selectedRow > 0)
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), 0));
				}
				break;
			case END:
				if (selectedRow < (model.getRowCount(Section.SCROLLABLE) - 1))
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), (model.getRowCount(Section.SCROLLABLE) - 1)));
				}
		}
	}
}
