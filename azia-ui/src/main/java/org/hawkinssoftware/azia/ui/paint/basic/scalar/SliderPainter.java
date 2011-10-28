package org.hawkinssoftware.azia.ui.paint.basic.scalar;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

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
