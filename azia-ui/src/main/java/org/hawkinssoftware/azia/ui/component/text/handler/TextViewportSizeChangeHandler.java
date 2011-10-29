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
package org.hawkinssoftware.azia.ui.component.text.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeViewportContentBoundsDirective;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextBlockPainter;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class TextViewportSizeChangeHandler implements UserInterfaceHandler
{
	private final ScrollPaneViewport viewport;

	public TextViewportSizeChangeHandler(ScrollPaneViewport viewport)
	{
		this.viewport = viewport;
	}

	public void textSizeChanged(TextBlockPainter.TextSizeChangeNotification notification, PendingTransaction transaction)
	{
		transaction.contribute(new ChangeViewportContentBoundsDirective(viewport, notification.newTextBounds));
		viewport.requestRepaint();
	}
}
