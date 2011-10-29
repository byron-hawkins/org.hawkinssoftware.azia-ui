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

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ColumnAddress
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public enum Section
	{
		WEST,
		SCROLLABLE,
		EAST;

		public boolean isStatic()
		{
			return this != SCROLLABLE;
		}
	}

	public final int column;
	public final Section section;

	public ColumnAddress(int column, Section section)
	{
		this.column = column;
		this.section = section;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
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
		ColumnAddress other = (ColumnAddress) obj;
		if (column != other.column)
			return false;
		if (section != other.section)
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ColumnAddress (" + column + ", " + section + ")";
	}
}
