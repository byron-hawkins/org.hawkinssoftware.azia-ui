package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

@InvocationConstraint(domains = MouseEventDomain.class)
public class ChangeMouseOverDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public boolean isMouseOver()
		{
			return isMouseOver;
		}
	}

	public final boolean isMouseOver;

	public ChangeMouseOverDirective(UserInterfaceActorDelegate actor, boolean isMouseOver)
	{
		super(actor);
		this.isMouseOver = isMouseOver;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
