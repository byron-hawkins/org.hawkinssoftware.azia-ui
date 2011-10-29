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

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.PainterCompositionDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = PainterCompositionDomain.class)
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class })
public abstract class ComponentPainter<ComponentType extends AbstractComponent> implements InstancePainter<ComponentType>, UserInterfaceHandler,
		UserInterfaceActorDelegate, CompositionElement.Initializing
{
	// TODO: can't validate read on this field, because it is the actor for which the lock is registered. Make it final?
	protected ComponentType component;

	protected ComponentPainter()
	{
	}

	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		return BoundedEntity.MaximumSize.NONE;
	}

	@Override
	public ComponentType getComponent()
	{
		return component;
	}

	@Override
	public void setComponent(ComponentType component)
	{
		this.component = component;
		component.installHandler(this);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return component;
	}
	
	@Override
	public void compositionCompleted()
	{
	}
}
