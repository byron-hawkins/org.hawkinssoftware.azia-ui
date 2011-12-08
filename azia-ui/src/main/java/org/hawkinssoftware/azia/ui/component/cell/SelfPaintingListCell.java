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

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Size;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomRequest;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class SelfPaintingListCell extends VirtualComponent implements AggregatePainter.Atom, CompositionElement.Initializing
{
	EnclosureBounds contentBounds = EnclosureBounds.EMPTY;
	Size textBounds = Size.EMPTY;

	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private SelfPaintingListViewport viewport;

	private String data;
	private Color color = Color.black;

	public SelfPaintingListCell()
	{
		installHandler(new Handler());
	}

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		viewport = CompositionRegistry.getComposite(SelfPaintingListViewport.class);
	}

	public void paint()
	{
		Canvas c = Canvas.get();

		c.pushBoundsPosition(0, -viewport.getComponent().yViewport());
		c.pushBounds(contentBounds);
		c.pushBoundsPosition(-viewport.getComponent().xViewport(), 0);

		c.pushColor(Color.white);
		c.g.fillRect(0, 0, c.span().width, c.span().height);

		c.pushColor(color);
		c.g.drawString(data, 0, CellStamp.TEXT_BASELINE);
	}

	public EnclosureBounds getContentBounds()
	{
		return contentBounds;
	}

	public void setData(String data)
	{
		this.data = data;
		textBounds = InstancePainter.TextMetrics.INSTANCE.getSize(data, BoundsType.TEXT);
		contentBounds = new EnclosureBounds(contentBounds.x, contentBounds.y, textBounds.width, textBounds.height);
	}

	void setContentLocation(int x, int y)
	{
		contentBounds = new EnclosureBounds(x, y, contentBounds.width, contentBounds.height);
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	@Override
	public void requestRepaint()
	{
		RepaintRequestManager.requestRepaint(new RepaintAtomRequest(this));
	}

	public ChangeTextDirective createTextChange(String newText)
	{
		return new ChangeTextDirective(newText);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class ChangeTextDirective extends ChangeComponentStateDirective
	{
		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public class Notification extends UserInterfaceNotification
		{
			public String getText()
			{
				return text;
			}
		}

		final String text;

		public ChangeTextDirective(String text)
		{
			super(SelfPaintingListCell.this);

			this.text = text;
		}

		@Override
		public void commit()
		{
			SelfPaintingListCell.this.setData(text);
		}

		@Override
		public UserInterfaceNotification createNotification()
		{
			return new Notification();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Handler implements UserInterfaceHandler
	{
		public void textChanging(ChangeTextDirective.Notification textChange, PendingTransaction transaction)
		{
			SelfPaintingListCell.this.requestRepaint();
		}
	}
}
