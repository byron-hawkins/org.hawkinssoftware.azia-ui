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
package org.hawkinssoftware.azia.ui.paint.basic.scalar;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.MoveViewportOriginDirective;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <ViewportType>
 *            the generic type
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public abstract class ScrollPaneViewportPainter<ViewportType extends ScrollPaneViewport> extends ComponentPainter<ViewportType>
{
	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public abstract int getScrollableContentSize(Axis axis);

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public abstract int getStaticContentSpan(Axis axis);

	@Override
	public BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		return new BoundedEntity.MaximumSize(getScrollableContentSize(axis));
	}

	// RNS: protect these handler methods? Pattern match in domains.xml?
	public void viewportMoving(MoveViewportOriginDirective.Notification notification, PendingTransaction transaction)
	{
		component.requestRepaint();
	}
}
