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
import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery.Property;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry.CompositionInitializationDomain;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
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
 * Maintains the maximum width for the content of a model-based cell viewport, such as a scrollable list.
 * 
 * @author Byron Hawkins
 * 
 * @JTourBusStop 1, Concurrency invariance with @ValidateRead and @ValidateWrite, Introducing the MaximumWidthHandler:
 * 
 *               A scrollpane viewport having cellular content must maintain the maximum width of its content for the
 *               benefit of the scroll bars of the enclosing scroll pane. This handler owns a cached calculation of the
 *               width in its maxWidth field. The annotations @ValidateRead and @ValidateWrite indicate that a
 *               centralized agent will govern concurrency for the maxWidth field. The RNS bytecode instrumentation
 *               agent inserts a pointcut from every "get" and "put" instruction to the associated agent, which is
 *               registered in ValidateWrite.ValidationAgent.
 */
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class, FlyweightCellDomain.class, ModelListDomain.class,
		CompositionInitializationDomain.class })
public class MaximumWidthHandler implements UserInterfaceHandler, CompositionElement.Initializing, UserInterfaceActorDelegate, UserInterfaceActorPreview
{
	/**
	 * @JTourBusStop 4, Concurrency invariance with @ValidateRead and @ValidateWrite, Conclusion:
	 * 
	 *               Having already established a comprehensive concurrency policy for the entire Azia library in the
	 *               FieldAccessLockValidator and related classes, thread-sensitive fields such as this maxWidth need
	 *               only be annotated with @ValidateRead and @ValidateWrite. This cohesion is not as effective as
	 *               having all the thread-sensitive fields encapsulated within a single class, but it does simulate
	 *               many of the benefits of that optimal scenario.
	 */
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

	/**
	 * @JTourBusStop 3.2, Concurrency invariance with @ValidateRead and @ValidateWrite, Entry point for the instrumented
	 *               "get":
	 * 
	 *               External classes interested in the maximum width will query this method. The implementation of this
	 *               method is somewhat complicated by the fact that the maxWidth might have a transitory value on the
	 *               current transaction. Skipping over those details for a moment, this UserInterfaceTransactionQuery
	 *               eventually calls the getCurrentValue() method in tour stop 3.1. It will try to read this.maxWidth,
	 *               and the "get" pointcut will be invoked to verify that the necessary semaphore is held by the
	 *               calling thread.
	 * 
	 *               This getMaxWidth() method can be called from any object interested in the horizontal scroll extent
	 *               of the viewport. All consumers need the concurrency policy of the field to be maintained by this
	 *               MaximumWidthHandler, but there is no guarantee that the policy will be compatible with the caller's
	 *               concurrency plan. This creates a high risk of deadlock and such concurrency failures. The value of
	 *               having a centralized concurrency manager like the FieldAccessLockValidator is that it can establish
	 *               a comprehensive concurrency policy for the entire application. In some cases, special concurrency
	 *               handling will be necessary, and these cases simply take the additional requirement that they must
	 *               be compatible with the global concurrency policy.
	 */
	int getMaxWidth()
	{
		return UserInterfaceTransactionQuery.start(this).getTransactionalValue(WidthProperty.INSTANCE).getValue();
	}

	@Override
	public boolean affects(Property<?, ?> property)
	{
		return property.matches("getMaxWidth");
	}

	@SuppressWarnings("unchecked")
	public <T> T getPreview(UserInterfaceDirective action, T value)
	{
		return (T) (Integer) ((UpdateWidthDirective) action).calculateMaxWidth((Integer) value);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent().getActor();
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
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class, ListDataModel.ModelListDomain.class, FlyweightCellDomain.class,
			ScrollPaneViewportComposite.ScrollPaneViewportDomain.class })
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

		private <DataType> int calculateMaxWidth(int currentMaxWidth)
		{
			int maxWidth;
			if (revalidate)
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
			else
			{
				maxWidth = currentMaxWidth;
				for (String textAddition : textAdditions)
				{
					int width = TextMetrics.INSTANCE.getSize(textAddition, BoundsType.TEXT).width;
					if (width > maxWidth)
					{
						maxWidth = width;
					}
				}
			}
			return maxWidth;
		}

		@Override
		public void commit()
		{
			/**
			 * @JTourBusStop 3, Concurrency invariance with @ValidateRead and @ValidateWrite, Instance of an
			 *               instrumented "put":
			 * 
			 *               The enclosing UpdateWidthDirective changes the cached maxWidth calculation of the handler
			 *               in this commit() method (which is an artifact of Azia's transactional architecture). When
			 *               the directive class is loaded into the JVM, the bytecode for this statement will be
			 *               instrumented with a pre-cut to FieldAccessLockValidator.validateWrite().
			 */
			MaximumWidthHandler.this.maxWidth = this.calculateMaxWidth(MaximumWidthHandler.this.maxWidth);

			revalidate = false;
			textAdditions.clear();
			active = false;
		}
	}

	private static class WidthProperty extends UserInterfaceTransactionQuery.Property<MaximumWidthHandler, Integer>
	{
		static final WidthProperty INSTANCE = new WidthProperty();

		public WidthProperty()
		{
			super("getMaxWidth");
		}

		protected Integer getCurrentValue(MaximumWidthHandler parentValue)
		{
			/**
			 * @JTourBusStop 3.1, Concurrency invariance with @ValidateRead and @ValidateWrite, Instance of an
			 *               instrumented "get":
			 * 
			 *               The enclosing WidthProperty reads the cached maxWidth calculation of the handler in this
			 *               method. When the property class is loaded into the JVM, the bytecode for this statement
			 *               will be instrumented with a pre-cut to FieldAccessLockValidator.validateRead().
			 */
			return parentValue.maxWidth;
		}
	}
}
