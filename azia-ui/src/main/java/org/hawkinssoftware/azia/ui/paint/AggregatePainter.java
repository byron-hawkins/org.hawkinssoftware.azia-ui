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

import java.util.Collection;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = RenderingDomain.class)
public interface AggregatePainter<ComponentType extends AbstractComponent> extends InstancePainter<ComponentType>
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = RenderingDomain.class)
	@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class })
	public interface Atom extends UserInterfaceActorDelegate
	{
		void paint();
	}

	void paint(ComponentType component, Collection<AggregatePainter.Atom> atoms);
}
