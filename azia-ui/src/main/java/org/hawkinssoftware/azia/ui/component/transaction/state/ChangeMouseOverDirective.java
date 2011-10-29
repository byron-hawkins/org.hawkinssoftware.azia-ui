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
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = MouseEventDomain.class)
public class ChangeMouseOverDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Notification extends UserInterfaceNotification
	{
		public boolean isMouseOver()
		{
			return isMouseOver;
		}
	}

	public final boolean isMouseOver;

	public ChangeMouseOverDirective(UserInterfaceActorDelegate actor, boolean isMouseOver)
	{
		super(actor);
		this.isMouseOver = isMouseOver;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
