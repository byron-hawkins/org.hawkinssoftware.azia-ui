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

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

// TODO: moving a class to a different package causes RNS failures in the evaluation by FQN, b/c it is stale some places
/**
 * An <code>AbstractComponent</code> is composed with an <code>InstancePainter</code> and a set of
 * <code>UserInterfaceHandler</code>s having characteristic behaviors into an <code>AbstractComposite</code>.
 * 
 * @param <ComponentType>
 *            The specific type of composed <code>AbstractComposite</code>.
 * @param <PainterType>
 *            The specific type of <code>InstancePainter</code> which renders the <code>ComponentType</code> for this
 *            <code>AbstractComposite</code>.
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = RenderingDomain.class)
public abstract class AbstractComposite<ComponentType extends AbstractComponent, PainterType extends InstancePainter<? extends ComponentType>> extends
		ComponentEnclosure<ComponentType, PainterType>
{
	/**
	 * General declaration of a transaction participant which responds to and applies size changes. Not really necessary
	 * now that routers are instrumented according to convention.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = TransactionParticipant.class)
	public interface ResizeHandler extends UserInterfaceHandler
	{
		void resize(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction);

		void apply(UserInterfaceDirective action);
	}

	/**
	 * General declaration of a transaction participant which responds to paint requests. Not really necessary now that
	 * routers are instrumented according to convention.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = TransactionParticipant.class)
	public interface PaintHandler extends UserInterfaceHandler
	{
		void paint(PaintIncludeNotification notification, PendingTransaction transaction);
	}

	private BoundedEntity sizeDelegate;

	private final List<Object> services = new ArrayList<Object>();

	protected AbstractComposite(ComponentType component)
	{
		super(component);
	}

	protected AbstractComposite(ComponentType component, PainterType painter)
	{
		super(component, painter);
	}

	public void installService(Object service)
	{
		services.add(service);
	}

	public void uninstallService(Object service)
	{
		services.remove(service);
	}

	public void installPainter(PainterType painter)
	{
		super.installPainter(painter);
		installService(painter);
	}

	@SuppressWarnings("unchecked")
	public <ServiceType> ServiceType getService(Class<ServiceType> serviceType)
	{
		if (serviceType.isAssignableFrom(AbstractComponent.class))
		{
			return (ServiceType) component;
		}

		for (Object service : services)
		{
			if (serviceType.isAssignableFrom(service.getClass()))
			{
				return (ServiceType) service;
			}

			if (service instanceof AbstractComposite)
			{
				ServiceType nestedService = ((AbstractComposite<?, ?>) service).getService(serviceType);
				if (nestedService != null)
				{
					return nestedService;
				}
			}
		}

		return null;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void installSizeDelegate(BoundedEntity sizeDelegate)
	{
		this.sizeDelegate = sizeDelegate;
	}

	@Override
	public final Expansion getExpansion(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getExpansion(axis);
		}
		else
		{
			return sizeDelegate.getExpansion(axis);
		}
	}

	@Override
	public final int getPackedSize(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getPackedSize(axis);
		}
		else
		{
			return sizeDelegate.getPackedSize(axis);
		}
	}

	@Override
	public final BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getMaxSize(axis);
		}
		else
		{
			return sizeDelegate.getMaxSize(axis);
		}
	}
}
