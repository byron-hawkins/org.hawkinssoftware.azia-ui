package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

public class ChangeViewportContentBoundsDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public EnclosureBounds getBounds()
		{
			return bounds;
		}
	}
	
	public final EnclosureBounds bounds;

	public ChangeViewportContentBoundsDirective(ScrollPaneViewport actor, EnclosureBounds bounds)
	{
		super(actor);

		this.bounds = bounds; 
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
