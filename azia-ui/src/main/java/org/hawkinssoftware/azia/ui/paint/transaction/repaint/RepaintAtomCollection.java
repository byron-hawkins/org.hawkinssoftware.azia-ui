package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

public class RepaintAtomCollection extends RepaintDirective
{
	private final ComponentEnclosure<AbstractComponent, AggregatePainter<AbstractComponent>> enclosure;
	private final Set<AggregatePainter.Atom> repaintAtoms = new HashSet<AggregatePainter.Atom>();

	public RepaintAtomCollection(RepaintAtomRequest repaintAtom)
	{
		this.enclosure = repaintAtom.getComposite();

		add(repaintAtom);
	}

	@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
	public void add(RepaintAtomRequest repaintAtom)
	{
		repaintAtoms.add(repaintAtom.atom);
	}

	@InvocationConstraint(domains = RenderingDomain.class)
	public ComponentEnclosure<AbstractComponent, AggregatePainter<AbstractComponent>> getEnclosure()
	{
		return enclosure;
	}

	@InvocationConstraint(domains = RenderingDomain.class)
	public Collection<AggregatePainter.Atom> getAtoms()
	{
		return repaintAtoms;
	}

	@Override
	public PaintableActor getActor()
	{
		return enclosure.getComponent();
	}

	@Override
	public Object getInstanceKey()
	{
		return enclosure.getComponent();
	}
}
