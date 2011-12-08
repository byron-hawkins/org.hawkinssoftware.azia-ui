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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public abstract class AbstractComponent extends VirtualComponent 
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <Type>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = AssemblyDomain.class)
	@InvocationConstraint(domains = AssemblyDomain.class)
	public static class Key<Type extends AbstractComponent>
	{
		public final Class<Type> componentType;

		public Key(Class<Type> componentType)
		{
			this.componentType = componentType;
		}
	}

	private RepaintInstanceDirective repaintAction = new RepaintInstanceDirective(getActor());

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public abstract BoundedEntity.Expansion getExpansion(Axis axis);

	@Override
	public void requestRepaint()
	{
		RepaintRequestManager.requestRepaint(repaintAction);
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setRepaintAction(RepaintInstanceDirective repaintAction)
	{
		this.repaintAction = repaintAction;
	}
}
