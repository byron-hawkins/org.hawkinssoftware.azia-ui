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
package org.hawkinssoftware.azia.ui.input;

import java.util.HashSet;
import java.util.Set;

import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.input.NativeInputSpool;
import org.hawkinssoftware.azia.ui.component.DesktopWindow;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class InputDispatch
{
	
	/**
	 * The listener interface for receiving input events. The class that is interested in processing a input event
	 * implements this interface, and the object created with that class is registered with a component using the
	 * component's <code>addInputListener<code> method. When
	 * the input event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see InputEvent
	 */
	@DomainRole.Join(membership = MouseEventDomain.class)
	private class InputListener implements NativeInputSpool.Listener
	{
		@Override
		public void keyboardStateChanged(KeyboardInputEvent event)
		{
			KeyEventDispatch.getInstance().keyEvent(event);
		}

		@Override
		public void mouseStateChanged(MouseInputEvent event)
		{
			synchronized (windows)
			{
				for (DesktopWindow<?> window : windows)
				{
					window.mouseEvent(event);
				}
			}
		}
	}

	public static InputDispatch getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new InputDispatch();
		}
		return INSTANCE;
	}

	@InvocationConstraint(domains = InitializationDomain.class)
	public static void start()
	{
		getInstance().startSpool();
	}

	private static InputDispatch INSTANCE;

	private final NativeInputSpool spool;

	private final Set<DesktopWindow<?>> windows = new HashSet<DesktopWindow<?>>();

	private final InputListener inputListener = new InputListener();

	public InputDispatch()
	{
		spool = new NativeInputSpool();
	}

	private void startSpool()
	{
		spool.addListener(inputListener);
		spool.start();
	}

	@InvocationConstraint(types = DesktopWindow.class)
	public void register(DesktopWindow<?> window)
	{
		synchronized (windows)
		{
			windows.add(window);
		}
	}
}
