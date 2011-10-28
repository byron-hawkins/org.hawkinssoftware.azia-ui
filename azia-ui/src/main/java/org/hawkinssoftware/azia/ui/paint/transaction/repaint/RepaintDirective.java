package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import java.util.Collection;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.PaintableActorDelegate;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
@DomainRole.Join(membership = RenderingDomain.class)
public abstract class RepaintDirective implements PaintableActorDelegate
{
	@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
	public interface Host
	{
		void invokeTransactionRepaints(Collection<RepaintDirective> repaints);

		void applyTransactionRepaints();
	}

	// No access to the actor when it appears as a RepaintDirective
	@InvocationConstraint(types = RepaintRequestManager.class)
	public abstract PaintableActor getActor();

	public Object getInstanceKey()
	{
		return getActor();
	}
}
