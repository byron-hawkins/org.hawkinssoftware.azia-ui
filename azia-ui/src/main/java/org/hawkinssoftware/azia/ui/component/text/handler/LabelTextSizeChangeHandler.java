package org.hawkinssoftware.azia.ui.component.text.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextBlockPainter;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class LabelTextSizeChangeHandler implements UserInterfaceHandler
{
	private final AbstractComposite<?, ?> label;

	private final RepaintInstanceDirective repaint;

	public LabelTextSizeChangeHandler(AbstractComposite<?, ?> label)
	{
		this.label = label;
		repaint = new RepaintInstanceDirective(label.getComponent());
	}

	public void textSizeChanged(TextBlockPainter.TextSizeChangeNotification notification, PendingTransaction transaction)
	{
		transaction.contribute(new ComponentBoundsChangeDirective(label.getComponent(), notification.newTextBounds));
		RepaintRequestManager.requestRepaint(repaint);
	}
}
