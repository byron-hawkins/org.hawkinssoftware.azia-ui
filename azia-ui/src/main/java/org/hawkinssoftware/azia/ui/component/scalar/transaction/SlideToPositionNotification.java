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
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;

/**
 * Appoints a new position the knob of a slider according to external calculation. The slider is expected to contribute
 * a directive which puts the new position into effect.
 * 
 * @author Byron Hawkins
 */
public class SlideToPositionNotification extends UserInterfaceNotification.Directed
{
	/**
	 * Relative to the slider track
	 */
	public final int pixelPosition;

	public SlideToPositionNotification(SliderComposite<? extends AbstractSlider> actor, int pixelPosition)
	{
		super(actor);

		this.pixelPosition = pixelPosition;
	}
}
