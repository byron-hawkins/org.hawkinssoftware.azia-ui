package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class SlideToPositionNotification extends UserInterfaceNotification.Directed
{
	/**
	 * Relative to the slider track
	 */
	public final int pixelPosition;

	public SlideToPositionNotification(UserInterfaceActorDelegate actor, int pixelPosition)
	{
		super(actor);

		this.pixelPosition = pixelPosition;
	}
}
