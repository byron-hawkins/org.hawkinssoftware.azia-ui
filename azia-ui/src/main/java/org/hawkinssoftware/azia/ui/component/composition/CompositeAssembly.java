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
package org.hawkinssoftware.azia.ui.component.composition;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @param <PainterMarker>
 *            the generic type
 * @param <CompositeType>
 *            the generic type
 * @author Byron Hawkins
 */
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
