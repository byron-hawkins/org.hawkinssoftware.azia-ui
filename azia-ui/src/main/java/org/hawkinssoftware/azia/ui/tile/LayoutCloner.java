package org.hawkinssoftware.azia.ui.tile;

import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(domains = TileLayoutDomain.class)
@DomainRole.Join(membership = TileLayoutDomain.class)
public class LayoutCloner
{
	public static <KeyType extends LayoutEntity.Key<KeyType>> void cloneLayout(TopTile<KeyType> original, TopTile<KeyType> clone)
	{
		// TODO: write validation misses this collection put
		clone.entitiesByKey.putAll(original.entitiesByKey);
		clone.bounds = original.bounds;
		clone.unit = cloneUnit(original.unit);
	}

	private static <KeyType extends LayoutEntity.Key<KeyType>> LayoutUnit<KeyType> cloneUnit(LayoutUnit<KeyType> original)
	{
		if (original == null)
		{
			return null;
		}

		if (original instanceof ComponentTile)
		{
			ComponentTile<KeyType> clone = new ComponentTile<KeyType>(original.getKey());
			ComponentTile<KeyType> originalContainer = (ComponentTile<KeyType>) original;
			clone.component = originalContainer.component;
			clone.bounds = originalContainer.bounds;
			return clone;
		}
		else if (original instanceof PairTile)
		{
			return clonePair((PairTile<KeyType>) original);
		}
		else
		{
			throw new IllegalStateException("Unknown LayoutUnit of type " + original.getClass().getName());
		}
	}

	private static <KeyType extends LayoutEntity.Key<KeyType>> LayoutTile<KeyType> cloneTile(LayoutTile<KeyType> original)
	{
		if (original == null)
		{
			return null;
		}

		if (original instanceof PairTile)
		{
			return clonePair((PairTile<KeyType>) original);
		}
		else if (original instanceof UnitTile)
		{
			UnitTile<KeyType> originalUnit = (UnitTile<KeyType>) original;
			UnitTile<KeyType> clone = new UnitTile<KeyType>(original.getKey());
			clone.unit = cloneUnit(originalUnit.unit);
			clone.xLayout = originalUnit.xLayout;
			clone.yLayout = originalUnit.yLayout;
			return clone;
		}
		else
		{
			throw new IllegalStateException("Unknown LayoutTile of type " + original.getClass().getName());
		}
	}

	private static <KeyType extends LayoutEntity.Key<KeyType>> PairTile<KeyType> clonePair(PairTile<KeyType> original)
	{
		if (original == null)
		{
			return null;
		}

		PairTile<KeyType> clone = new PairTile<KeyType>(original.getKey(), original.axis);
		clone.first = cloneTile(original.first);
		clone.second = cloneTile(original.second);
		clone.crossExpansion = original.crossExpansion;
		clone.bounds = original.bounds;
		return clone;
	}
}
