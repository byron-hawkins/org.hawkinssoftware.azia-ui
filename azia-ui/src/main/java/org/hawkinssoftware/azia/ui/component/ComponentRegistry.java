package org.hawkinssoftware.azia.ui.component;

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionFacilitation;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { TransactionFacilitation.class, AssemblyDomain.class })
public final class ComponentRegistry
{
	private static ComponentRegistry INSTANCE;

	public static ComponentRegistry getInstance()
	{
		synchronized (ComponentRegistry.class)
		{
			if (INSTANCE == null)
			{
				INSTANCE = new ComponentRegistry();
			}
			return INSTANCE;
		}
	}

	private final Map<ComponentAssembly<?, ?, ?>, ComponentEnclosure<?, ?>> componentsByKey = new HashMap<ComponentAssembly<?, ?, ?>, ComponentEnclosure<?, ?>>();

	/**
	 * Get a composite which the caller is certain has already been instantiated.
	 * 
	 * @param assembly
	 * @return the composite
	 */
	public <ComponentType extends AbstractComponent, PainterType, CompositeType extends AbstractComposite<ComponentType, ?>> CompositeType getComposite(
			CompositeAssembly<ComponentType, PainterType, CompositeType> assembly)
	{
		return establishComposite(assembly, null);
	}

	/**
	 * Get a composite which may not yet be instantiated. If the composite is instantiated now, associate it with
	 * <code>repaintHost</code>.
	 * 
	 * @param assembly
	 * @param repaintHost
	 * @return the composite, associated with <code>repaintHost</code> only if it was created just now.
	 */
	@SuppressWarnings("unchecked")
	public <ComponentType extends AbstractComponent, PainterType, CompositeType extends AbstractComposite<ComponentType, ?>> CompositeType establishComposite(
			CompositeAssembly<ComponentType, PainterType, CompositeType> assembly, RepaintDirective.Host repaintHost)
	{
		CompositeType composite = (CompositeType) componentsByKey.get(assembly);
		if (composite == null)
		{
			ComponentCreationTask task = new ComponentCreationTask(assembly.actorType, assembly.getClass().getName(), assembly, repaintHost);
			task.start();
			composite = (CompositeType) task.enclosure;
			componentsByKey.put(assembly, composite);
		}
		return composite;
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(types = ComponentFactory.class)
	public <ComponentType extends AbstractComponent, PainterType, EnclosureType extends ComponentEnclosure<ComponentType, ?>> EnclosureType getComponent(
			ComponentAssembly<ComponentType, PainterType, EnclosureType> assembly)
	{
		EnclosureType enclosure = (EnclosureType) componentsByKey.get(assembly);
		if (enclosure == null)
		{
			ComponentCreationTask task = new ComponentCreationTask(assembly.actorType, assembly.getClass().getName(), assembly, null);
			task.start();
			enclosure = (EnclosureType) task.enclosure;
			componentsByKey.put(assembly, enclosure);
		}
		return enclosure;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static class ComponentCreationTask extends InstantiationTask.Producer<ComponentCreationTask>
	{
		private ComponentAssembly assembly;
		private RepaintDirective.Host repaintHost;
		ComponentEnclosure enclosure = null;

		public ComponentCreationTask(SynchronizationRole role, String description, ComponentAssembly assembly, RepaintDirective.Host repaintHost)
		{
			super(role, description);
			this.assembly = assembly;
			this.repaintHost = repaintHost;
		}

		@Override
		protected void execute()
		{
			enclosure = ComponentFactory.createComponent(assembly, repaintHost);
		}
	}
}
