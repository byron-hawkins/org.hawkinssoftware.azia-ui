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
package org.hawkinssoftware.azia.ui.component.scalar.model;

import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelCellViewport;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
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
