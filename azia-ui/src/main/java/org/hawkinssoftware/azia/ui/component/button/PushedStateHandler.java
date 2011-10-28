package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
 
@ValidateRead
@ValidateWrite
public class PushedStateHandler extends ComponentDataHandler implements UserInterfaceActorDelegate
{
	public static void install(ChangeComponentStateDirective.Component component)
	{
		PushedStateHandler handler = new PushedStateHandler(component);
		component.installHandler(handler);
	}

	public static final Key<PushedStateHandler> KEY = new Key<PushedStateHandler>();

	public final ChangePushedStateDirective beginPushedState;
	public final ChangePushedStateDirective endPushedState;

	private final ChangeComponentStateDirective.Component component;

	private boolean isPushed = false;

	protected PushedStateHandler(ChangeComponentStateDirective.Component component)
	{
		super(KEY);

		this.component = component;

		beginPushedState = new ChangePushedStateDirective(component, true);
		endPushedState = new ChangePushedStateDirective(component, false);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return component;
	}

	public boolean isPushed()
	{
		return isPushed;
	}

	public void click(ChangePressedStateDirective.Notification click, PendingTransaction transaction)
	{
		if (click.isPressed())
		{
			if (isPushed)
			{
				transaction.contribute(endPushedState);
			}
			else
			{
				transaction.contribute(beginPushedState);
			}
		}
	}

	public void changeState(ChangePushedStateDirective change)
	{
		isPushed = change.isPushed;
		component.requestRepaint();
	}
}
