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
package org.hawkinssoftware.azia.ui.component.transaction.state;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.input.MouseAware;

/**
 * Generic transaction contribution for changing a stateful characteristic of an <code>AbstractComponent</code>.
 * 
 * @author Byron Hawkins
 */
public abstract class ChangeComponentStateDirective extends UserInterfaceDirective
{
	/**
	 * Generically describes a <code>UserInterfaceHandler</code> which commits a subtype of the enclosing
	 * <code>ChangeComponentStateDirective</code>. This interface is not really necessary now that routers are
	 * instrumented according to handler convention.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Handler extends UserInterfaceHandler
	{
		void applyStateChange(ChangeComponentStateDirective action);
	}

	/**
	 * Generic compositional characteristics of a component for which state changes may be transactionally executed:
	 * painted on the screen, interacts with the mouse, maintains <code>UserInterfaceHandler</code>s and accepts repaint
	 * requests.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Component extends MouseAware, PaintableActor, UserInterfaceHandler.Host
	{
		void requestRepaint();
	}

	public ChangeComponentStateDirective(UserInterfaceActorDelegate actor)
	{
		super(actor);
	}
}
