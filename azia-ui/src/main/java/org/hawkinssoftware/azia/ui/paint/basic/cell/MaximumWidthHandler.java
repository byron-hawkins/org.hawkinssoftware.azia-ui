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
package org.hawkinssoftware.azia.ui.paint.basic.cell;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry.CompositionInitializationDomain;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class,
		CompositionInitializationDomain.class })
public class MaximumWidthHandler implements UserInterfaceHandler, CompositionElement.Initializing, UserInterfaceActorDelegate
{
	@ValidateRead
	@ValidateWrite
	private int maxWidth = -1;

	private UpdateWidthDirective updateWidthDirective;

	private ListDataModel model;
	private CellStamp.Factory stampFactory;
	private CellViewportComposite<ListModelPainter> viewport;

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		model = CompositionRegistry.getService(ListDataModel.class);
		stampFactory = CompositionRegistry.getService(CellStamp.Factory.class);
		viewport = CompositionRegistry.getComposite(CellViewportComposite.class);
		updateWidthDirective = new UpdateWidthDirective();
		
		viewport.installHandler(this);
	}

	int getMaxWidth()
	{
		return maxWidth;
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent();
	}
	
	private <DataType> void revalidateWidth()
	{
		maxWidth = 0;
		for (int i = 0; i < model.getRowCount(Section.SCROLLABLE); i++)
		{
			RowAddress address = viewport.createAddress(i, Section.SCROLLABLE);
			@SuppressWarnings("unchecked")
			DataType datum = (DataType) model.get(address);
			CellStamp<DataType> stamp = stampFactory.getStamp(address, datum);
			int width = stamp.getSpan(Axis.H, datum);
			if (width > maxWidth)
			{
				maxWidth = width;
			}
		}
	}

	public void dataChanging(ListDataModel.DataChangeNotification dataChange, PendingTransaction transaction)
	{
		if (!updateWidthDirective.active)
		{
			transaction.contribute(updateWidthDirective);
		}

		switch (dataChange.type)
		{
			case ADD:
			{
				updateWidthDirective.addNewRow(dataChange.datum.toString());
				break;
			}
			case REMOVE:
			case REPLACE:
				updateWidthDirective.setRevalidate();
				break;
			default:
				throw new UnknownEnumConstantException(dataChange.type);
		}

		viewport.getCellPainter().repaint(dataChange.address);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	private class UpdateWidthDirective extends UserInterfaceDirective
	{
		private final List<String> textAdditions = new ArrayList<String>();
		private boolean revalidate = false;
		private boolean active = false;

		public UpdateWidthDirective()
		{
			super(viewport.getActor());
		}

		void addNewRow(String text)
		{
			textAdditions.add(text);
			active = true;
		}

		void setRevalidate()
		{
			revalidate = true;
			active = true;
		}

		@Override
		public void commit()
		{
			if (revalidate)
			{
				revalidateWidth();
			}
			else
			{
				for (String textAddition : textAdditions)
				{
					int width = TextMetrics.INSTANCE.getSize(textAddition, BoundsType.TEXT).width;
					if (width > maxWidth)
					{
						maxWidth = width;
					}
				}
			}

			revalidate = false;
			textAdditions.clear();
			active = false;
		}
	}
}
