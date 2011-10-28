package org.hawkinssoftware.azia.ui.tile;

/**
 * A layout entity that can be held within a single UnitTile.
 * 
 * @author b
 */
public interface LayoutUnit<KeyType extends LayoutEntity.Key<KeyType>> extends LayoutEntity<KeyType>
{
	public interface Floater<KeyType extends LayoutEntity.Key<KeyType>> extends LayoutUnit<KeyType>
	{
		public enum Edge
		{
			LEFT,
			RIGHT;
		}
		
		Edge getEdge();
	}
}
