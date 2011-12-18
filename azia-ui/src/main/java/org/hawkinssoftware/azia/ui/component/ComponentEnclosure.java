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
 * A generic encapsulation of an AbstractComponent which binds it to a compatible InstancePainter and maintains its
 * pixel size.
 * 
 * @param <ComponentType>
 *            the type of AbstractComponent contained in this enclosure
 * @param <PainterType>
 *            the type of InstancePainter responsible for rendering this.component
 * @author Byron Hawkins
 * 
 * @JTourBusStop 1, Integration of a class fragment into multiple features, Introducing the ComponentEnclosure:
 * 
 *               In Azia, every component is a subclass of AbstractComponent and has no size, state or functionality.
 *               The latter two are fulfilled by ComponentDataHandler and UserInterfaceHandler, respectively, and are
 *               plugged in via AbstractComponent.installHandler(). At assembly time, every AbstractComponent is wrapped
 *               in a ComponentEnclosure, which maintains its size (in the "bounds" field). All user interface entities
 *               needing information about the size of an AbstractComponent queries its ComponentEnclosure.getBounds().
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
		return component.getActor();
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

	/**
	 * @JTourBusStop 2, Integration of a class fragment into multiple features, ComponentEnclosure.getBounds():
	 * 
	 *               According to RNS assertion #6, every consumer of this method belongs to an implicit layer of code
	 *               which could be referred to as the "ComponentEnclosure bounds consumers." Furthermore, there must be
	 *               some kind of cohesion amongst these consumers, and it must occur in terms of this getBounds()
	 *               method. The ComponentEnclosure is a fundamental base type of Azia, so the domain of getBounds()
	 *               consumers is open to client code; therefore no cohesion can be made on the basis of set members,
	 *               rather it must be drawn from homogeneity of meaning given to the return value. So the domain of
	 *               consumers is cohesive on the basis that they all make the same meaning out of the returned
	 *               EnclosureBounds; specifically, that it refers to the position and size of this.component.
	 * 
	 *               As a developer, it is very simple to comprehend the domain of getBounds() consumers: it is a set of
	 *               classes which regard the returned EnclosureBounds as the position and size of this.component. Any
	 *               alternative usage will break the cohesion, which depends solely on the meaning made from the
	 *               returned EnclosureBounds; therefore it is implicitly illegal for any class to call getBounds() and
	 *               regard the return value as anything other than the position and size of this.component.
	 */
	public EnclosureBounds getBounds()
	{
		// Fetch the bounds via query, in case the caller is in a transaction which has changed the value without yet
		// committing it. If there is no current transaction, or if the bounds have not changed in the current
		// transaction, the heap-persisted value is returned via getCurrentBounds().
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
