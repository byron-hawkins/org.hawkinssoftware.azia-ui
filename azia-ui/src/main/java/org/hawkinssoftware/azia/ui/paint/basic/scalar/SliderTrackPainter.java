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
package org.hawkinssoftware.azia.ui.paint.basic.scalar;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack.SliderTrackDomain;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderTrackDivisionHandler;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * Generic painter for a slider track, commonly used for painting scrollbar tracks.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = SliderTrackDomain.class)
public class SliderTrackPainter extends ComponentPainter<SliderTrack> implements SliderTrack.Painter
{
	private static final Color NORMAL_BACKGROUND = new Color(0xDDDDDD);
	private static final Color MOUSEOVER_BACKGROUND = new Color(0xEEEEEE);
	private static final Color PRESSED_BACKGROUND = new Color(0xFFFFFF);

	private int length = 50;
	private int width = 12;

	@Override
	public int getPackedSize(Axis axis)
	{
		if (axis == Axis.H)
		{
			return length;
		}
		else
		{
			return width;
		}
	}

	private void setColorForHalf(Canvas c, SliderTrackDivisionHandler.Half half)
	{
		if (half.mousePressed.isPressed())
		{
			c.pushColor(PRESSED_BACKGROUND);
		}
		else if (half.mouseOver.isMouseOver())
		{
			c.pushColor(MOUSEOVER_BACKGROUND);
		}
		else
		{
			c.pushColor(NORMAL_BACKGROUND);
		}
	}

	@Override
	public void paint(SliderTrack component)
	{
		Canvas c = Canvas.get();
		
		setColorForHalf(c, component.getDivisionHandler().lowerHalf);
		switch (component.getAxis())
		{
			case H:
				c.g.fillRect(0, 0, component.getDivisionPoint(), c.span().height);
				break;
			case V:
				c.g.fillRect(0, 0, c.span().width, component.getDivisionPoint());
				break;
			default:
				throw new UnknownEnumConstantException(component.getAxis());
		}

		setColorForHalf(c, component.getDivisionHandler().upperHalf);
		switch (component.getAxis())
		{
			case H:
				c.g.fillRect(component.getDivisionPoint(), 0, c.span().width - component.getDivisionPoint(), c.span().height);
				break;
			case V:
				c.g.fillRect(0, component.getDivisionPoint(), c.span().width, c.span().height - component.getDivisionPoint());
				break;
			default:
				throw new UnknownEnumConstantException(component.getAxis());
		}

		c.pushColor(Color.black);
		c.g.drawRect(0, 0, c.span().width, c.span().height);
	}
}
