package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;

public class SlideSubregionSpanNotification extends UserInterfaceNotification.Directed
{
	public final AbstractSlider.Direction direction;

	public SlideSubregionSpanNotification(AbstractSlider slider, AbstractSlider.Direction direction)
	{
		super(slider);

		this.direction = direction;
	}
}
