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
import org.hawkinssoftware.azia.ui.component.scalar.SliderKnob;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class SliderKnobPainter extends ComponentPainter<SliderKnob> implements SliderKnob.Painter
{
	private int length = 20;
	private int width = 12;

	@Override
	public int getPackedSize(Axis axis)
	{
		if (axis == Axis.H)
		{
			// FIXME: really there is no packed length here
			return length;
		}
		else
		{
			return width;
		}
	}

	@Override
	public void paint(SliderKnob component)
	{
		Canvas c = Canvas.get();
		
		c.pushColor(Color.gray);
		c.g.fillRect(0, 0, c.span().width, c.span().height);
		c.pushColor(Color.black);
		c.g.drawRect(0, 0, c.span().width, c.span().height);
	}
}
