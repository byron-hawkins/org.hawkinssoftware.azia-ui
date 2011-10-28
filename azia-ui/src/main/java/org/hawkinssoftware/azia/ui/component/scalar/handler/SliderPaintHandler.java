package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.scalar.AbstractSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderComposite.SliderCompositeDomain;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = { SliderCompositeDomain.class, RenderingDomain.class })
public class SliderPaintHandler implements AbstractComposite.PaintHandler
{
	private final SliderComposite<? extends AbstractSlider> host;

	public SliderPaintHandler(SliderComposite<? extends AbstractSlider> host)
	{
		this.host = host;
	}

	@Override
	public void paint(PaintIncludeNotification notification, PendingTransaction transaction)
	{
		transaction.contribute(new PaintIncludeNotification(host.getTrack()));
		transaction.contribute(new PaintIncludeNotification(host.getKnob()));
	}
}
