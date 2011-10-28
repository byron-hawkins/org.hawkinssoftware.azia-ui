package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.input.MouseAware;

public abstract class ChangeComponentStateDirective extends UserInterfaceDirective
{
	public interface Handler extends UserInterfaceHandler
	{
		void applyStateChange(ChangeComponentStateDirective action);
	}

	public interface Component extends MouseAware, PaintableActor, UserInterfaceHandler.Host
	{
		void requestRepaint();
	}

	public ChangeComponentStateDirective(UserInterfaceActorDelegate actor)
	{
		super(actor);
	}
}
