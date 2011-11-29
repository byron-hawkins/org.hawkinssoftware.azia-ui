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

import java.util.Collection;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.PainterCompositionDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintComponentNotification;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.publication.ExtensionConstraint;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @param <PainterType>
 *            the generic type
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { DisplayBoundsDomain.class, RenderingDomain.class })
public class ComponentEnclosure<ComponentType extends AbstractComponent, PainterType extends InstancePainter<? extends ComponentType>> implements
		UserInterfaceHandler.Host, ComponentDataHandler.Host, BoundedEntity, PaintableActorDelegate, CompositionElement
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	public class PaintIncludeHandler implements UserInterfaceHandler
	{
		public void paintPosted(PaintIncludeNotification note, PendingTransaction transaction)
		{
			transaction.contribute(new PaintComponentNotification(component, bounds));
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	public class BoundsChangeHandler implements UserInterfaceHandler
	{
		public void applyBoundsChange(ComponentBoundsChangeDirective change)
		{
			bounds = change.bounds.applyValues(bounds);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@SuppressWarnings("rawtypes")
	public static class Key
	{
		public final Class<? extends ComponentEnclosure> enclosureType;

		public Key(Class<? extends ComponentEnclosure> enclosureType)
		{
			this.enclosureType = enclosureType;
		}
	}

	public static final Key SINGULAR = new Key(ComponentEnclosure.class);

	// RNS: special validation for structural member changes?
	protected final ComponentType component;
	private PainterType painter;

	@ValidateRead
	@ValidateWrite
	protected EnclosureBounds bounds = EnclosureBounds.EMPTY;

	@ValidateRead
	@ValidateWrite
	private boolean visible = true;

	private ComponentEnclosure()
	{
		component = null;
	}

	@InvocationConstraint(extendedTypes = ComponentEnclosure.class)
	public ComponentEnclosure(ComponentType component)
	{
		this.component = component;

		installHandler(new PaintIncludeHandler());
		installHandler(new BoundsChangeHandler());
	}

	@InvocationConstraint(extendedTypes = ComponentEnclosure.class)
	public ComponentEnclosure(ComponentType component, PainterType painter)
	{
		this(component);
		installPainter(painter);
	}

	@Override
	@InvocationConstraint(domains = AssemblyDomain.class, extendedTypes = ComponentEnclosure.class, inherit = true)
	public void installHandler(UserInterfaceHandler handler)
	{
		component.installHandler(handler);
	}

	@Override
	public <HandlerType extends UserInterfaceHandler> void changeHandler(HandlerType oldHandler, HandlerType newHandler)
	{
		component.changeHandler(oldHandler, newHandler);
	}

	@Override
	@InvocationConstraint(domains = AssemblyDomain.class, extendedTypes = ComponentEnclosure.class, inherit = true)
	public void removeHandler(UserInterfaceHandler handler)
	{
		component.removeHandler(handler);
	}

	@Override
	public <HandlerType extends ComponentDataHandler> HandlerType getDataHandler(org.hawkinssoftware.azia.ui.component.ComponentDataHandler.Key<HandlerType> key)
	{
		return component.getDataHandler(key);
	}

	@Override
	public Collection<ComponentDataHandler> getDataHandlers()
	{
		return component.getDataHandlers();
	}

	@Override
	public PaintableActor getActor()
	{
		return component;
	}

	// this method is a hack for initializing static dimensions
	public void setBounds(EnclosureBounds bounds)
	{
		this.bounds = bounds;
	}

	@InvocationConstraint(voidInheritance = true)
	public ComponentType getComponent()
	{
		return component;
	}

	public boolean isVisible()
	{
		return visible;
	}

	protected void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public PainterType getPainter()
	{
		return painter;
	}

	@InvocationConstraint(domains = PainterCompositionDomain.class)
	public void installPainter(PainterType painter)
	{
		this.painter = painter;
	}

	public EnclosureBounds getBounds()
	{
		if (!visible)
		{
			return EnclosureBounds.EMPTY;
		}
		return bounds;
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return component.getExpansion(axis);
	}

	@Override
	@ExtensionConstraint(types = { AbstractComposite.class })
	public int getPackedSize(Axis axis)
	{
		return getPainter().getPackedSize(axis);
	}

	@Override
	@ExtensionConstraint(types = { AbstractComposite.class })
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		return getPainter().getMaxSize(axis);
	}
}
