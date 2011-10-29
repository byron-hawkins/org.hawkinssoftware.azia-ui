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

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeMouseOverDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellContext.CellContextImpl;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <DataType>
 *            the generic type
 * @author Byron Hawkins
 */
public abstract class AbstractCellStamp<DataType> implements CellStamp<DataType>, CompositionElement.Initializing
{
	private Map<RowAddress, InteractiveCell> interactiveCells = new HashMap<RowAddress, InteractiveCell>();

	private RepaintHandler repaintHandler = null;

	@InvocationConstraint(domains = RenderingDomain.class)
	protected abstract void paint(RowAddress address, DataType datum, InteractiveCell interactiveCell);

	// TODO: might be sorta nice to try a mini-composition session instead of this hook wiring
	@InvocationConstraint(domains = FlyweightCellDomain.class)
	protected void interactiveCellCreated(InteractiveCell cell)
	{
	}
	
	@Override
	public void compositionCompleted()
	{
		repaintHandler = CompositionRegistry.getService(RepaintHandler.class);
	}
	
	@Override
	public final void paint(Canvas c, RowAddress address, DataType datum)
	{
		InteractiveCell interactiveCell = interactiveCells.get(address);
		paint(address, datum, interactiveCell);
	}

	@Override
	public final MouseAware getMouseAwareCellHandle(RowAddress address, DataType datum, int xCell, int yCell)
	{
		InteractiveCell mouseHandle = interactiveCells.get(address);
		if (mouseHandle == null)
		{
			CellContextImpl<DataType> cellContext = new CellContextImpl<DataType>(address, datum, repaintHandler);
			mouseHandle = new InteractiveCell(cellContext);
			interactiveCellCreated(mouseHandle);
			interactiveCells.put(address, mouseHandle);
		}
		else
		{
			mouseHandle.cellContext.setDatum(datum);
		}
		mouseHandle.cellContext.setAbsoluteCellOrigin(xCell, yCell);
		return mouseHandle;
	}
	
	public <PluginType extends CellPlugin> PluginType getCellPlugin(RowAddress address, CellPluginKey<PluginType> key)
	{
		InteractiveCell cell = interactiveCells.get(address);
		if (cell == null)
		{
			return null;
		}
		return cell.getPlugin(key);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ValidateRead
	@ValidateWrite
	@VisibilityConstraint(domains = FlyweightCellDomain.class)
	@InvocationConstraint(domains = FlyweightCellDomain.class)
	@DomainRole.Join(membership = { UserInterfaceActor.DependentActorDomain.class, FlyweightCellDomain.class })
	public class InteractiveCell extends VirtualComponent implements MouseAware, ChangeComponentStateDirective.Component
	{
		public final CellContextImpl<DataType> cellContext;

		private final Map<CellPluginKey<?>, CellPlugin> plugins = new HashMap<CellPluginKey<?>, CellPlugin>();

		public InteractiveCell(CellContextImpl<DataType> cellContext)
		{
			this.cellContext = cellContext;
			MouseOverState.install(this);
			installHandler(new MouseHandler());
		}

		public void addPlugin(CellPlugin plugin)
		{
			plugins.put(plugin.getKey(), plugin);
		}

		@SuppressWarnings("unchecked")
		public <PluginType extends CellPlugin> PluginType getPlugin(CellPluginKey<PluginType> key)
		{
			return (PluginType) plugins.get(key);
		}

		public void removeMouseHandle(RemoveMouseHandleDirective remove)
		{
			interactiveCells.remove(cellContext.getAddress());
		}

		@Override
		public PaintableActor getActor()
		{
			return this;
		}

		@Override
		public void requestRepaint()
		{
			RepaintRequestManager.requestRepaint(repaintHandler.createRepaintRequest(cellContext.getAddress()));
		}

		@Override
		public String toString()
		{
			return "MouseAwareCellHandle for row " + cellContext.getAddress().row;
		}

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public class MouseHandler implements UserInterfaceHandler
		{
			public void mouseOverChanged(ChangeMouseOverDirective.Notification note, PendingTransaction transaction)
			{
				if (!note.isMouseOver())
				{
					transaction.contribute(new RemoveMouseHandleDirective(InteractiveCell.this));
				}
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = FlyweightCellDomain.class)
	private class RemoveMouseHandleDirective extends UserInterfaceDirective
	{
		RemoveMouseHandleDirective(InteractiveCell actor)
		{
			super(actor);
		}
	}
}
