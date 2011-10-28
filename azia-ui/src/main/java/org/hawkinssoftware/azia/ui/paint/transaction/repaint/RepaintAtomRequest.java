package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = RenderingDomain.class)
public class RepaintAtomRequest implements UserInterfaceActorDelegate, CompositionElement.Initializing
{
	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private AbstractComposite<AbstractComponent, AggregatePainter<AbstractComponent>> composite;
	
	final AggregatePainter.Atom atom;

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	public RepaintAtomRequest()
	{
		// TODO: is this assumption too strange? I'm saying, if you don't send me an atom, then I must be the atom
		this.atom = (AggregatePainter.Atom) this;
	}

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	public RepaintAtomRequest(AggregatePainter.Atom atom)
	{
		this.atom = atom;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		this.composite = CompositionRegistry.getComposite(AbstractComposite.class);
	}
	
	public AbstractComposite<AbstractComponent, AggregatePainter<AbstractComponent>> getComposite()
	{
		return composite;
	}
	
	public PaintableActor getPaintedActor()
	{
		return composite.getComponent();
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return composite.getComponent();
	}

	public Object getAggregationKey()
	{
		return getActor();
	}
}
