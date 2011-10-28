package org.hawkinssoftware.azia.ui.paint.basic.cell;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class })
public class ListModelCellViewport extends CellViewport 
{
}
