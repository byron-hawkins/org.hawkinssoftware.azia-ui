package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite.SliderCompositeDomain;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeKnobPositionNotification;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.ChangeKnobSpanDirective;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { SliderCompositeDomain.class, DisplayBoundsDomain.class })
public class SliderCollaborationActionContributor implements UserInterfaceHandler
{
	private final SliderComposite<? extends AbstractSlider> host;

	public SliderCollaborationActionContributor(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;
	}

	public void changeKnobPosition(ChangeKnobPositionNotification moveKnob, PendingTransaction transaction)
	{
		int absolutePosition = moveKnob.knobPosition + host.getBounds().getPosition(host.getAxis());

		ComponentBoundsChangeDirective moveKnobBounds = ComponentBoundsChangeDirective.changePosition(host.getKnob().getComponent(), host.getAxis(),
				absolutePosition);
		transaction.contribute(moveKnobBounds);
	}

	public void changeKnobSpan(ChangeKnobSpanDirective.Notification resizeKnob, PendingTransaction transaction)
	{
		ComponentBoundsChangeDirective resizeKnobBounds = ComponentBoundsChangeDirective.changeSpan(host.getKnob().getComponent(), host.getAxis(),
				resizeKnob.getKnobWidth());
		transaction.contribute(resizeKnobBounds);
	}
}
