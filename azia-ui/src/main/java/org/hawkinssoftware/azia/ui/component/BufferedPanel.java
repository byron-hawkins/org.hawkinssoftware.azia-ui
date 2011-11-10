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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.VolatileImage;
import java.util.Collection;

import org.hawkinssoftware.azia.core.action.ReadOnlyTransaction;
import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseEventTransaction;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintTransaction;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.ApplyLayoutTransaction;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
public class BufferedPanel<KeyType extends LayoutEntity.Key<KeyType>> extends Canvas
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	private class PaintAllTask extends UserInterfaceTask
	{
		@Override
		protected boolean execute()
		{
			getTransaction(ReadOnlyTransaction.class);

			try
			{
				PaintTransaction paintAll = getTransaction(PaintTransaction.class);

				paintAll.configure(getBufferGraphics());
				paintAll.addRegion(topTile, new EnclosureBounds(0, 0, getSize().width, getSize().height));
				paintAll.start(topTile);

				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				t.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class TopTileResizer extends ComponentAdapter
	{

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		private class ResizeTask extends UserInterfaceTask
		{
			@Override
			protected boolean execute()
			{
				EnclosureBounds bounds = new EnclosureBounds(0, 0, getSize().width, getSize().height);
				ApplyLayoutTransaction transaction = getTransaction(ApplyLayoutTransaction.class);

				try
				{
					transaction.addRegion(topTile, bounds);
					transaction.beginAssembly();
					return true;
				}
				catch (Throwable t)
				{
					if (t instanceof RetryException)
					{
						throw (RetryException) t;
					}
					t.printStackTrace();
					return false;
				}
			}
		}

		private final ResizeTask resizeTask = new ResizeTask();

		private long lastResize = 0L;
		private boolean skippedResize = false;

		@Override
		public void componentResized(ComponentEvent event)
		{
			if ((System.currentTimeMillis() - lastResize) < 20L)
			{
				synchronized (bufferLock)
				{
					dirty = true;
				}

				skippedResize = true;
				return;
			}

			execute();
		}

		void execute()
		{
			skippedResize = false;
			lastResize = System.currentTimeMillis();

			synchronized (bufferLock)
			{
				dirty = true;
			}

			try
			{
				TransactionRegistry.executeTask(resizeTask);
			}
			catch (ConcurrentAccessException e)
			{
				e.printStackTrace();
			}
		}

		void leftMouseButtonReleased()
		{
			if (skippedResize)
			{
				execute();
				repaint();
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	private class PackTask extends UserInterfaceTask
	{
		private boolean tight;
		private Dimension minSize;
		private Dimension maxSize;

		Dimension packSize;

		void configure(boolean tight, Dimension minSize, Dimension maxSize)
		{
			this.tight = tight;
			this.minSize = minSize;
			this.maxSize = maxSize;
		}

		@Override
		protected boolean execute()
		{
			getTransaction(ReadOnlyTransaction.class);
			try
			{
				if (tight)
				{
					packSize = new Dimension(topTile.getPackedSize(Axis.H), topTile.getPackedSize(Axis.V));
				}
				else
				{
					packSize = new Dimension(topTile.getMaxSize(Axis.H).getValue(), topTile.getMaxSize(Axis.V).getValue());
				}

				packSize.width = Math.max(minSize.width, Math.min(packSize.width, maxSize.width));
				packSize.height = Math.max(minSize.height, Math.min(packSize.height, maxSize.height));

				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				t.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = MouseEventDomain.class)
	private class MouseEventTask extends UserInterfaceTask
	{
		private final MouseAware.State mouseState = new MouseAware.State();

		private MouseInputEvent event;

		void setMouseEvent(MouseInputEvent event)
		{
			this.event = event;
		}

		@Override
		protected boolean execute()
		{
			try
			{
				MouseEventTransaction transaction = getTransaction(MouseEventTransaction.class);
				mouseState.newMouseFrame(event);
				transaction.assemble(topTile, mouseState);
				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				t.printStackTrace();
				return false;
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	private class RepaintTask extends UserInterfaceTask
	{
		PaintTransaction repaint;
		Collection<RepaintDirective> repaints;

		public RepaintTask()
		{
			super(Type.POST_PROCESSING);
		}

		public void setRepaints(Collection<RepaintDirective> repaints)
		{
			this.repaints = repaints;
		}

		boolean painted()
		{
			return !repaint.isEmpty();
		}

		@Override
		protected boolean execute()
		{
			try
			{
				synchronized (bufferLock)
				{
					repaint = getTransaction(PaintTransaction.class);
					repaint.configure(getBufferGraphics());
					repaint.start(repaints);
				}

				return true;
			}
			catch (Throwable t)
			{
				if (t instanceof RetryException)
				{
					throw (RetryException) t;
				}
				t.printStackTrace();
				return false;
			}
		}
	}

	private final RepaintInstanceDirective.Host repaintHost;
	private final TopTile<KeyType> topTile;

	// TODO: dynamic screen size for the buffered panel
	private final Dimension MAX_BUFFER_SIZE = new Dimension(1920, 1200);

	private final Object bufferLock = new Object();
	private VolatileImage buffer = null;

	private boolean dirty = true;

	private final PaintAllTask paintAllTask = new PaintAllTask();
	private final TopTileResizer topTileResizer = new TopTileResizer();
	private final PackTask packTask = new PackTask();
	private final MouseEventTask mouseEventTask = new MouseEventTask();
	private final RepaintTask repaintTask = new RepaintTask();

	// for optional frame rate reporting in reportFrameRate()
	private int fpsMax = 0;
	private int framesThisSecond = 0;
	private long currentSecond = 0L;

	protected BufferedPanel(RepaintInstanceDirective.Host repaintHost, TopTile<KeyType> topTile)
	{
		this.repaintHost = repaintHost;
		this.topTile = topTile;
		addComponentListener(topTileResizer);
		setBackground(new Color(0xDDDDDD));
	}

	TopTile<KeyType> getTopTile()
	{
		return topTile;
	}

	private boolean prepareBuffer()
	{
		synchronized (bufferLock)
		{
			if ((buffer != null) && buffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
			{
				buffer = null;
			}

			if (buffer == null)
			{
				buffer = createVolatileImage(MAX_BUFFER_SIZE.width, MAX_BUFFER_SIZE.height);
				// System.out.println("created buffer " + buffer.getWidth(null) + ", " + buffer.getHeight(null));

				return true;
			}
			return false;
		}
	}

	private boolean validateBuffer()
	{
		synchronized (bufferLock)
		{
			prepareBuffer();
			return (buffer.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_OK);
		}
	}

	private Graphics2D getBufferGraphics()
	{
		prepareBuffer();
		Graphics2D g = (Graphics2D) buffer.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g;
	}

	@Override
	public void update(Graphics g)
	{
		paint(g);
	}

	@Override
	@InvocationConstraint
	public void paint(Graphics g)
	{
		blit((Graphics2D) g);

		// reportFrameRate();
	}

	private void reportFrameRate()
	{
		long now = System.currentTimeMillis();
		if ((framesThisSecond > 0) && ((now - currentSecond) > 1000L))
		{
			if (framesThisSecond > fpsMax)
			{
				fpsMax = framesThisSecond;
			}
			System.out.println("Max FPS: " + fpsMax);

			currentSecond = now - (now % 1000L);
			framesThisSecond = 0;
		}
		else
		{
			framesThisSecond++;
		}
	}

	void paintAll()
	{
		try
		{
			TransactionRegistry.executeTask(paintAllTask);
		}
		catch (ConcurrentAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void blit(Graphics2D g)
	{
		synchronized (bufferLock)
		{
			while (dirty || !validateBuffer())
			{
				paintAll();
				dirty = false;
			}

			g.drawImage(buffer, 0, 0, null);
		}
	}

	void pack(boolean tight, Dimension minSize, Dimension maxSize)
	{
		try
		{
			packTask.configure(tight, minSize, maxSize);
			TransactionRegistry.executeTask(packTask);
		}
		catch (ConcurrentAccessException e)
		{
			e.printStackTrace();
			return;
		}

		setPreferredSize(packTask.packSize);
	}

	void mouseEvent(MouseInputEvent event)
	{
		if (event.changes().contains(MouseInputEvent.Change.LEFT_BUTTON) && !event.buttonsDown().contains(MouseInputEvent.Button.LEFT))
		{
			topTileResizer.leftMouseButtonReleased();
		}

		Point mainPanelLocationOnScreen = getLocationOnScreen();
		Dimension panelSize = getSize();
		int xWindow = event.x() - mainPanelLocationOnScreen.x;
		int yWindow = event.y() - mainPanelLocationOnScreen.y;

		if ((xWindow <= 0) || (yWindow <= 0) || (xWindow >= panelSize.width) || (yWindow >= panelSize.height))
		{
			// TODO: should all mouse events really be sent when the mouse is outside the window? I only need mouse up
			// for drag release.
			// return;
		}

		try
		{
			event.setPosition(xWindow, yWindow);
			mouseEventTask.setMouseEvent(event);
			TransactionRegistry.executeTask(mouseEventTask);
		}
		catch (ConcurrentAccessException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Modifies the backbuffer with the specified <code>repaints</code>
	 * 
	 * @param repaints
	 */
	void invokeTransactionRepaints(Collection<RepaintDirective> repaints)
	{
		try
		{
			repaintTask.setRepaints(repaints);
			TransactionRegistry.executeTask(repaintTask);
		}
		catch (ConcurrentAccessException e)
		{
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Swap the backbuffer into the screen.
	 */
	void applyTransactionRepaints()
	{
		if (repaintTask.painted())
		{
			repaint();
		}
	}
}
