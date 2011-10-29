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
package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.input.MouseAware;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public abstract class ChangeComponentStateDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Handler extends UserInterfaceHandler
	{
		void applyStateChange(ChangeComponentStateDirective action);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Component extends MouseAware, PaintableActor, UserInterfaceHandler.Host
	{
		void requestRepaint();
	}

	public ChangeComponentStateDirective(UserInterfaceActorDelegate actor)
	{
		super(actor);
	}
}
