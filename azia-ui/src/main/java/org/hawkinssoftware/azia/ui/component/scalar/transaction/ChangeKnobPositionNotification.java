package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;

public class ChangeKnobPositionNotification extends UserInterfaceNotification.Directed
{
	public final int knobPosition;

	public ChangeKnobPositionNotification(AbstractSlider slider, int relativeKnobPosition)
	{
		super(slider);

		this.knobPosition = relativeKnobPosition;
	}
}
