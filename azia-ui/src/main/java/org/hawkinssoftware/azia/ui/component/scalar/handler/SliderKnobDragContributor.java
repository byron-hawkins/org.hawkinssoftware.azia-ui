package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite.SliderCompositeDomain;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SlideToPositionNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.MouseDragDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { SliderCompositeDomain.class, MouseEventDomain.class })
public class SliderKnobDragContributor implements UserInterfaceHandler
{
	private final SliderComposite<? extends AbstractSlider> host;
	private final RepaintInstanceDirective repaint;
	private int knobStartDelta;

	public SliderKnobDragContributor(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;
		repaint = new RepaintInstanceDirective(host.getComponent());
	} 

	public void mouseDragged(MouseDragDirective.Notification drag, PendingTransaction transaction)
	{
		int trackStart = host.getBounds().getPosition(host.getAxis());
		int dragPosition = (host.getAxis().extractPosition(drag.getPosition()) - trackStart);
		switch (drag.getState())
		{
			case START:
				knobStartDelta = dragPosition - (host.getKnob().getBounds().getPosition(host.getAxis()) - trackStart);
				break;
			case DRAG:
				int dragToPosition = Math.min(Math.max(0, dragPosition - knobStartDelta), host.getMaxKnobPosition());
				transaction.contribute(new SlideToPositionNotification(host, dragToPosition));
				RepaintRequestManager.requestRepaint(repaint);
				break;
		}
	}
}
