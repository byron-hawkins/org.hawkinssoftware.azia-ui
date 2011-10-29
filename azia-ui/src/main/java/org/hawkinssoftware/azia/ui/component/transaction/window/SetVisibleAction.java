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
package org.hawkinssoftware.azia.ui.component.transaction.window;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class SetVisibleAction extends UserInterfaceDirective
{
	public final boolean visible;

	public SetVisibleAction(UserInterfaceActorDelegate actor, boolean visible)
	{
		super(actor);
		this.visible = visible;
	}
}
