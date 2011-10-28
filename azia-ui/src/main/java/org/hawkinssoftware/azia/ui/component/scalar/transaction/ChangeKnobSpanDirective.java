package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderKnob;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

public class ChangeKnobSpanDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public int getKnobWidth()
		{
			return knobWidth;
		}
	}

	public final int knobWidth;

	public ChangeKnobSpanDirective(AbstractSlider actor, int knobWidth)
	{
		super(actor);
		this.knobWidth = knobWidth;
	}

	private ChangeKnobSpanDirective(SliderKnob actor, int knobWidth)
	{
		super(actor);
		this.knobWidth = knobWidth;
	}

	public ChangeKnobSpanDirective forward(SliderKnob actor)
	{
		return new ChangeKnobSpanDirective(actor, knobWidth);
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
