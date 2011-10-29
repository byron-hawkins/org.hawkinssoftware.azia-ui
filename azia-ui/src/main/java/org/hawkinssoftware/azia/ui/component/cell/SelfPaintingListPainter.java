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

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellContentPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
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
public class SelfPaintingListPainter extends AbstractCellContentPainter
{
	public static final int PAD = 4;

	private final List<SelfPaintingListCell> cells = new ArrayList<SelfPaintingListCell>();

	private int width;
	private int height;

	@Override
	public int getScrollableContentSize(Axis axis)
	{
		switch (axis)
		{
			case H:
				return width;
			case V:
				return height;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public void paint()
	{
		Canvas c = Canvas.get();
		
//		c.pushBounds(new EnclosureBounds(0, 0, viewport.getBounds().width, viewport.getBounds().height));
		c.g.setColor(Color.white);
		c.g.fillRect(0, 0, c.span().width, c.span().height);

		Rectangle rBounds = new Rectangle(0, 0, c.size().width, c.size().height); // c.g.getClipBounds();
		rBounds.translate(viewport.getComponent().xViewport(), viewport.getComponent().yViewport());

		for (SelfPaintingListCell cell : cells)
		{
			if (cell.contentBounds.intersects(rBounds))
			{
				cell.paint();
			}
		}
	}

	public SelfPaintingListCell getCell(int row)
	{
		return cells.get(row);
	}

	public int getCellCount()
	{
		return cells.size();
	}

	public void addCell(final String data)
	{
		new InstantiationTask.Task(SynchronizationRole.DEPENDENT, getClass().getSimpleName()) {
			@Override
			protected void execute()
			{
				SelfPaintingListCell cell = new SelfPaintingListCell();
				cell.setData(data);
				cell.setContentLocation(0, height);
				cells.add(cell);

				height += cell.textBounds.height + PAD;
				width = Math.max(width, cell.textBounds.width);
			}
		}.start();
	}

	@Override
	public int getStaticContentSpan(Axis axis)
	{
		return 0;
	}

	@Override
	public MouseAware getMouseAwareCellHandle(EventPass event)
	{
		int yEvent = event.event().y() + viewport.getComponent().yViewport() - viewport.getBounds().getPosition(Axis.V);
		for (SelfPaintingListCell cell : cells)
		{
			if ((cell.contentBounds.y < yEvent) && ((yEvent - cell.contentBounds.y) < cell.contentBounds.height))
			{
				return cell;
			}
		}
		return null;
	}
}
