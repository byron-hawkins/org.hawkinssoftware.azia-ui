package org.hawkinssoftware.azia.ui.component.text.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeViewportContentBoundsDirective;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextBlockPainter;

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
