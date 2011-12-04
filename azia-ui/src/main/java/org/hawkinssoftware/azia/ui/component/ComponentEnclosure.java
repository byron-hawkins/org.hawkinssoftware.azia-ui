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

import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery;
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
	public class BoundsChangeHandler implements UserInterfaceHandler, UserInterfaceActorPreview
	{
		public void applyBoundsChange(ComponentBoundsChangeDirective change)
		{
			bounds = change.bounds.applyValues(bounds);
		}

		@Override
		public boolean affects(UserInterfaceTransactionQuery.Property<?, ?> property)
		{
			return property.matches("getBounds");
		}

		@SuppressWarnings("unchecked")
		public <T> T getPreview(UserInterfaceDirective action, T value)
		{
			return (T) ((ComponentBoundsChangeDirective) action).bounds.applyValues((EnclosureBounds) value);
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
	public <HandlerType extends ComponentDataHandler> HandlerType getDataHandler(ComponentDataHandler.Key<HandlerType> key)
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
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(VisibilityProperty.INSTANCE).getValue();
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

	private EnclosureBounds getCurrentBounds()
	{
		if (!isVisible())
		{
			return EnclosureBounds.EMPTY;
		}
		return bounds;
	}

	public EnclosureBounds getBounds()
	{
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(BoundsProperty.INSTANCE).getValue();
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

	private static class VisibilityProperty extends UserInterfaceTransactionQuery.Property<ComponentEnclosure<?, ?>, Boolean>
	{
		private static final VisibilityProperty INSTANCE = new VisibilityProperty();

		private VisibilityProperty()
		{
			super("isVisible");
		}

		@Override
		public Boolean getCurrentValue(ComponentEnclosure<?, ?> parentValue)
		{
			return parentValue.visible;
		}
	}

	private static class BoundsProperty extends UserInterfaceTransactionQuery.Property<ComponentEnclosure<?, ?>, EnclosureBounds>
	{
		private static final BoundsProperty INSTANCE = new BoundsProperty();

		private BoundsProperty()
		{
			super("getBounds");
		}

		@Override
		public EnclosureBounds getCurrentValue(ComponentEnclosure<?, ?> parentValue)
		{
			return parentValue.getCurrentBounds();
		}
	}
}
