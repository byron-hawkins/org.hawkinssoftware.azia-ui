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
		return viewport.getComponent().getActor();
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
	 * @JTourBusStop 1, Stack-based properties for java.awt.Graphics, A repaint request for a single list cell
	 *               especially benefits from the property stack:
	 * 
	 *               This repaint request specifies an individual cell, which may be located anywhere in the scroll pane
	 *               and may have any kind of content inside it. The properties of the graphics object, such as its clip
	 *               bounds, color and font, must be modified many times along the execution path of the paint() method.
	 *               Class java.awt.Graphics applies each change permanently, but in almost all cases, an application
	 *               method only intends for its property changes to have effect until it returns. Resetting the
	 *               Graphics properties on method exit can be extremely complicated when multiple property changes
	 *               occur throughout the method, and for paint() methods that occur in a long change of delegation, it
	 *               easily becomes unclear what properties should be expected by each method in the chain. In the Azia
	 *               class Canvas, property changes are pushed onto a stack and automatically popped on method exit,
	 *               relieving the client code of all these responsibilities and complications.
	 * 
	 * @param <DataType>
	 *            the type of data contained in the cell
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
			/**
			 * @JTourBusStop 2, Stack-based properties for java.awt.Graphics, Obtaining the canvas automatically creates
			 *               a stack frame:
			 * 
			 *               Client code need not be concerned about when to create a stack frame for the Canvas. The
			 *               only way to get a Canvas instance is by calling Canvas.get(), and this method automatically
			 *               creates a stack frame and associates it with the method (if none has yet been associated).
			 */
			Canvas c = Canvas.get();

			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);

			/**
			 * @JTourBusStop 3, Stack-based properties for java.awt.Graphics, Changes to the clip bounds are exclusively
			 *               narrowing:
			 * 
			 *               Bounds changes in the Graphics object are absolute, allowing a delegated paint() method to
			 *               expand the bounds. This is almost never necessary, and when it occurs, most often it is by
			 *               arithmetic error in the client code. Pushing a bounds change to the Canvas exclusively
			 *               narrows the clip, so each paint() method need only concern itself with the clip bounds that
			 *               are logically meaningful within its context. If any paintable areas were clipped off by a
			 *               previous method on the call stack, those areas will remain clipped off no matter what the
			 *               current method requests. Here, the clip is set to the scrollable region of the scroll pane,
			 *               guaranteeing that no component will accidentally paint on the scroll bars and beyond.
			 * 
			 *               each bounds frame represents something meaningful about the visual construct; here the
			 *               visible area of the scroll pane
			 */
			c.pushBounds(viewport.getCellPainter().staticContent.getScrollableBounds());

			/**
			 * @JTourBusStop 4, Stack-based properties for java.awt.Graphics, Changes to the clip bounds occur in
			 *               logical units to avoid messy arithmetic::
			 * 
			 *               The process of cumulative narrowing for clip bounds lends itself naturally to simple
			 *               arithmetic, because each set of logical bounds changes can simply be pushed in sequence. In
			 *               this sequence of translation and narrowing, the y position is relevant to the cell bounds,
			 *               but the x position is not. This requires the y position to be pushed before narrowing and
			 *               the x position after, which is easy to do and easy to read in these stack-based semantics.
			 */
			c.pushBoundsPosition(0, -viewport.getComponent().yViewport());
			c.pushBounds(ListModelPainter.CELL_TEXT_INSET, yTop, viewport.getBounds().width, stamp.getSpan(Axis.V, datum));
			c.pushBoundsPosition(-viewport.getComponent().xViewport(), 0);

			c.pushColor(Color.white);
			c.g.fillRect(0, 0, viewport.getBounds().width, c.span().height);

			/**
			 * @JTourBusStop 5, Stack-based properties for java.awt.Graphics, Delegate painting to the list cell stamp
			 *               with no risk of context corruption:
			 * 
			 *               At this point the clip bounds have been set correctly for the cell to be repainted. The
			 *               cell stamp is now free to paint the cell according to its best understanding, knowing that
			 *               it cannot accidentally paint outside the scrollable area, and that all its Graphics
			 *               property change will be popped off the stack on method return.
			 */
			stamp.paint(c, address, datum);
		}
	}
}
