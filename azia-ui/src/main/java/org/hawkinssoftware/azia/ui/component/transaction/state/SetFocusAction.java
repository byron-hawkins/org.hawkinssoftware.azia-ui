package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;

public class SetFocusAction extends UserInterfaceDirective
{
	public final ComponentEnclosure<?, ?> activate;

	public SetFocusAction(ComponentEnclosure<?, ?> activate)
	{
		super(ComponentRegistry.getInstance().getFocusHandler());

		this.activate = activate;
	}
}
