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
package org.hawkinssoftware.azia.ui.component.transaction.mouse;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPassTermination;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
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
@VisibilityConstraint(extendedTypes = { VirtualComponent.class, UserInterfaceHandler.class })
@DomainRole.Join(membership = MouseEventDomain.class)
public class MousePressedState extends ComponentDataHandler implements MouseAware.MouseHandler, UserInterfaceActorDelegate,
		ChangeComponentStateDirective.Handler
{
	public static void install(ChangeComponentStateDirective.Component component)
	{
		MousePressedState handler = new MousePressedState(component);
		component.installHandler(handler);
	}

	public static final Key<MousePressedState> KEY = new Key<MousePressedState>();

	public final ChangePressedStateDirective beginPressedState;
	public final ChangePressedStateDirective endPressedState;

	private final ChangeComponentStateDirective.Component component;

	private boolean isPressed = false;

	@InvocationConstraint(domains = AssemblyDomain.class)
	protected MousePressedState(ChangeComponentStateDirective.Component component)
	{
		super(KEY);

		this.component = component;

		beginPressedState = new ChangePressedStateDirective(component, true);
		endPressedState = new ChangePressedStateDirective(component, false);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return component;
	}

	public boolean isPressed()
	{
		return isPressed;
	}

	@Override
	public void applyStateChange(ChangeComponentStateDirective change)
	{
		if (change == beginPressedState)
		{
			isPressed = true;
		}
		else if (change == endPressedState)
		{
			isPressed = false;
		}
	}

	@Override
	public void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (pass.event().changes().contains(MouseInputEvent.Change.LEFT_BUTTON))
		{
			if (pass.event().buttonsDown().contains(MouseInputEvent.Button.LEFT))
			{
				transaction.contribute(beginPressedState);
				component.requestRepaint();
			}
			else
			{
				transaction.contribute(endPressedState);
				component.requestRepaint();
			}
		}
	}

	@Override
	public void mouseStateTerminated(EventPassTermination pass, PendingTransaction transaction)
	{
		if (isPressed)
		{
			transaction.contribute(endPressedState);
		}
		component.requestRepaint();
	}
}
