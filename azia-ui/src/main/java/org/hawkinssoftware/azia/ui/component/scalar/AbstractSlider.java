package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public abstract class AbstractSlider extends AbstractComponent
{
	public enum Direction
	{
		DOWN,
		UP;
	}

	public interface Painter
	{
		// marker
	}

	// RNS: if the `axis is not final, then it should be transactional
	protected Axis axis;

	public Axis getAxis()
	{
		return axis;
	}

	public void setAxis(Axis axis)
	{
		this.axis = axis;
	}
}
