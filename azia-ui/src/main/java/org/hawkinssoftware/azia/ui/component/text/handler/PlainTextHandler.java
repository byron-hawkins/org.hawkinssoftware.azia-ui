/*
 * Copyright (c) 2011 HawkinsSoftware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Byron Hawkins of HawkinsSoftware
 */
package org.hawkinssoftware.azia.ui.component.text.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public abstract class PlainTextHandler extends ComponentDataHandler
{
	public static final Key<PlainTextHandler> KEY = new Key<PlainTextHandler>();

	protected PlainTextHandler()
	{
		super(KEY);
	}

	public abstract String getText();

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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