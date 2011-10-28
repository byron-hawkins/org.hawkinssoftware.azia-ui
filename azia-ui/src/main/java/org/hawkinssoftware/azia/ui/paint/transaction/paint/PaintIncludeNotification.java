package org.hawkinssoftware.azia.ui.paint.transaction.paint;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(domains = RenderingDomain.class)
@DomainRole.Join(membership = RenderingDomain.class)
public class PaintIncludeNotification extends UserInterfaceNotification.Directed
{
	public PaintIncludeNotification(UserInterfaceActorDelegate actor)
	{ 
		super(actor);
	}
}
