package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@VisibilityConstraint(domains = DisplayBoundsDomain.class)
@InvocationConstraint(domains = DisplayBoundsDomain.class)
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ChangeTrackDivisionDirective extends ChangeComponentStateDirective
{
	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public class Notification extends UserInterfaceNotification
	{
		public int getTrackDivision()
		{
			return trackDivision;
		}
	}

	public final int trackDivision;

	public ChangeTrackDivisionDirective(UserInterfaceActorDelegate actor, int trackDivision)
	{
		super(actor);
		this.trackDivision = trackDivision;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
