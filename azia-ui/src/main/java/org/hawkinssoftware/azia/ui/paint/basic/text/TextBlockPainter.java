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
package org.hawkinssoftware.azia.ui.paint.basic.text;

import java.awt.Color;
import java.awt.Rectangle;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

// TODO: should there be a separate painter?
/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@InvocationConstraint(domains = RenderingDomain.class)
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class })
public class TextBlockPainter implements UserInterfaceHandler, UserInterfaceActorDelegate
{
	// TODO: this TextBlock should really be a top-level actor inside its host component, e.g. Label.textBlock, and its
	// painter should be a parallel under e.g. LabelPainter. There shouldn't be any data or data-specific handlers in
	// a painter.

	// TODO: should all references within a source be exempt from constraints?
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ValidateRead
	@ValidateWrite
	@DomainRole.Join(membership = RenderingDomain.class)
	private class TextBlock implements UserInterfaceActorDelegate
	{
		private String text;
		private TextAreaLinePainter[] textLines;

		// initialize to some arbitrary size
		private final Rectangle textBounds = new Rectangle(100, 100);
		private final Rectangle clipBounds = new Rectangle();

		TextBlock(String text)
		{
			this.text = text;

			String[] splitLines = text.split("\\n");
			textLines = new TextAreaLinePainter[splitLines.length];
			Rectangle lineBounds = new Rectangle();
			textBounds.setSize(-1, -1);
			int y = 0;
			for (int i = 0; i < splitLines.length; i++)
			{
				InstancePainter.TextMetrics.INSTANCE.getSize(splitLines[i], BoundsType.TEXT).applyTo(lineBounds);
				lineBounds.y = y;
				y += lineBounds.height;
				textLines[i] = new TextAreaLinePainter(splitLines[i], lineBounds);

				textBounds.add(lineBounds);
			}
		}

		boolean intersects(Rectangle bounds)
		{
			return (clipBounds.isEmpty() || bounds.intersects(clipBounds));
		}

		@Override
		public UserInterfaceActor getActor()
		{
			return host.getActor();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class TextSizeChangeNotification extends UserInterfaceNotification
	{
		public final EnclosureBounds newTextBounds;

		TextSizeChangeNotification(EnclosureBounds newTextBounds)
		{
			this.newTextBounds = newTextBounds;
		}
	}

	private final ComponentEnclosure<?, ?> host;

	private TextBlock textBlock = new TextBlock("");

	@InvocationConstraint(domains = AssemblyDomain.class)
	public TextBlockPainter(ComponentEnclosure<?, ?> host)
	{
		this.host = host;
		host.installHandler(this);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return host.getActor();
	}

	public void paint(Canvas c, Color color)
	{
		textBlock.clipBounds.x = 0;
		textBlock.clipBounds.y = 0;
		textBlock.clipBounds.width = c.span().width;
		textBlock.clipBounds.height = c.span().height;

		c.pushColor(color);
		for (TextAreaLinePainter textLine : textBlock.textLines)
		{
			if (textBlock.intersects(textLine.lineBounds))
			{
				c.g.drawString(textLine.lineText, 0, textLine.lineBounds.y);
			}
		}
	}

	public String getText()
	{
		return textBlock.text;
	}

	public Rectangle getTextBounds()
	{
		return textBlock.textBounds;
	}

	public void textChanged(SetTextBlockDirective change)
	{
		textBlock = change.textBlock;
	}

	public void textChanged(ChangeTextDirective.Notification notification, PendingTransaction transaction)
	{
		SetTextBlockDirective setTextBlock = new SetTextBlockDirective(new TextBlock(notification.getText()));
		transaction.contribute(setTextBlock);

		TextSizeChangeNotification sizeChangeNotification = new TextSizeChangeNotification(new EnclosureBounds(setTextBlock.textBlock.textBounds));
		transaction.contribute(sizeChangeNotification);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class SetTextBlockDirective extends ChangeComponentStateDirective
	{
		private final TextBlock textBlock;

		SetTextBlockDirective(TextBlock textBlock)
		{
			super(host.getActor());

			this.textBlock = textBlock;
		}
	}
}
