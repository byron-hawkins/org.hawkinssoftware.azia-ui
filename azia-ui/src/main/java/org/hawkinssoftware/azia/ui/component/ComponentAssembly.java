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

//@SuppressWarnings("rawtypes")
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
