package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ViewportStateChangeHandler implements UserInterfaceHandler
{
	private final ScrollPaneViewport host;

	public ViewportStateChangeHandler(ScrollPaneViewport host)
	{
		this.host = host;
	}

	public void moveViewport(MoveViewportOriginDirective move)
	{
		host.setViewportPosition(move.x, move.y);
	}
}
