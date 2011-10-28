package org.hawkinssoftware.azia.ui.paint.transaction.resize;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

@InvocationConstraint(domains = RenderingDomain.class)
public class PainterSizeChangeDirective extends UserInterfaceDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public Integer getWidth()
		{
			return width;
		}

		public Integer getHeight()
		{
			return height;
		}
	}

	public final Integer width;
	public final Integer height;

	public PainterSizeChangeDirective(AbstractComponent component, Integer width, Integer height)
	{
		super(component);

		this.width = width;
		this.height = height;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
