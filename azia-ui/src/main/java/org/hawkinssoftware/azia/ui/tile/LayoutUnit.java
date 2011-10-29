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
package org.hawkinssoftware.azia.ui.tile;

/**
 * A layout entity that can be held within a single UnitTile.
 * 
 * @author b
 */
public interface LayoutUnit<KeyType extends LayoutEntity.Key<KeyType>> extends LayoutEntity<KeyType>
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <KeyType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public interface Floater<KeyType extends LayoutEntity.Key<KeyType>> extends LayoutUnit<KeyType>
	{
		
		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public enum Edge
		{
			LEFT,
			RIGHT;
		}
		
		Edge getEdge();
	}
}
