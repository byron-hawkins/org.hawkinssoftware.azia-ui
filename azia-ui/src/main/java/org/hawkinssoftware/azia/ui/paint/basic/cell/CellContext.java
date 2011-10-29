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

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp.RepaintHandler;
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
@InvocationConstraint(domains = FlyweightCellDomain.class)
@DomainRole.Join(membership = FlyweightCellDomain.class)
public interface CellContext<DataType>
{
	RowAddress getAddress();

	DataType getDatum();

	RepaintAtomRequest createRepaintRequest();

	int x();

	int y();

	CellContext<DataType> translate(int dx, int dy);

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class CellContextImpl<DataType> implements CellContext<DataType>
	{
		final RowAddress address;
		DataType datum;
		final CellStamp.RepaintHandler repaintHandler;

		int x;
		int y;

		public CellContextImpl(RowAddress address, DataType datum, RepaintHandler repaintHandler)
		{
			this.address = address;
			this.datum = datum;
			this.repaintHandler = repaintHandler;
		}

		public RowAddress getAddress()
		{
			return address;
		}

		public DataType getDatum()
		{
			return datum;
		}
		
		public void setDatum(DataType datum)
		{
			this.datum = datum;
		}

		public RepaintAtomRequest createRepaintRequest()
		{
			return repaintHandler.createRepaintRequest(address);
		}

		public int x()
		{
			return x;
		}

		public int y()
		{
			return y;
		}

		public CellContext<DataType> translate(int dx, int dy)
		{
			return new Translation<DataType>(this, dx, dy);
		}

		void setAbsoluteCellOrigin(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Translation<DataType> implements CellContext<DataType>
	{
		private final CellContext<DataType> original;

		private final int dx;
		private final int dy;

		public Translation(CellContext<DataType> original, int dx, int dy)
		{
			this.original = original;

			this.dx = dx;
			this.dy = dy;
		}

		@Override
		public RowAddress getAddress()
		{
			return original.getAddress();
		}

		@Override
		public DataType getDatum()
		{
			return original.getDatum();
		}

		@Override
		public RepaintAtomRequest createRepaintRequest()
		{
			return original.createRepaintRequest();
		}

		@Override
		public int x()
		{
			return original.x() + dx;
		}

		@Override
		public int y()
		{
			return original.y() + dy;
		}

		@Override
		public CellContext<DataType> translate(int dx, int dy)
		{
			return original.translate(this.dx + dx, this.dy + dy);
		}
	}
}
