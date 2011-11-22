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
import java.awt.Rectangle;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter.CellRepaintRequest;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Size;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomRequest;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * Painting delegate for the immovable list items at the top and bottom of a list. This painter will also be used for
 * table column headers and footers when the flyweight table is built.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class })
public class ListModelStaticContentPainter implements UserInterfaceHandler, UserInterfaceActorDelegate, CompositionElement.Initializing
{
	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private CellViewportComposite<ListModelPainter> viewport;

	private ListDataModel model;
	private CellStamp.Factory stampFactory;

	int northSpan;
	int southSpan;

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

	public void dataChanging(ListDataModel.StaticDataChangeNotification dataChange, PendingTransaction transaction)
	{
		String text = dataChange.datum.toString();
		Size size = TextMetrics.INSTANCE.getSize(text, BoundsType.TEXT);

		switch (dataChange.type)
		{
			case ADD:
			{
				switch (dataChange.address.section)
				{
					case NORTH:
						northSpan += size.height;
						break;
					case SOUTH:
						southSpan += size.height;
						break;
					default:
						throw new IllegalArgumentException("Can't process actions for scrollable content here.");
				}
				break;
			}
			case REMOVE:
			{
				switch (dataChange.address.section)
				{
					case NORTH:
						northSpan -= size.height;
						break;
					case SOUTH:
						southSpan -= size.height;
						break;
					default:
						throw new IllegalArgumentException("Can't process actions for scrollable content here.");
				}
				break;
			}
			case REPLACE:
			{
				// TODO: adjust N/S span
				break;
			}
			default:
				throw new UnknownEnumConstantException(dataChange.type);
		}

		viewport.getCellPainter().repaint(dataChange.address);
	}

	@InvocationConstraint(domains = RenderingDomain.class)
	<DataType> void paint(Canvas c)
	{
		int y = 0;
		for (int i = 0; i < model.getRowCount(Section.NORTH); i++)
		{
			RowAddress address = viewport.createAddress(i, Section.NORTH);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);

			// WIP: BG such as highlight needs to paint outside this inset I just pushed
			c.pushBounds(ListModelPainter.CELL_TEXT_INSET, y, stamp.getSpan(Axis.H, datum), stamp.getSpan(Axis.V, datum));
			stamp.paint(c, address, datum);
			c.popBounds();

			y += stamp.getSpan(Axis.V, datum);
		}

		y = getSouthSectionTop();
		for (int i = 0; i < model.getRowCount(Section.SOUTH); i++)
		{
			RowAddress address = viewport.createAddress(i, Section.SOUTH);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);

			// WIP: BG such as highlight needs to paint outside this inset I just pushed
			c.pushBounds(ListModelPainter.CELL_TEXT_INSET, y, stamp.getSpan(Axis.H, datum), stamp.getSpan(Axis.V, datum));
			stamp.paint(c, address, datum);
			c.popBounds();

			y += stamp.getSpan(Axis.V, datum);
		}
	}

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	RepaintAtomRequest createRepaintRequest(RowAddress address)
	{
		return new Repaint<Object>(address);
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	EnclosureBounds trimForScrollableContent(Rectangle bounds)
	{
		int south = Math.min(bounds.y + bounds.height, getSouthSectionTop());
		int north = Math.max(bounds.y, northSpan);
		return new EnclosureBounds(bounds.x, north, bounds.width, south - north);
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	EnclosureBounds getScrollableBounds()
	{
		return new EnclosureBounds(0, northSpan, viewport.getBounds().width, getSouthSectionTop() - northSpan);
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	int getContentHeight()
	{
		return northSpan + southSpan;
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	int getSouthSectionTop()
	{
		return (viewport.getBounds().height - southSpan);
	}

	private <DataType> int calculateRowTop(RowAddress address)
	{
		int position = 0;
		if (address.section == Section.SOUTH)
		{
			position = getSouthSectionTop();
		}
		for (int i = 0; i < address.row; i++)
		{
			RowAddress iterationAddress = viewport.createAddress(i, address.section);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(iterationAddress);
			CellStamp<DataType> stamp = stampFactory.getStamp(iterationAddress, datum);
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
		private final DataType datum;
		private final CellStamp<DataType> stamp;
		private final EnclosureBounds paintBounds;

		@SuppressWarnings("unchecked")
		Repaint(RowAddress address)
		{
			this.address = address;
			yTop = calculateRowTop(address);
			datum = (DataType) model.get(address);
			stamp = stampFactory.getStamp(address, datum);
			paintBounds = new EnclosureBounds(0, yTop, viewport.getBounds().width, stamp.getSpan(Axis.V, datum));
		}

		@Override
		public void paint()
		{
			Canvas c = Canvas.get();

			// WIP: need to paint BG outside the inset
			c.pushBounds(ListModelPainter.CELL_TEXT_INSET, yTop, stamp.getSpan(Axis.H, datum), stamp.getSpan(Axis.V, datum));
			c.pushBoundsPosition(-viewport.getComponent().xViewport(), 0);

			c.pushColor(Color.white);
			c.g.fillRect(0, 0, c.size().width, c.size().height);
			stamp.paint(c, address, datum);
		}

		@Override
		public UserInterfaceActor getActor()
		{
			return viewport.getComponent();
		}
	}
}
