package org.hawkinssoftware.azia.ui.paint;

import java.util.Collection;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(domains = RenderingDomain.class)
public interface AggregatePainter<ComponentType extends AbstractComponent> extends InstancePainter<ComponentType>
{
	@InvocationConstraint(domains = RenderingDomain.class)
	@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class })
	public interface Atom extends UserInterfaceActorDelegate
	{
		void paint();
	}

	void paint(ComponentType component, Collection<AggregatePainter.Atom> atoms);
}
