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
package org.hawkinssoftware.azia.ui.model;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentIdentifier;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@VisibilityConstraint(domains = { RenderingDomain.class, FlyweightCellDomain.class })
public class RowAddress extends ComponentIdentifier
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public enum Section
	{
		NORTH,
		SCROLLABLE,
		SOUTH;

		public boolean isStatic()
		{
			return this != SCROLLABLE;
		}
	}

	public final int row;
	public final Section section;

	public RowAddress(AbstractComponent component, int row, Section section)
	{
		super(component);

		this.row = row;
		this.section = section;
	}

	public boolean hasRow()
	{
		return row >= 0;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + row;
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RowAddress other = (RowAddress) obj;
		if (row != other.row)
			return false;
		if (section != other.section)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "RowAddress (" + row + ", " + section + ")";
	}
}
