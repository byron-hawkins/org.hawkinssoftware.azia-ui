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
package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPaneViewportPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <ViewportType>
 *            the generic type
 * @param <PainterType>
 *            the generic type
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = ScrollPaneViewportComposite.ScrollPaneViewportDomain.class)
public class ScrollPaneViewportComposite<ViewportType extends ScrollPaneViewport, PainterType extends ScrollPaneViewportPainter<ViewportType>> extends
		AbstractComposite<ViewportType, PainterType>
{  
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class ScrollPaneViewportDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final ScrollPaneViewportDomain INSTANCE = new ScrollPaneViewportDomain();
	}

	@InvocationConstraint
	public ScrollPaneViewportComposite(ViewportType component)
	{
		super(component);
	}

	public int getScrollableSpan(Axis axis)
	{
		return bounds.getSpan(axis) - getPainter().getStaticContentSpan(axis);
	}
}    
