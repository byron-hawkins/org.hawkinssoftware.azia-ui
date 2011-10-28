package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.DefinesIdentity;

@DefinesIdentity
@DomainRole.Join(membership = { TransactionParticipant.class, UserInterfaceHandler.HandlerDomain.class })
public interface UserInterfaceHandler extends CompositionElement
{ 
	public static class HandlerDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final HandlerDomain INSTANCE = new HandlerDomain(); 
	}
 
	public interface Host extends UserInterfaceActorDelegate
	{
		void installHandler(UserInterfaceHandler handler);

		<HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler);

		void removeHandler(UserInterfaceHandler handler);
	}
}
