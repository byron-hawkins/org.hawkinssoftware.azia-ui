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
package org.hawkinssoftware.azia.ui.paint.canvas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.moa.ExecutionContext;
import org.hawkinssoftware.rns.core.moa.ExecutionPath;
import org.hawkinssoftware.rns.core.moa.ExecutionStackFrame;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ExecutionPath.NoFrame
@DomainRole.Join(membership = { DisplayBoundsDomain.class, RenderingDomain.class })
public class Canvas
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private static final class Key implements ExecutionContext.Key<PaintExecutionContext>
	{
		private static final Key INSTANCE = new Key();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private static class PaintExecutionContext extends ExecutionContext
	{
		final Canvas canvas;

		PaintExecutionContext(Canvas canvas)
		{
			this.canvas = canvas;
		}

		@Override
		protected void pushFrame(ExecutionStackFrame frame)
		{
			canvas.currentFrame.invocationDepth++;
		}

		@Override
		protected void popFromFrame(ExecutionStackFrame frame)
		{
			if (canvas.currentFrame.invocationDepth == 0)
			{
				canvas.popFrame();
			}
			else
			{
				canvas.currentFrame.invocationDepth--;
			}
		}
	}

	public static Canvas installExecutionContext(Graphics2D g)
	{
		Canvas c = new Canvas(g);
		c.pushFrame();
		ExecutionPath.installExecutionContext(Key.INSTANCE, new PaintExecutionContext(c));
		return c;
	}

	public static void removeExecutionContext()
	{
		ExecutionPath.removeExecutionContext(Key.INSTANCE);
	}

	public static Canvas get()
	{
		Canvas c = ExecutionPath.getExecutionContext(Key.INSTANCE).canvas;

		if (c.currentFrame.invocationDepth > 0)
		{
			c.pushFrame();
		}

		return c;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private class CurrentBounds
	{
		BoundsFrame bounds;
		Size size;
		Size span; // the span is more convenient for drawing that the size

		void update()
		{
			if (boundsStack.isEmpty())
			{
				bounds = new BoundsFrame(0, 0);
				size = null;
				span = null;
			}
			else
			{
				bounds = boundsStack.get(boundsStack.size() - 1);
				size = bounds.size;
				span = new Size(size.width - 1, size.height - 1);
			}
		}

		void clip()
		{
			revertClip(bounds.effectiveClip);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private class BoundsFrame
	{
		final int x, y;
		final Size size;
		Rectangle effectiveClip;
		final boolean changesClip;

		BoundsFrame(EnclosureBounds bounds)
		{
			this(bounds.x, bounds.y, bounds.width, bounds.height);
		}

		BoundsFrame(int x, int y, int width, int height)
		{
			this.x = x;
			this.y = y;
			size = new Size(width, height);
			this.changesClip = true;
		}

		BoundsFrame(int x, int y)
		{
			this.x = x;
			this.y = y;
			this.changesClip = false;

			if (boundsStack.isEmpty())
			{
				size = null;
				effectiveClip = null;
			}
			else
			{
				BoundsFrame peek = boundsStack.get(boundsStack.size() - 1);
				size = peek.size;
				effectiveClip = new Rectangle(peek.effectiveClip);
				effectiveClip.x -= x;
				effectiveClip.y -= y;
			}
		}
	}

	public final Graphics2D g;

	private final List<Frame> frameStack = new ArrayList<Frame>();
	private final List<BoundsFrame> boundsStack = new ArrayList<BoundsFrame>();
	private final List<Color> colorStack = new ArrayList<Color>();
	private final List<Font> fontStack = new ArrayList<Font>();

	private Frame currentFrame;
	private CurrentBounds currentBounds = new CurrentBounds();
	private Color currentColor = null;
	private Font currentFont = null;

	// temporarily public
	public Canvas(Graphics2D g)
	{
		this.g = g;
	}

	public Size size()
	{
		return currentBounds.size;
	}

	public Size span()
	{
		return currentBounds.span;
	}

	public int centerLeadingEdge(Rectangle2D glyphBounds)
	{
		return (int) Math.round(((size().width - glyphBounds.getWidth()) / 2) - glyphBounds.getX());
	}

	public int centerTypicalTextBaseline()
	{
		float ascentSpan = TextMetrics.INSTANCE.getFontAscent();
		return Math.round(((size().height - ascentSpan) / 2) + ascentSpan);
	}

	public int centerExactBaseline(Rectangle2D glyphBounds)
	{
		double yAscent = ((size().height - glyphBounds.getHeight()) / 2);
		return (int) Math.round(yAscent + glyphBounds.getHeight() - (glyphBounds.getHeight() + glyphBounds.getY()));
	}
	
	private void revertClip(Rectangle clip)
	{
		if (clip == null)
		{
			Log.out(Tag.CANVAS_DEBUG, "Revert clip to null");

			g.setClip(null);
		}
		else
		{
			Log.out(Tag.CANVAS_DEBUG, "Revert clip to (%d, %d) %d x %d", clip.x, clip.y, clip.width, clip.height);

			g.setClip(clip);
		}
	}

	private void narrowClip(BoundsFrame bounds)
	{
		if (bounds.size == null)
		{
			Log.out(Tag.CANVAS_DEBUG, "Set clip to null");

			bounds.effectiveClip = null;
		}
		else
		{
			bounds.effectiveClip = new Rectangle(0, 0, bounds.size.width, bounds.size.height);

			Rectangle outerBounds = g.getClipBounds();
			if (outerBounds != null)
			{
				bounds.effectiveClip.x = Math.max(0, outerBounds.x);
				bounds.effectiveClip.y = Math.max(0, outerBounds.y);

				int xOuterExtent = outerBounds.x + outerBounds.width;
				int xExtent = bounds.effectiveClip.x + bounds.effectiveClip.width;
				if (xExtent > xOuterExtent)
				{
					bounds.effectiveClip.width -= (xExtent - xOuterExtent);
				}

				int yOuterExtent = outerBounds.y + outerBounds.height;
				int yExtent = bounds.effectiveClip.y + bounds.effectiveClip.height;
				if (yExtent > yOuterExtent)
				{
					bounds.effectiveClip.height -= (yExtent - yOuterExtent);
				}
			}

			Log.out(Tag.CANVAS_DEBUG, "Narrow clip to (%d, %d) %d x %d", bounds.effectiveClip.x, bounds.effectiveClip.y, bounds.effectiveClip.width,
					bounds.effectiveClip.height);
		}

		g.setClip(bounds.effectiveClip);
	}

	private void pushBounds(BoundsFrame bounds)
	{
		g.translate(bounds.x, bounds.y);
		Log.out(Tag.CANVAS_DEBUG, "Push origin: delta (%d, %d)", bounds.x, bounds.y);

		if (bounds.changesClip)
		{
			narrowClip(bounds);
		}

		boundsStack.add(bounds);
		currentBounds.update();
		currentFrame.boundsCount++;
	}

	public void pushBounds(EnclosureBounds bounds)
	{
		pushBounds(new BoundsFrame(bounds));
	}

	public void pushBounds(int x, int y, int width, int height)
	{
		pushBounds(new BoundsFrame(x, y, width, height));
	}

	public void pushBoundsPosition(int x, int y)
	{
		pushBounds(new BoundsFrame(x, y));
	}

	public void popBounds()
	{
		BoundsFrame pop = boundsStack.remove(boundsStack.size() - 1);
		reverseTranslation(pop);
		currentBounds.update();
		currentBounds.clip();
		currentFrame.boundsCount--;
	}

	private void reverseTranslation(BoundsFrame bounds)
	{
		g.translate(-bounds.x, -bounds.y);
		Log.out(Tag.CANVAS_DEBUG, "Pop origin: delta (%d, %d)", -bounds.x, -bounds.y);
	}

	public void pushColor(Color c)
	{
		g.setColor(c);
		colorStack.add(c);
		currentColor = c;
		currentFrame.colorCount++;
	}

	public void popColor(Color c)
	{
		colorStack.remove(colorStack.size() - 1);
		currentColor = colorStack.get(colorStack.size() - 1);
		g.setColor(currentColor);
		currentFrame.colorCount--;
	}

	public void pushFont(Font f)
	{
		g.setFont(f);
		fontStack.add(f);
		currentFont = f;
		currentFrame.fontCount++;
	}

	public void popFont(Font f)
	{
		fontStack.remove(fontStack.size() - 1);
		currentFont = fontStack.get(fontStack.size() - 1);
		g.setFont(currentFont);
		currentFrame.fontCount--;
	}

	private void pushFrame()
	{
		currentFrame = new Frame();
		frameStack.add(currentFrame);
	}

	private void popFrame()
	{
		Frame pop = frameStack.remove(frameStack.size() - 1);
		pop.popAll();

		currentFrame = frameStack.get(frameStack.size() - 1);
		currentFrame.invocationDepth--;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ExecutionPath.NoFrame
	private class Frame
	{
		int invocationDepth = 0;

		int boundsCount = 0;
		int colorCount = 0;
		int fontCount = 0;

		void popAll()
		{
			if (boundsCount > 0)
			{
				for (int i = 0; i < boundsCount; i++)
				{
					reverseTranslation(boundsStack.remove(boundsStack.size() - 1));
				}
				currentBounds.update();
				currentBounds.clip();
			}

			for (int i = 0; i < colorCount; i++)
			{
				colorStack.remove(colorStack.size() - 1);
			}
			if (colorStack.isEmpty())
			{
				currentColor = null;
			}
			else
			{
				currentColor = colorStack.get(colorStack.size() - 1);
				g.setColor(currentColor);
			}

			for (int i = 0; i < fontCount; i++)
			{
				fontStack.remove(fontStack.size() - 1);
			}
			if (fontStack.isEmpty())
			{
				currentFont = null;
			}
			else
			{
				currentFont = fontStack.get(fontStack.size() - 1);
				g.setFont(currentFont);
			}
		}

		void reset()
		{
			boundsCount = 0;
			colorCount = 0;
		}
	}
}
