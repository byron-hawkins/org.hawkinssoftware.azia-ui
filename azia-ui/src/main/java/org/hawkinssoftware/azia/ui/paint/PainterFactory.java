package org.hawkinssoftware.azia.ui.paint;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.PainterCompositionDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
@DomainRole.Join(membership = { PainterCompositionDomain.class, RenderingDomain.class })
public abstract class PainterFactory
{
	public abstract <ComponentType extends AbstractComponent> InstancePainter<ComponentType> getComponentPainter(Class<? extends ComponentType> componentType);

	public abstract <RegionType extends BoundedEntity.PanelRegion> RegionPainter<RegionType> getRegionPainter(Class<? extends RegionType> regionType);
}
