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
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.DefinesIdentity;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * A generic handler for receiving <code>UserInterfaceDirective</code>s and <code>UserInterfaceNotification</code>s
 * broadcast by the central transaction manager.
 * <p>
 * To receive a UINotification, the subclass of UIHandler implements a public method having any name and two parameters:
 * <ul>
 * <li>the specific subtype of <code>UserInterfaceNotification</code>it wants to receive (UINotification base types will
 * be matched to instances of all their subclasses)</li>
 * <li>a </code>PendingTransaction</code></li>
 * </ul>
 * Fields protected with <code>@ValidateRead</code> are readable during this method invocation. So for example, to
 * receive a (presently fictitious) <code>ButtonPressedNotification</code> for collaboration purposes, the UIHandler
 * declares:
 * <p>
 * <code>public void buttonPressed(ButtonPressedNotification notification, PendingTransaction transaction);</code>
 * <i>or perhaps</i><br>
 * <code>public void buttonEventOccurred(AbstractButtonNotification notification, PendingTransaction transaction);</code>
 * <p>
 * To receive a UIDirective, the subclass of UIHandler implements a public method having any name and one parameter:
 * <ul>
 * <li>the type is the specific subtype of <code>UserInterfaceDirective</code> the UIHandler wants to receive
 * (UIDirective base types will be matched to instances of all their subclasses)</li>
 * </ul>
 * Fields protected with <code>@ValidateRead</code> and <code>@ValidateWrite</code> are readable and writable
 * (respectively) during this method invocation. So for example, to modify local fields protected with
 * <code>@ValidateWrite</code> when a (presently fictitious) <code>ComboBoxOpenedEvent</code> occurs, the UIHandler
 * declares:
 * <p>
 * <code>public void comboBoxIsOpeningNow(ComboBoxOpenedEvent event);</code>
 * <p>
 * Invocations of these special methods are instrumented in bytecode at runtime when the subclass of UIHandler is loaded
 * into the JVM.
 * 
 * @author Byron Hawkins
 * @see ValidateRead
 * @see ValidateWrite
 * @see UserInterfaceDirective
 * @see UserInterfaceNotification
 * @see UserInterfaceTransaction.PendingTransaction
 */
@DefinesIdentity
@DomainRole.Join(membership = { TransactionParticipant.class, UserInterfaceHandler.HandlerDomain.class })
public interface UserInterfaceHandler extends CompositionElement
{
	/**
	 * DomainRole for restricting publication of <code>UserInterfaceHandler</code> concerns.
	 * 
	 * @author Byron Hawkins
	 */
	public static class HandlerDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final HandlerDomain INSTANCE = new HandlerDomain();
	}

	/**
	 * By implementing this interface, the Host declares that it will forward all instances of
	 * <code>UserInterfaceDirective</code> and <code>UserInterfaceNotification</code> directed to its actor (specified
	 * per <code>UserInterfaceActorDelegate</code>) to every instance of <code>UserInterfaceHandler</code> registered
	 * via <code>installHandler()</code> (or <code>changeHandler()</code>).
	 * 
	 * @author Byron Hawkins
	 * @see UserInterfaceDirective
	 * @see UserInterfaceNotification
	 */
	public interface Host extends UserInterfaceActorDelegate
	{
		void installHandler(UserInterfaceHandler handler);

		<HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler);

		void removeHandler(UserInterfaceHandler handler);
	}
}