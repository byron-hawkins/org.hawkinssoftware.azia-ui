package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.ScreenPosition;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@VisibilityConstraint(domains = MouseEventDomain.class)
@InvocationConstraint(domains = MouseEventDomain.class)
@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseDragDirective extends ChangeComponentStateDirective
{
	public enum State
	{
		START,
		DRAG,
		END;
	}

	@VisibilityConstraint(domains = MouseEventDomain.class)
	@InvocationConstraint(domains = MouseEventDomain.class)
	@DomainRole.Join(membership = MouseEventDomain.class)
	public class Notification extends UserInterfaceNotification
	{
		public State getState()
		{
			return state;
		}

		public ScreenPosition getPosition()
		{
			return position;
		}
	}

	public final State state;
	public final ScreenPosition position;

	public MouseDragDirective(UserInterfaceActorDelegate draggedActor, State state, ScreenPosition position)
	{
		super(draggedActor);

		this.state = state;
		this.position = position;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
