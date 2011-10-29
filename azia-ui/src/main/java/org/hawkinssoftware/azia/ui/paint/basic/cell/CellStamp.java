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


import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomRequest;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <DataType>
 *            the generic type
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class, DisplayBoundsDomain.class })
public interface CellStamp<DataType> extends CompositionElement
{
	public static final int ROW_HEIGHT = InstancePainter.TextMetrics.INSTANCE.getSize("|", BoundsType.TEXT).height;
	public static final int TEXT_BASELINE = InstancePainter.TextMetrics.INSTANCE.getTypicalBaseline(ROW_HEIGHT);
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class })
	public interface Factory extends CompositionElement
	{
		@InvocationConstraint(domains = FlyweightCellDomain.class)
		<DataType> CellStamp<DataType> getStamp(RowAddress address, DataType datum);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = FlyweightCellDomain.class)
	@DomainRole.Join(membership = { RenderingDomain.class, FlyweightCellDomain.class })
	public interface RepaintHandler
	{
		RepaintAtomRequest createRepaintRequest(RowAddress address);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = FlyweightCellDomain.class)
	@DomainRole.Join(membership = FlyweightCellDomain.class)
	public interface CellPlugin extends CompositionElement
	{
		CellPluginKey<? extends CellPlugin> getKey();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <PluginType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = FlyweightCellDomain.class)
	public static class CellPluginKey<PluginType>
	{
		public CellPluginKey()
		{
		}
	}
	
	@InvocationConstraint(domains = RenderingDomain.class)
	void paint(Canvas c, RowAddress address, DataType datum);

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	int getSpan(Axis axis, DataType datum);

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	MouseAware getMouseAwareCellHandle(RowAddress address, DataType datum, int xCell, int yCell);

	@InvocationConstraint(domains = FlyweightCellDomain.class)
	<PluginType extends CellPlugin> PluginType getCellPlugin(RowAddress address, CellPluginKey<PluginType> key);
}
