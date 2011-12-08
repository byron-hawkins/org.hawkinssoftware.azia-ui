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
package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellContentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class CellViewport extends ScrollPaneViewport
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <CellPainterType>
	 *            the generic type
	 * @param <ViewportCompositeType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static abstract class Assembly<CellPainterType extends AbstractCellContentPainter, ViewportCompositeType extends CellViewportComposite<CellPainterType>>
			extends ComponentAssembly<CellViewport, CellViewport.Painter, ViewportCompositeType>
	{
		public <ViewportType extends CellViewport> Assembly(Class<ViewportType> viewportType, Class<ViewportCompositeType> compositeType)
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent(new AbstractComponent.Key<ViewportType>(viewportType));
			setEnclosure(new ComponentEnclosure.Key(compositeType));
		}

		protected abstract CellPainterType createCellPainter();

		@Override
		public void assemble(ViewportCompositeType enclosure)
		{
			super.assemble(enclosure);

			CellPainterType cellContentPainter = createCellPainter();
			enclosure.setCellPainter(cellContentPainter);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Painter extends ScrollPaneViewport.Painter
	{
		// marker
	}

	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private CellViewportComposite<?> viewport;

	@InvocationConstraint
	public CellViewport()
	{
		installHandler(new MouseHandler());
	}

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		viewport = CompositionRegistry.getComposite(CellViewportComposite.class);
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FILL;
	}

	// the coordinates need to be adjusted for the ScrollPaneViewport(CellViewport) (x,y). The viewport doesn't
	// intervene in this call, it is as if this handler were directly on the viewport. Trouble is, I can't see the
	// viewport's scroll position from here... In general, a painter is allowed to see its component.
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class MouseHandler implements UserInterfaceHandler
	{
		public void mouseStateChange(final EventPass pass, final PendingTransaction transaction)
		{
			new ForwardTask() {
				@Override
				protected void execute()
				{
					MouseAware cellHandle = viewport.getCellPainter().getMouseAwareCellHandle(pass);
					if (cellHandle != null)
					{
						transaction.contribute(new Forward(cellHandle));
					}
				}
			}.start();
		}

		@DomainRole.Join(membership = { TransactionParticipant.class, FlyweightCellDomain.class })
		private abstract class ForwardTask extends InstantiationTask.SubordinateTask
		{
			ForwardTask()
			{
				super(CellViewport.this);
			}
		}
	}
}
