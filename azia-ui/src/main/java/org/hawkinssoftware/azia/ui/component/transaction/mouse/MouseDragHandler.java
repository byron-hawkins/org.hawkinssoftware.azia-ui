package org.hawkinssoftware.azia.ui.component.transaction.mouse;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.MouseDragDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPassTermination;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@VisibilityConstraint(extendedTypes = { VirtualComponent.class, UserInterfaceHandler.class })
@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseDragHandler extends ComponentDataHandler implements UserInterfaceActorDelegate
{
	public static void install(UserInterfaceHandler.Host host)
	{
		MouseDragHandler handler = new MouseDragHandler(host);
		host.installHandler(handler);
	}
	
	public static final Key<MouseDragHandler> KEY = new Key<MouseDragHandler>();
	
	public final ChangePressedStateDirective beginPressedState;
	public final ChangePressedStateDirective endPressedState;

	private final UserInterfaceActorDelegate actor;

	private boolean isPressed = false;
	private boolean dragging = false;

	private MouseDragHandler(UserInterfaceActorDelegate actor) 
	{
		super(KEY);

		this.actor = actor;

		beginPressedState = new ChangePressedStateDirective(actor, true);
		endPressedState = new ChangePressedStateDirective(actor, false);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return actor.getActor();
	}

	public void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (pass.event().changes().contains(MouseInputEvent.Change.LEFT_BUTTON))
		{
			if (pass.event().buttonsDown().contains(MouseInputEvent.Button.LEFT))
			{
				transaction.contribute(beginPressedState);
			}
			else
			{
				transaction.contribute(endPressedState);
				if (dragging)
				{
					transaction.contribute(new MouseDragDirective(actor, MouseDragDirective.State.END, pass.event()));
				}
			}
		}
		else if (pass.event().changes().contains(MouseInputEvent.Change.POSITION))
		{
			if (isPressed)
			{
				if (dragging)
				{
					transaction.contribute(new MouseDragDirective(actor, MouseDragDirective.State.DRAG, pass.event()));
				}
				else
				{
					transaction.contribute(new MouseDragDirective(actor, MouseDragDirective.State.START, pass.event()));
				}
			}
		}
	}

	public void mouseStateTerminated(EventPassTermination termination, PendingTransaction transaction)
	{
		MouseInputEvent.Button releasedButton = termination.event().getButtonRelease();
		if (releasedButton == MouseInputEvent.Button.LEFT)
		{
			if (dragging)
			{
				transaction.contribute(new MouseDragDirective(actor, MouseDragDirective.State.END, termination.event()));
				transaction.contribute(endPressedState);
			}
		}
	}

	public void mouseDragged(MouseDragDirective drag)
	{
		dragging = drag.state != MouseDragDirective.State.END;
	}

	public void changePressedState(ChangePressedStateDirective change)
	{
		if (change == endPressedState)
		{
			isPressed = false;
		}
		else if (change == beginPressedState)
		{
			isPressed = true;
		}
	}
}
