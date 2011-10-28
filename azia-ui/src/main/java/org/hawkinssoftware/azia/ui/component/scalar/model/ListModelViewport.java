package org.hawkinssoftware.azia.ui.component.scalar.model;

import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelCellViewport;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = ListDataModel.ModelListDomain.class)
public class ListModelViewport extends CellViewportComposite<ListModelPainter>
{
	public ListModelViewport(ListModelCellViewport component)
	{
		super(component);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <ServiceType> ServiceType getService(Class<ServiceType> serviceType)
	{
		if (serviceType.isAssignableFrom(ListModelPainter.class))
		{
			return (ServiceType) getCellPainter();
		}
		return super.getService(serviceType);
	}
}
