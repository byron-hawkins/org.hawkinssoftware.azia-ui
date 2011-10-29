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

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = SliderTrackDomain.class)
public class SliderTrackDivisionHandler implements UserInterfaceHandler, UserInterfaceActorDelegate
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Half implements UserInterfaceHandler
	{
		private final boolean polarity;

		public final MouseOverHalf mouseOver = new MouseOverHalf();
		public final MousePressedHalf mousePressed = new MousePressedHalf();

		public Half(boolean polarity)
		{
			this.polarity = polarity;
		}

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
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

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
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
