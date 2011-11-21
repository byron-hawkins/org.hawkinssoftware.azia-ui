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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.PartialBounds;
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
	protected Axis.Bounds.Double textBounds = new Axis.Bounds.Double(-1.0, -1.0, -1.0, -1.0);
	protected PartialBounds fixedBounds = new PartialBounds();
	protected final Axis.Bounds activeBounds = new ActiveBounds();

	private class ActiveBounds implements Axis.Bounds
	{
		@Override
		public int getExtent(Axis axis)
		{
			return getPosition(axis) + getSpan(axis);
		}

		@Override
		public int getPosition(Axis axis)
		{
			return fixedBounds.hasPosition(axis) ? fixedBounds.getPosition(axis) : textBounds.getPosition(axis);
		}

		@Override
		public int getSpan(Axis axis)
		{
			return fixedBounds.hasSpan(axis) ? fixedBounds.getSpan(axis) : textBounds.getSpan(axis);
		}
	}

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

	public void clearFixedBounds()
	{
		fixedBounds = new PartialBounds();
	}

	public void setFixedBounds(PartialBounds fixedBounds)
	{
		this.fixedBounds = fixedBounds;
	}

	public void mergeFixedBounds(PartialBounds fixedBounds)
	{
		this.fixedBounds = this.fixedBounds.fillWith(fixedBounds);
	}

	public void textChanged(ChangeTextDirective textChange)
	{
		if (textChange.text != null)
		{
			textBounds.bounds = TextMetrics.INSTANCE.getBounds(textChange.text, boundsType);
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

			int x = c.centerLeadingEdge(activeBounds);
			int baseline;
			switch (boundsType)
			{
				case TEXT:
					baseline = c.centerTypicalTextBaseline();
					break;
				case GLYPH:
					baseline = c.centerExactBaseline(activeBounds);
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
			return ((int) Math.round(activeBounds.getSpan(axis))) + 1;
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
