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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Collection;

import javax.swing.SwingUtilities;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.component.transaction.window.SetVisibleAction;
import org.hawkinssoftware.azia.ui.component.transaction.window.WindowEventTransaction;
import org.hawkinssoftware.azia.ui.component.transaction.window.WindowFocusAction;
import org.hawkinssoftware.azia.ui.input.InputDispatch;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class DesktopWindow<KeyType extends LayoutEntity.Key<KeyType>> extends AbstractEventDispatch implements RepaintInstanceDirective.Host
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public enum FrameType
	{
		UNDECORATED,
		CLOSE_BUTTON,
		FULL_FRAME;
	}

	// WIP: can't enforce @ValidateWrite on this frame's content without wrapping :-/
	public final Window frame;
	private final WindowState state = new WindowState();

	private final BufferedPanel<KeyType> mainPanel;

	private Dimension maximumSize;

	public DesktopWindow(FrameType frameType, TopTile<KeyType> topTile, String title)
	{
		switch (frameType)
		{
			case UNDECORATED:
				frame = new Window(new Frame());
				break;
			case CLOSE_BUTTON:
				frame = new Dialog(new Frame(), title);
				break;
			case FULL_FRAME:
				frame = new Frame(title);
				break;
			default:
				throw new UnknownEnumConstantException(frameType);
		}
		frame.addWindowListener(state);
		frame.addWindowFocusListener(state);
		frame.addWindowStateListener(state);
		frame.addComponentListener(state);

		mainPanel = new BufferedPanel<KeyType>(this, topTile);
		frame.add(mainPanel);

		InputDispatch.getInstance().register(this);

		installHandler(new VisibilityHandler());
	}

	public TopTile<KeyType> getTopTile()
	{
		return mainPanel.getTopTile();
	}

	public void setSize(int width, int height)
	{
		frame.setSize(width, height);
	}

	public void pack(Dimension minimumSize, Dimension maximumSize)
	{
		mainPanel.pack(false, minimumSize, maximumSize);
		frame.pack();
		this.maximumSize = frame.getSize();
		frame.setMaximumSize(maximumSize);
	}

	public void setVisible(boolean b)
	{
		frame.setVisible(b);

		if (b)
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					// TODO: why does the main panel need explicit repainting at this point?
					mainPanel.paintAll();
					mainPanel.repaint();
				}
			});
		}
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void center(int monitor)
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

		Point center;
		if (monitor < env.getScreenDevices().length)
		{
			Rectangle bounds = env.getScreenDevices()[monitor].getDefaultConfiguration().getBounds();
			center = new Point(bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
		}
		else
		{
			Log.out(Tag.WARNING, "Window requested to center() on monitor %d, but only %d monitors are available. Centering on monitor 0.", monitor,
					env.getScreenDevices().length);
			center = env.getCenterPoint();
		}

		frame.setLocation(center.x - (frame.getSize().width / 2), center.y - (frame.getSize().height / 2));
	}

	@InvocationConstraint(domains = MouseEventDomain.class)
	public void mouseEvent(MouseInputEvent event)
	{
		if (!frame.isVisible())
		{
			return;
		}

		mainPanel.mouseEvent(event);
	}

	@Override
	public void invokeTransactionRepaints(Collection<RepaintDirective> repaints)
	{
		if (frame.isVisible())
		{
			mainPanel.invokeTransactionRepaints(repaints);
		}
	}

	@Override
	public void applyTransactionRepaints()
	{
		if (frame.isVisible())
		{
			mainPanel.applyTransactionRepaints();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class VisibilityHandler implements UserInterfaceHandler
	{
		public void setVisible(SetVisibleAction action)
		{
			DesktopWindow.this.setVisible(action.visible);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class WindowEventDispatch extends UserInterfaceTask
	{
		private UserInterfaceDirective windowAction;

		@Override
		protected boolean execute()
		{
			try
			{
				WindowEventTransaction transaction = getTransaction(WindowEventTransaction.class);
				transaction.assemble(windowAction);
				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				Log.out(Tag.CRITICAL, t, "Failed to initiate a window event transaction.");
				return false;
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class WindowState implements WindowListener, WindowStateListener, WindowFocusListener, ComponentListener
	{
		private final WindowEventDispatch eventDispatch = new WindowEventDispatch();

		private void dispatchEvent(UserInterfaceDirective action)
		{
			try
			{
				eventDispatch.windowAction = action;
				TransactionRegistry.executeTask(eventDispatch);
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.CRITICAL, e, "Failed to dispatch a window event: %s.", action.getClass().getSimpleName());
			}
		}

		public void windowGainedFocus(WindowEvent event)
		{
			dispatchEvent(new WindowFocusAction(DesktopWindow.this, true));
		}

		public void windowLostFocus(WindowEvent event)
		{
			dispatchEvent(new WindowFocusAction(DesktopWindow.this, false));
		}

		public void windowStateChanged(WindowEvent event)
		{
		}

		public void windowOpened(WindowEvent event)
		{
		}

		public void windowClosing(WindowEvent event)
		{
			System.exit(0);
		}

		public void windowClosed(WindowEvent event)
		{
		}

		public void windowIconified(WindowEvent event)
		{
		}

		public void windowDeiconified(WindowEvent event)
		{
		}

		public void windowActivated(WindowEvent event)
		{
		}

		public void windowDeactivated(WindowEvent event)
		{
		}

		@Override
		public void componentResized(ComponentEvent event)
		{
		}

		@Override
		public void componentMoved(ComponentEvent event)
		{
		}

		@Override
		public void componentShown(ComponentEvent event)
		{
		}

		@Override
		public void componentHidden(ComponentEvent event)
		{
		}
	}
}
