package org.hawkinssoftware.azia.ui.paint.transaction.paint;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(domains = RenderingDomain.class)
@DomainRole.Join(membership = RenderingDomain.class)
public class PaintComponentNotification extends UserInterfaceNotification
{
	final AbstractComponent component;
	final EnclosureBounds bounds;

	public PaintComponentNotification(AbstractComponent component, EnclosureBounds bounds)
	{
		this.component = component;
		this.bounds = bounds;
	}
}
