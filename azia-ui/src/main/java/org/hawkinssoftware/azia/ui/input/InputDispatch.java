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

public class InputDispatch
{
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
