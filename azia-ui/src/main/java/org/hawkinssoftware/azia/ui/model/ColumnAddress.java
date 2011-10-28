package org.hawkinssoftware.azia.ui.model;

public class ColumnAddress
{
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
