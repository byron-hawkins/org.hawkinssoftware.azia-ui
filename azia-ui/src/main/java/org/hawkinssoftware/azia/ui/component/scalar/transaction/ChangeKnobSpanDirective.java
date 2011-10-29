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
package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderKnob;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ChangeKnobSpanDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
