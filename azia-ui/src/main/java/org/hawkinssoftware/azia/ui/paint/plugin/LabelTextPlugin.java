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
package org.hawkinssoftware.azia.ui.paint.plugin;

import java.awt.geom.Rectangle2D;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.text.handler.PlainTextHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
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
public abstract class LabelTextPlugin extends LabelContentPlugin
{
	protected Rectangle2D textBounds = new Rectangle2D.Double(-1.0, -1.0, -1.0, -1.0);

	@ValidateWrite.Exempt
	protected BoundsType boundsType = BoundsType.GLYPH;

	@Override
	public void paint(VirtualComponent component)
	{
		// TODO: the risk of generalized data handling is that this plugin could be installed where no such handler
		// exists, or is at some point removed, and in that case it will crash trying to size or paint
		Canvas.get().g.drawString(component.getDataHandler(PlainTextHandler.KEY).getText(), 0, 0);
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setBoundsType(BoundsType boundsType)
	{
		this.boundsType = boundsType;
	}

	public void textChanged(ChangeTextDirective textChange)
	{
		if (textChange.text != null)
		{
			textBounds = TextMetrics.INSTANCE.getBounds(textChange.text, boundsType);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class Center extends LabelTextPlugin
	{
		@Override
		public void paint(VirtualComponent component)
		{
			Canvas c = Canvas.get();

			int x = c.centerLeadingEdge(textBounds);
			int baseline;
			switch (boundsType)
			{
				case TEXT:
					baseline = c.centerTypicalTextBaseline();
					break;
				case GLYPH:
					baseline = c.centerExactBaseline(textBounds);
					break;
				default:
					throw new UnknownEnumConstantException(boundsType);
			}
			c.pushBoundsPosition(x, baseline);

			super.paint(component);
		}

		@Override
		public int getPackedSize(Axis axis)
		{
			switch (axis)
			{
				case H:
					return (int) Math.round(textBounds.getWidth());
				case V:
					return (int) Math.round(textBounds.getHeight());
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class Fixed extends LabelTextPlugin
	{
		private final int x;
		private final int baseline;
		private final int width;
		private final int height;

		public Fixed(int x, int baseline, int width, int height)
		{
			this.x = x;
			this.baseline = baseline;
			this.width = width;
			this.height = height;
		}

		@Override
		public int getPackedSize(Axis axis)
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
		public void paint(VirtualComponent component)
		{
			Canvas.get().pushBoundsPosition(x, baseline);
			super.paint(component);
		}
	}
}
