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
import org.hawkinssoftware.azia.core.layout.ScreenPosition;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@VisibilityConstraint(domains = MouseEventDomain.class)
@InvocationConstraint(domains = MouseEventDomain.class)
@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseDragDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public enum State
	{
		START,
		DRAG,
		END;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = MouseEventDomain.class)
	@InvocationConstraint(domains = MouseEventDomain.class)
	@DomainRole.Join(membership = MouseEventDomain.class)
	public class Notification extends UserInterfaceNotification
	{
		public State getState()
		{
			return state;
		}

		public ScreenPosition getPosition()
		{
			return position;
		}
	}

	public final State state;
	public final ScreenPosition position;

	public MouseDragDirective(UserInterfaceActorDelegate draggedActor, State state, ScreenPosition position)
	{
		super(draggedActor);

		this.state = state;
		this.position = position;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
