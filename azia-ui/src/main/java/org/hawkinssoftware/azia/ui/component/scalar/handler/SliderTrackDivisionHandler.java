package org.hawkinssoftware.azia.ui.component.scalar.handler;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack.SliderTrackDomain;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTrackDivisionDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = SliderTrackDomain.class)
public class SliderTrackDivisionHandler implements UserInterfaceHandler, UserInterfaceActorDelegate
{
	public class Half implements UserInterfaceHandler
	{
		private final boolean polarity;

		public final MouseOverHalf mouseOver = new MouseOverHalf();
		public final MousePressedHalf mousePressed = new MousePressedHalf();

		public Half(boolean polarity)
		{
			this.polarity = polarity;
		}

		@DomainRole.Join(membership = SliderTrack.SliderTrackDomain.class)
		public class MouseOverHalf extends MouseOverState
		{
			MouseOverHalf()
			{
				super(host);
				host.installHandler(this);
			}

			@Override
			public void mouseStateChange(EventPass pass, PendingTransaction transaction)
			{
				if ((host.getAxis().extractPosition(pass.event()) <= host.getDivisionPoint()) == polarity)
				{
					super.mouseStateChange(pass, transaction);
				}
			}
		}

		@DomainRole.Join(membership = SliderTrack.SliderTrackDomain.class)
		public class MousePressedHalf extends MousePressedState
		{
			MousePressedHalf()
			{
				super(host);
				host.installHandler(this);
			}

			@Override
			public void mouseStateChange(EventPass pass, PendingTransaction transaction)
			{
				if ((host.getAxis().extractPosition(pass.event()) <= host.getDivisionPoint()) == polarity)
				{
					super.mouseStateChange(pass, transaction);
				}
			}
		}
	}

	private final SliderTrack host;

	public final Half lowerHalf;
	public final Half upperHalf;

	private int divisionPoint;

	public SliderTrackDivisionHandler(SliderTrack host)
	{
		this.host = host;
		// TODO: this configuration disallows `changeHandler()
		host.installHandler(this);

		lowerHalf = new Half(true);
		upperHalf = new Half(false);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return host;
	}

	public int getDivisionPoint()
	{
		return divisionPoint;
	}

	public void changeTrackDivision(ChangeTrackDivisionDirective change)
	{
		divisionPoint = change.trackDivision;
	}
}
