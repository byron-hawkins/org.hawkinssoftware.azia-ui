package org.hawkinssoftware.azia.ui.component.transaction.window;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;

public class SetVisibleAction extends UserInterfaceDirective
{
	public final boolean visible;

	public SetVisibleAction(UserInterfaceActorDelegate actor, boolean visible)
	{
		super(actor);
		this.visible = visible;
	}
}
