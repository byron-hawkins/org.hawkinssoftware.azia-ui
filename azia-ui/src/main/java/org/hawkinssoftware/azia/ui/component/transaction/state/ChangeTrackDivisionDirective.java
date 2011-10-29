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
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@VisibilityConstraint(domains = DisplayBoundsDomain.class)
@InvocationConstraint(domains = DisplayBoundsDomain.class)
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ChangeTrackDivisionDirective extends ChangeComponentStateDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public class Notification extends UserInterfaceNotification
	{
		public int getTrackDivision()
		{
			return trackDivision;
		}
	}

	public final int trackDivision;

	public ChangeTrackDivisionDirective(UserInterfaceActorDelegate actor, int trackDivision)
	{
		super(actor);
		this.trackDivision = trackDivision;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
