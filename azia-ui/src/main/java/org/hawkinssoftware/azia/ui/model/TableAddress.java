package org.hawkinssoftware.azia.ui.model;

import java.util.EnumMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { FlyweightCellDomain.class })
public class TableAddress
{
	public static class Section
	{
		private static final Map<RowAddress.Section, Map<ColumnAddress.Section, Section>> sectionsByRowAndColumn = initialize();
		private static final Section SCROLLABLE = sectionsByRowAndColumn.get(RowAddress.Section.SCROLLABLE).get(ColumnAddress.Section.SCROLLABLE);

		private static Map<RowAddress.Section, Map<ColumnAddress.Section, Section>> initialize()
		{
			Map<RowAddress.Section, Map<ColumnAddress.Section, Section>> tableMap = new EnumMap<RowAddress.Section, Map<ColumnAddress.Section, Section>>(
					RowAddress.Section.class);
			for (RowAddress.Section rowSection : RowAddress.Section.values())
			{
				Map<ColumnAddress.Section, Section> columnMap = new EnumMap<ColumnAddress.Section, Section>(ColumnAddress.Section.class);
				for (ColumnAddress.Section columnSection : ColumnAddress.Section.values())
				{
					columnMap.put(columnSection, new Section(rowSection, columnSection));
				}
				tableMap.put(rowSection, columnMap);
			}
			return tableMap;
		}

		static TableAddress.Section forAddress(TableAddress address)
		{
			return sectionsByRowAndColumn.get(address.row.section).get(address.column.section);
		}

		public final RowAddress.Section row;
		public final ColumnAddress.Section column;

		private Section(RowAddress.Section row, ColumnAddress.Section column)
		{
			this.row = row;
			this.column = column;
		}
	}

	public final RowAddress row;
	public final ColumnAddress column;

	public TableAddress(RowAddress row, ColumnAddress column)
	{
		this.row = row;
		this.column = column;
	}

	public Section getSection()
	{
		return Section.forAddress(this);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((row == null) ? 0 : row.hashCode());
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
		TableAddress other = (TableAddress) obj;
		if (column == null)
		{
			if (other.column != null)
				return false;
		}
		else if (!column.equals(other.column))
			return false;
		if (row == null)
		{
			if (other.row != null)
				return false;
		}
		else if (!row.equals(other.row))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "TableAddress (" + row + ", " + column + ")";
	}

}
