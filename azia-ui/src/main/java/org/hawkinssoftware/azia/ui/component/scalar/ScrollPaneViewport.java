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

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ViewportStateChangeHandler;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public abstract class ScrollPaneViewport extends AbstractComponent
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Painter
	{
		// marker
	}

	protected int xViewport;
	protected int yViewport;

	private final ViewportStateChangeHandler stateHandler = new ViewportStateChangeHandler(this);

	@InvocationConstraint
	public ScrollPaneViewport()
	{
		installHandler(stateHandler);
	}

	@InvocationConstraint(domains = { DisplayBoundsDomain.class, RenderingDomain.class })
	public int xViewport()
	{
		return xViewport;
	}

	@InvocationConstraint(domains = { DisplayBoundsDomain.class, RenderingDomain.class })
	public int yViewport()
	{
		return yViewport;
	}

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	public void setViewportPosition(int x, int y)
	{
		xViewport = x;
		yViewport = y;
	}
}
