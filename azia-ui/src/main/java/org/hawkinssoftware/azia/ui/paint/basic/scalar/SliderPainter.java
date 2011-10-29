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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <SliderType>
 *            the generic type
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class SliderPainter<SliderType extends AbstractSlider> extends ComponentPainter<SliderType> implements AbstractSlider.Painter
{
	private int length = 50;
	private int width = 12;

	@Deprecated
	public void setLength(int length)
	{
		this.length = length;
	}

	@Deprecated
	public void setWidth(int width)
	{
		this.width = width;
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		if (axis == component.getAxis())
		{
			// FIXME: there's no packed length for a slider, right?
			return length;
		}
		else
		{
			return width;
		}
	}

	@Override
	public void paint(AbstractSlider component)
	{
		// painting delegated to sub-components
	}
}
