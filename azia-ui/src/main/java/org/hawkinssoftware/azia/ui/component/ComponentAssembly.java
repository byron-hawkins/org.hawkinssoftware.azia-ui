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

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent.Key;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.PainterRegistry;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.moa.ExecutionPath;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.role.TypeRole;
import org.hawkinssoftware.rns.core.util.DefinesIdentity;

/**
 * Abstract assembly descriptor for an <code>AbstractComponent</code>.
 * 
 * @param <ComponentType>
 *            The specific kind of component to create.
 * @param <PainterMarker>
 *            Identifies the type of painter for type checking purposes only. Generic specifications do not support
 *            direct identification of the painter, since its hierarchy can diverge from the <code>ComponentType</code>
 *            hierarchy, which is also specified here and would be directly related to the (unsupported) PainterType.
 * @param <EnclosureType>
 *            The kind of <code>ComponentEnclosure</code> with which to wrap the constructed component.
 * @author Byron Hawkins
 * 
 * @JTourBusStop 1, Usage of @DefinesIdentity in Azia, Identity root - ComponentAssembly:
 * 
 *               There are 5 base types in Azia annotated with @DefinesIdentity, each of them fundamental to the
 *               architecture of the library. This component assembly descriptor is a fundamental base type because its
 *               usage is mandatory for constructing all subtypes of AbstractComponent and AbstractComposite.
 */
@DefinesIdentity
@InvocationConstraint(domains = AssemblyDomain.class)
@DomainRole.Join(membership = AssemblyDomain.class)
public abstract class ComponentAssembly<ComponentType extends AbstractComponent, PainterMarker, EnclosureType extends ComponentEnclosure<ComponentType, ?>>
{
	public final UserInterfaceActor.SynchronizationRole actorType;
	private AbstractComponent.Key<? extends ComponentType> component;
	private InstancePainter.Key<PainterMarker, ? extends InstancePainter<ComponentType>> painter;
	private ComponentEnclosure.Key enclosure;

	protected ComponentAssembly(UserInterfaceActor.SynchronizationRole actorType)
	{
		this.actorType = actorType;
	}

	protected ComponentAssembly(UserInterfaceActor.SynchronizationRole actorType, Key<? extends ComponentType> component, ComponentEnclosure.Key enclosure)
	{
		this.actorType = actorType;
		this.component = component;
		this.enclosure = enclosure;
	}

	protected ComponentAssembly(UserInterfaceActor.SynchronizationRole actorType, Key<? extends ComponentType> component,
			InstancePainter.Key<PainterMarker, ? extends InstancePainter<ComponentType>> painter, ComponentEnclosure.Key enclosure)
	{
		this.actorType = actorType;
		this.component = component;
		this.painter = painter;
		this.enclosure = enclosure;
	}

	// WIP: R-N-S clampdown on the Constraint implementations, to avoid stack overflow
	/**
	 * MOA implementation of domain containment, which will print log warnings any time a thread leaves the
	 * <code>AssemblyDomain</code> after having started something inside it.
	 * 
	 * @author Byron Hawkins
	 */
	private static class Containment implements ExecutionPath.StackObserver
	{
		static final Containment INSTANCE = new Containment();

		@Override
		public void sendingMessage(TypeRole senderRole, TypeRole receiverRole, Object receiver, String messageDescription)
		{
			if (ExecutionPath.getCurrentCallDescription().contains("<init>"))
			{
				return;
			}
			if (!receiverRole.hasRole(AssemblyDomain.INSTANCE))
			{
				Log.out(Tag.CONTAIN_DEBUG, "Warning: roaming from the AssemblyDomain: (receiver %s) %s", receiverRole,
						ExecutionPath.getCurrentCallDescription());
			}
		}

		@Override
		public void messageReturningFrom(TypeRole receiverRole, Object receiver)
		{
		}
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(types = ComponentFactory.class, inherit = true)
	public void assemble(EnclosureType enclosure)
	{
		// WIP: could auto-pop it
		// ExecutionPath.addConstraint(Containment.INSTANCE);

		try
		{
			if (painter == null)
			{
				PainterRegistry.getInstance().installPainter((ComponentEnclosure<ComponentType, InstancePainter<ComponentType>>) enclosure);
			}
			else
			{
				PainterRegistry.getInstance().installPainter((ComponentEnclosure<ComponentType, InstancePainter<ComponentType>>) enclosure, painter);
			}
		}
		finally
		{
			// ExecutionPath.removeConstraint(Containment.INSTANCE);
		}
	}

	public AbstractComponent.Key<? extends ComponentType> getComponent()
	{
		return component;
	}

	public void setComponent(AbstractComponent.Key<? extends ComponentType> component)
	{
		this.component = component;
	}

	public ComponentEnclosure.Key getEnclosure()
	{
		return enclosure;
	}

	public void setEnclosure(ComponentEnclosure.Key enclosure)
	{
		this.enclosure = enclosure;
	}

	public InstancePainter.Key<PainterMarker, ? extends InstancePainter<ComponentType>> getPainter()
	{
		return painter;
	}

	public void setPainter(InstancePainter.Key<PainterMarker, ? extends InstancePainter<ComponentType>> painter)
	{
		this.painter = painter;
	}

	public boolean useRegisteredPainter()
	{
		return painter == null;
	}
}
