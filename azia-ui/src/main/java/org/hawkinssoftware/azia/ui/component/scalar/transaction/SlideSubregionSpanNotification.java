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

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class SlideSubregionSpanNotification extends UserInterfaceNotification.Directed
{
	public final AbstractSlider.Direction direction;

	public SlideSubregionSpanNotification(AbstractSlider slider, AbstractSlider.Direction direction)
	{
		super(slider);

		this.direction = direction;
	}
}
