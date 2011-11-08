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
package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.DefinesIdentity;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DefinesIdentity
@DomainRole.Join(membership = { TransactionParticipant.class, UserInterfaceHandler.HandlerDomain.class })
public interface UserInterfaceHandler extends CompositionElement
{ 
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class HandlerDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final HandlerDomain INSTANCE = new HandlerDomain(); 
	}
 
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */  
	public interface Host extends UserInterfaceActorDelegate
	{
		void installHandler(UserInterfaceHandler handler);

		<HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler);

		void removeHandler(UserInterfaceHandler handler);
	}
}