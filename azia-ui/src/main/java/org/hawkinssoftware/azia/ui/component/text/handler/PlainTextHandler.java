package org.hawkinssoftware.azia.ui.component.text.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

public abstract class PlainTextHandler extends ComponentDataHandler
{
	public static final Key<PlainTextHandler> KEY = new Key<PlainTextHandler>();

	protected PlainTextHandler()
	{
		super(KEY);
	}

	public abstract String getText();

	@ValidateRead
	@ValidateWrite
	public static class Basic extends PlainTextHandler implements UserInterfaceActorDelegate
	{
		protected final UserInterfaceActorDelegate actor;

		protected String text;

		public Basic(UserInterfaceActorDelegate actor)
		{
			this.actor = actor;
		}

		public String getText()
		{
			return text;
		}

		@Override
		public UserInterfaceActor getActor()
		{
			return actor.getActor();
		}

		public void changeText(ChangeTextDirective change)
		{
			text = change.text;
		}
	}
}