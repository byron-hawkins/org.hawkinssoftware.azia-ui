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
package org.hawkinssoftware.azia.ui.paint.basic.cell;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class })
public abstract class AbstractCellContentPainter implements UserInterfaceHandler, UserInterfaceActorDelegate, ScrollPaneViewport.Painter,
		CompositionElement.Initializing
{
	protected ScrollPaneViewportComposite<CellViewport, ?> viewport;

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		viewport = CompositionRegistry.getComposite(ScrollPaneViewportComposite.class);
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public abstract int getScrollableContentSize(Axis axis);

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public abstract int getStaticContentSpan(Axis axis);

	// assumes g is clipped
	@InvocationConstraint(domains = RenderingDomain.class)
	public abstract void paint();

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	public abstract MouseAware getMouseAwareCellHandle(EventPass event);

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent();
	}
}
