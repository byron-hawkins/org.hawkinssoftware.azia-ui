package org.hawkinssoftware.azia.ui.component.composition;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = AssemblyDomain.class)
public abstract class CompositeAssembly<ComponentType extends AbstractComponent, PainterMarker, CompositeType extends AbstractComposite<ComponentType, ?>>
 		extends ComponentAssembly<ComponentType, PainterMarker, CompositeType>
{
	protected CompositeAssembly(SynchronizationRole actorType)
	{
		super(actorType);
	}

	protected CompositeAssembly(SynchronizationRole actorType, AbstractComponent.Key<? extends ComponentType> component, ComponentEnclosure.Key enclosure)
	{
		super(actorType, component, enclosure);
	}

	protected CompositeAssembly(SynchronizationRole actorType, AbstractComponent.Key<? extends ComponentType> component,
			InstancePainter.Key<PainterMarker, ? extends InstancePainter<ComponentType>> painter, ComponentEnclosure.Key enclosure)
	{
		super(actorType, component, painter, enclosure);
	}
}
