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
package org.hawkinssoftware.azia.ui.paint;

import java.util.Map;
import java.util.WeakHashMap;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.PainterCompositionDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.paint.basic.BasicPainterFactory;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = AssemblyDomain.class)
@VisibilityConstraint(domains = { AssemblyDomain.class, RenderingDomain.class })
@DomainRole.Join(membership = { PainterCompositionDomain.class, RenderingDomain.class })
public final class PainterRegistry
{
	private static PainterRegistry INSTANCE;

	@InvocationConstraint(domains = { AssemblyDomain.class, RenderingDomain.class })
	public static PainterRegistry getInstance()
	{
		synchronized (PainterRegistry.class)
		{
			if (INSTANCE == null)
			{
				// hack: basic factory? which?
				INSTANCE = new PainterRegistry(new BasicPainterFactory());
			}
			return INSTANCE;
		}
	}

	private final PainterFactory factory;
	private final Map<AbstractComponent, InstancePainter<?>> componentPainters = new WeakHashMap<AbstractComponent, InstancePainter<?>>();
	private final Map<BoundedEntity.PanelRegion, RegionPainter<?>> regionPainters = new WeakHashMap<BoundedEntity.PanelRegion, RegionPainter<?>>();

	private PainterRegistry(PainterFactory factory)
	{
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(domains = RenderingDomain.class)
	public <ComponentType extends AbstractComponent> InstancePainter<ComponentType> getPainter(ComponentType component)
	{
		return (InstancePainter<ComponentType>) componentPainters.get(component);
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(domains = RenderingDomain.class)
	public <RegionType extends BoundedEntity.PanelRegion> RegionPainter<RegionType> getPainter(RegionType region)
	{
		RegionPainter<RegionType> painter = (RegionPainter<RegionType>) regionPainters.get(region);
		if (painter == null)
		{
			painter = (RegionPainter<RegionType>) factory.getRegionPainter((Class<RegionType>) region.getClass());
			regionPainters.put(region, painter);
		}
		return painter;
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(types = ComponentAssembly.class)
	public <ComponentType extends AbstractComponent, PainterType extends InstancePainter<ComponentType>> void installPainter(
			ComponentEnclosure<ComponentType, PainterType> enclosure)
	{
		ComponentType component = enclosure.getComponent();
		PainterType painter = (PainterType) componentPainters.get(component);
		if (painter == null)
		{
			painter = (PainterType) factory.getComponentPainter((Class<ComponentType>) component.getClass());

			if (painter == null)
			{
				throw new IllegalArgumentException("No painter is registered for " + component.getClass().getName());
			}

			componentPainters.put(component, painter);
			painter.setComponent(component);
		}
		enclosure.installPainter(painter);
	}

	@SuppressWarnings("unchecked")
	@InvocationConstraint(types = ComponentAssembly.class)
	public <ComponentType extends AbstractComponent, PainterType extends InstancePainter<ComponentType>> void installPainter(
			ComponentEnclosure<ComponentType, PainterType> enclosure, InstancePainter.Key<?, ?> painterKey)
	{
		try
		{
			PainterType painter = (PainterType) painterKey.painterType.newInstance();
			componentPainters.put(enclosure.getComponent(), painter);
			painter.setComponent(enclosure.getComponent());
			enclosure.installPainter(painter);
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException("Failed to create painter of type " + painterKey.painterType.getName(), e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("Failed to create painter of type " + painterKey.painterType.getName(), e);
		}
	}
}
