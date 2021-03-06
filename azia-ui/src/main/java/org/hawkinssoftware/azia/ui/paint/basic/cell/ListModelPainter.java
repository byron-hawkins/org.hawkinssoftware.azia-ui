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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp.CellPlugin;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp.CellPluginKey;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomRequest;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = { DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class })
public class ListModelPainter extends AbstractCellContentPainter implements CellStamp.RepaintHandler, ScrollPaneViewport.Painter,
		CompositionElement.Initializing
{
	public enum RowVisibilityType
	{
		ACKNOWLEDGE_PARTIAL,
		IGNORE_PARTIAL;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	public static abstract class CellRepaintRequest extends RepaintAtomRequest implements AggregatePainter.Atom
	{
		CellRepaintRequest()
		{
			super();
		}
	}

	static final int CELL_TEXT_INSET = 2;

	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private CellViewportComposite<ListModelPainter> viewport;

	private ListDataModel model;
	private CellStamp.Factory stampFactory;

	final ListModelStaticContentPainter staticContent;
	final ListModelScrollableContentPainter scrollableContent;

	private final MaximumWidthHandler widthHandler = new MaximumWidthHandler();

	private Color borderColor;

	@InvocationConstraint(domains = AssemblyDomain.class)
	public ListModelPainter()
	{
		staticContent = new ListModelStaticContentPainter();
		scrollableContent = new ListModelScrollableContentPainter();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		this.viewport = (CellViewportComposite<ListModelPainter>) CompositionRegistry.getComposite(CellViewportComposite.class);

		model = CompositionRegistry.getService(ListDataModel.class);
		stampFactory = CompositionRegistry.getService(CellStamp.Factory.class);
	}

	@Override
	public MouseAware getMouseAwareCellHandle(EventPass event)
	{
		return _getMouseAwareCellHandle(event);
	}

	private <DataType> MouseAware _getMouseAwareCellHandle(EventPass event)
	{
		int yEvent = event.event().y() - viewport.getBounds().y;
		if (yEvent < staticContent.northSpan)
		{
			int position = 0;
			for (int i = 0; i < model.getRowCount(Section.NORTH); i++)
			{
				RowAddress address = viewport.createAddress(i, Section.NORTH);
				@SuppressWarnings("unchecked")
				DataType datum = (DataType) model.get(address);
				CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
				int cellHeight = stamp.getSpan(Axis.V, datum);
				if ((position + cellHeight) >= yEvent)
				{
					return stamp.getMouseAwareCellHandle(address, datum, viewport.getBounds().getPosition(Axis.H),
							position + viewport.getBounds().getPosition(Axis.V));
				}
				position += cellHeight;
			}
		}
		else if (yEvent > staticContent.getSouthSectionTop())
		{
			int position = staticContent.getSouthSectionTop();
			for (int i = 0; i < model.getRowCount(Section.SOUTH); i++)
			{
				RowAddress address = viewport.createAddress(i, Section.SOUTH);
				@SuppressWarnings("unchecked")
				DataType datum = (DataType) model.get(address);
				CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
				int cellHeight = stamp.getSpan(Axis.V, datum);
				if ((position + cellHeight) >= yEvent)
				{
					return stamp.getMouseAwareCellHandle(address, datum, viewport.getBounds().getPosition(Axis.H),
							position + viewport.getBounds().getPosition(Axis.V));
				}
				position += cellHeight;
			}
		}
		else
		{
			yEvent += (viewport.getComponent().yViewport() - staticContent.northSpan);
			int position = 0;
			for (int i = 0; i < model.getRowCount(Section.SCROLLABLE); i++)
			{
				RowAddress address = viewport.createAddress(i, Section.SCROLLABLE);
				@SuppressWarnings("unchecked")
				DataType datum = (DataType) model.get(address);
				CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
				int cellHeight = stamp.getSpan(Axis.V, datum);
				if ((position + cellHeight) >= yEvent)
				{
					return stamp.getMouseAwareCellHandle(address, datum, viewport.getBounds().getPosition(Axis.H) - viewport.getComponent().xViewport(),
							position + viewport.getBounds().getPosition(Axis.V));
				}
				position += cellHeight;

				if (position >= (viewport.getComponent().yViewport() + staticContent.getSouthSectionTop()))
				{
					break;
				}
			}
		}
		return null;
	}

	public boolean isRowVisible(int row, RowVisibilityType visibilityType)
	{
		return isRowVisible(getRowSpan(Section.SCROLLABLE, Axis.V, row), visibilityType);
	}

	boolean isRowVisible(Axis.Span span, RowVisibilityType visibilityType)
	{
		int topIntersectingPosition = span.position;
		if (visibilityType == RowVisibilityType.ACKNOWLEDGE_PARTIAL)
		{
			topIntersectingPosition += span.span;
		}
		int bottomIntersectingPosition = span.position;
		if (visibilityType == RowVisibilityType.IGNORE_PARTIAL)
		{
			bottomIntersectingPosition += span.span;
		}

		if (topIntersectingPosition < viewport.getComponent().yViewport())
		{
			return false;
		}
		if ((bottomIntersectingPosition - viewport.getComponent().yViewport()) > staticContent.getSouthSectionTop())
		{
			return false;
		}
		return true;
	}

	public <DataType> Axis.Span getRowSpan(Section section, Axis axis, int row)
	{
		if ((row <= 0) || (model.getRowCount(section) <= row))
		{
			return new Axis.Span(axis, 0, 0);
		}

		RowAddress rowAddress = viewport.createAddress(row, section);
		@SuppressWarnings("unchecked")
		DataType rowDatum = (DataType) model.get(rowAddress);
		CellStamp<DataType> rowStamp = stampFactory.getStamp(rowAddress, rowDatum);

		int position = 0;
		int span = rowStamp.getSpan(axis, rowDatum);

		if (axis == Axis.V)
		{
			for (int i = 0; i < row; i++)
			{
				RowAddress address = viewport.createAddress(i, section);
				@SuppressWarnings("unchecked")
				DataType datum = (DataType) model.get(address);
				CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
				position += stamp.getSpan(Axis.V, datum);
			}
		}

		return new Axis.Span(axis, position, span);
	}

	public <DataType> int getRowAtPosition(int targetPosition)
	{
		if (targetPosition == 0)
		{
			return 0;
		}

		int row = 0;
		int position = 0;
		for (; row < model.getRowCount(Section.SCROLLABLE); row++)
		{
			RowAddress address = viewport.createAddress(row, Section.SCROLLABLE);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
			position += stamp.getSpan(Axis.V, datum);
			if (position >= targetPosition)
			{
				break;
			}
		}
		return row;
	}

	public <DataType> int getLastVisibleRow()
	{
		return Math.max(0, getRowAtPosition(viewport.getComponent().yViewport() + viewport.getBounds().height) - 1);
	}

	public void repaint(RowAddress address)
	{
		if (address.hasRow())
		{
			if (model.getRowCount(address.section) > address.row)
			{
				RepaintRequestManager.requestRepaint(createRepaintRequest(address));
			}
			// else this row is already gone, so there's no repainting to be done for it
		}
		else
		{
			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(viewport.getComponent()));
		}
	}

	@Override
	public RepaintAtomRequest createRepaintRequest(RowAddress address)
	{
		switch (address.section)
		{
			case SCROLLABLE:
				return scrollableContent.createRepaintRequest(address.row);
			default:
				return staticContent.createRepaintRequest(address);
		}
	}

	@Override
	public int getScrollableContentSize(Axis axis)
	{
		switch (axis)
		{
			case H:
				return widthHandler.getMaxWidth();
			case V:
				return scrollableContent.getContentHeight();
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public int getStaticContentSpan(Axis axis)
	{
		switch (axis)
		{
			case H:
				return widthHandler.getMaxWidth();
			case V:
				return staticContent.getContentHeight();
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public void paint()
	{
		Canvas c = Canvas.get();

		c.pushBoundsPosition(-viewport.getComponent().xViewport(), 0);
		staticContent.paint(c);
		c.popBounds();

		c.pushBounds(staticContent.getScrollableBounds());
		c.pushBoundsPosition(-viewport.getComponent().xViewport(), -viewport.getComponent().yViewport());
		scrollableContent.paint();
		c.popBounds();
	}

	public <PluginType extends CellPlugin> PluginType getCellPlugin(RowAddress address, CellPluginKey<PluginType> key)
	{
		return stampFactory.getStamp(address, model.get(address)).getCellPlugin(address, key);
	}
}
