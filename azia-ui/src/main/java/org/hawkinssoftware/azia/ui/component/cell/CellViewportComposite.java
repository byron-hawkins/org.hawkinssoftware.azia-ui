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
package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellContentPainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellViewportPainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelCellViewport;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.UpdateLayoutHandler;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * Abstract base class for a <code>ScrollPane</code> viewport containing a vertical list of cells. Supports both
 * flyweight and concrete cell implementations. Note that all scroll pane viewports in Azia are content-specific; there
 * is no generic viewport holding a generic component, because viewports are always content-specific (see the phony
 * linkages between JTable and JScrollPane for confirmation).
 * 
 * @param <CellPainterType>
 *            Specifies the painter to render the cells in this viewport.
 * @author Byron Hawkins
 */
public class CellViewportComposite<CellPainterType extends AbstractCellContentPainter> extends ScrollPaneViewportComposite<CellViewport, CellViewportPainter>
		implements ListDataModel.ComponentContext
{
	public static class ScrollPaneAssembly extends ScrollPane.Assembly<CellViewportComposite<?>, ScrollPaneComposite<CellViewportComposite<?>>>
	{
		@SuppressWarnings("unchecked")
		public ScrollPaneAssembly()
		{
			super((Class<ScrollPaneComposite<CellViewportComposite<?>>>) (Class<?>) ScrollPaneComposite.class, new CellViewportComposite.Assembly());
		}
	}
	
	public static class Assembly extends CellViewport.Assembly<ListModelPainter, CellViewportComposite<ListModelPainter>>
	{
		@SuppressWarnings("unchecked")
		public Assembly()
		{
			super(ListModelCellViewport.class, (Class<CellViewportComposite<ListModelPainter>>) (Class<?>) CellViewportComposite.class);
		}

		@Override
		protected ListModelPainter createCellPainter()
		{
			return new ListModelPainter();
		}
	}
	
	@DomainRole.Join(membership = ModelListDomain.class)
	public static class UpdateHandler<KeyType extends LayoutEntity.Key<KeyType>> extends UpdateLayoutHandler<KeyType>
	{
		public UpdateHandler(KeyType tileKey)
		{
			super(tileKey);
		}

		public void dataChanging(ListDataModel.DataChangeNotification dataChange, PendingTransaction transaction)
		{
			try
			{
				executeUpdate();
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.DEBUG, e, "Failed to update the layout of a cell viewport after data change.");
			}
		}
	}

	private CellPainterType cellPainter;

	public CellViewportComposite(CellViewport component)
	{
		super(component);
	}

	public CellPainterType getCellPainter()
	{
		return cellPainter;
	}

	public void setCellPainter(CellPainterType cellPainter)
	{
		if (this.cellPainter != null)
		{
			uninstallService(this.cellPainter);
		}

		this.cellPainter = cellPainter;
		installService(cellPainter);
	}

	@Override
	public RowAddress createAddress(int row, Section section)
	{
		return new RowAddress(getComponent(), row, section);
	}
}
