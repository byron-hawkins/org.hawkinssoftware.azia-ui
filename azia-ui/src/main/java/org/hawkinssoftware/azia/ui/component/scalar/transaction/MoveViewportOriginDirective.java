package org.hawkinssoftware.azia.ui.component.scalar.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;

public class MoveViewportOriginDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public int x()
		{
			return x;
		}

		public int y()
		{
			return y;
		}
	}

	public final int x;
	public final int y;

	public MoveViewportOriginDirective(ScrollPaneViewport actor, int x, int y)
	{
		super(actor);

		if (x < 0)
		{
			System.err.println("Warning: setting negative viewport position: x = " + x);
		}

		this.x = x;
		this.y = y;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
