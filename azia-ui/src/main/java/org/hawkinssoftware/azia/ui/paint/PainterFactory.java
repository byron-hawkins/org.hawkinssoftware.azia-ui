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

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.PainterCompositionDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * A factory for creating Painter objects.
 */
@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
@DomainRole.Join(membership = { PainterCompositionDomain.class, RenderingDomain.class })
public abstract class PainterFactory
{
	public abstract <ComponentType extends AbstractComponent> InstancePainter<ComponentType> getComponentPainter(Class<? extends ComponentType> componentType);

	public abstract <RegionType extends BoundedEntity.PanelRegion> RegionPainter<RegionType> getRegionPainter(Class<? extends RegionType> regionType);
}
