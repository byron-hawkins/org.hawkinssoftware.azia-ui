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
package org.hawkinssoftware.azia.ui.tile;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * Defines an abstract layout region, which is known only to have an EnclosureBounds.
 * 
 * @author Byron Hawkins
 * 
 * @JTourBusStop 2, Defining the TileLayoutDomain and its scope, Members of the TileLayoutDomain:
 * 
 *               The @DomainRole.Join annotation makes the LayoutRegion a member of the TileLayoutDomain. Membership is
 *               inherited by all subclasses and interface implementors, so this annotation causes the entire hierarchy
 *               of LayoutRegion to belong to the TileLayoutDomain and also the DisplayBoundsDomain.
 */
@VisibilityConstraint(domains = LayoutRegion.TileLayoutDomain.class, inherit = true)
@DomainRole.Join(membership = LayoutRegion.TileLayoutDomain.class)
public interface LayoutRegion extends MouseAware, BoundedEntity.PanelRegion, UserInterfaceActor
{
	/**
	 * The domain of classes taking responsibility for assigning size and position to instances of LayoutRegion.
	 * 
	 * @author Byron Hawkins
	 * 
	 * @JTourBusStop 1, Defining the TileLayoutDomain and its scope, Declaring the TileLayoutDomain:
	 * 
	 *               The TileLayoutDomain is declared here as a subclass of the DisplayBoundsDomain, meaning that every
	 *               member of the TileLayoutDomain is also a member of the DisplayBoundsDomain. By itself, the
	 *               TileLayoutDomain declaration doesn't have any meaning; it is effectively a marker.
	 */
	public static class TileLayoutDomain extends DisplayBoundsDomain
	{
		@DomainRole.Instance
		public static final TileLayoutDomain INSTANCE = new TileLayoutDomain();
	}

	EnclosureBounds getBounds();
}
