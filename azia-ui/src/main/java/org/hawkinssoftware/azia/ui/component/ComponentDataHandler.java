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
package org.hawkinssoftware.azia.ui.component;

import java.util.Collection;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public abstract class ComponentDataHandler implements UserInterfaceHandler
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <HandlerType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Key<HandlerType extends ComponentDataHandler>
	{
	}
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Host
	{
		<HandlerType extends ComponentDataHandler> HandlerType getDataHandler(ComponentDataHandler.Key<HandlerType> key);

		Collection<ComponentDataHandler> getDataHandlers();
	}
	
	public final Key<?> key;

	protected ComponentDataHandler(Key<?> key)
	{
		this.key = key;
	}
}
