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
package org.hawkinssoftware.azia.ui.paint.basic.cell;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter.CellRepaintRequest;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter.RowVisibilityType;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomRequest;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class })
public class ListModelScrollableContentPainter implements UserInterfaceHandler, UserInterfaceActorDelegate, CompositionElement.Initializing
{
	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private CellViewportComposite<ListModelPainter> viewport;

	private ListDataModel model;
	private CellStamp.Factory stampFactory;

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		viewport = (CellViewportComposite<ListModelPainter>) CompositionRegistry.getComposite(CellViewportComposite.class);
		viewport.getComponent().installHandler(this);

		model = CompositionRegistry.getService(ListDataModel.class);
		stampFactory = CompositionRegistry.getService(CellStamp.Factory.class);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent();
	}

	<DataType> void paint()
	{
		Canvas c = Canvas.get();

		CellStamp<DataType> stamp;
		int y = 0;
		for (int i = 0; i < model.getRowCount(Section.SCROLLABLE); i++)
		{
			RowAddress address = viewport.createAddress(i, Section.SCROLLABLE);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			stamp = stampFactory.getStamp(address, datum);

			Axis.Span stampSpan = new Axis.Span(Axis.V, y, stamp.getSpan(Axis.V, datum));
			if (viewport.getCellPainter().isRowVisible(stampSpan, RowVisibilityType.ACKNOWLEDGE_PARTIAL))
			{
				// WIP: BG such as highlight needs to paint outside this inset I just pushed
				c.pushBounds(ListModelPainter.CELL_TEXT_INSET, y, viewport.getBounds().width, stampSpan.span);
				stamp.paint(c, address, datum);
				c.popBounds();
			}

			y += stampSpan.span;
		}
	}

	RepaintAtomRequest createRepaintRequest(int row)
	{
		return new Repaint<Object>(row);
	}

	// RevalidateWidthNotification to self
	<DataType> int getContentHeight()
	{
		int height = viewport.getCellPainter().staticContent.northSpan;
		for (int i = 0; i < model.getRowCount(Section.SCROLLABLE); i++)
		{
			RowAddress address = viewport.createAddress(i, Section.SCROLLABLE);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
			height += stamp.getSpan(Axis.V, datum);
		}
		return height;
	}

	private <DataType> int calculateRowTop(int row)
	{
		int position = 0;
		for (int i = 0; i < row; i++)
		{
			RowAddress address = viewport.createAddress(i, Section.SCROLLABLE);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
			position += stamp.getSpan(Axis.V, datum);
		}
		return position;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class })
	private class Repaint<DataType> extends CellRepaintRequest
	{
		private final RowAddress address;
		private final int yTop;

		Repaint(int row)
		{
			super();
			yTop = calculateRowTop(row);
			address = viewport.createAddress(row, Section.SCROLLABLE);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void paint()
		{
			Canvas c = Canvas.get();

			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);

			c.pushBounds(viewport.getCellPainter().staticContent.getScrollableBounds());

			c.pushBoundsPosition(0, -viewport.getComponent().yViewport());
			c.pushBounds(ListModelPainter.CELL_TEXT_INSET, yTop, viewport.getBounds().width, stamp.getSpan(Axis.V, datum));
			c.pushBoundsPosition(-viewport.getComponent().xViewport(), 0);

			c.pushColor(Color.white);
			c.g.fillRect(0, 0, viewport.getBounds().width, c.span().height);

			stamp.paint(c, address, datum);
		}
	}
}
