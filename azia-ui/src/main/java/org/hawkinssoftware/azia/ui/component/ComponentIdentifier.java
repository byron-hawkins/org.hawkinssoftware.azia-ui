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

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public abstract class ComponentIdentifier
{
	private final AbstractComponent component;

	protected ComponentIdentifier(AbstractComponent component)
	{
		this.component = component;
	}
	
	public AbstractComponent getComponent()
	{
		return component;
	}
}
