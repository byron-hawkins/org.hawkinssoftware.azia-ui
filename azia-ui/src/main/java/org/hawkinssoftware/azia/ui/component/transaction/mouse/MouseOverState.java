package org.hawkinssoftware.azia.ui.component.transaction.mouse;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.ComponentDataHandler;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeMouseOverDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPassTermination;
import org.hawkinssoftware.azia.ui.input.MouseAware.MouseEventDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@VisibilityConstraint(extendedTypes = { VirtualComponent.class, UserInterfaceHandler.class })
@DomainRole.Join(membership = MouseEventDomain.class)
public class MouseOverState extends ComponentDataHandler implements UserInterfaceActorDelegate
{
	public static void install(ChangeComponentStateDirective.Component component)
	{
		MouseOverState handler = new MouseOverState(component);
		component.installHandler(handler);
	}

	public static final Key<MouseOverState> KEY = new Key<MouseOverState>();

	public final ChangeMouseOverDirective beginMouseOver;
	public final ChangeMouseOverDirective endMouseOver;

	private final ChangeComponentStateDirective.Component component;

	private boolean isMouseOver = false;

	@InvocationConstraint(domains = AssemblyDomain.class)
	protected MouseOverState(ChangeComponentStateDirective.Component component)
	{
		super(KEY);

		this.component = component;

		beginMouseOver = new ChangeMouseOverDirective(component, true);
		endMouseOver = new ChangeMouseOverDirective(component, false);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return component;
	}

	public boolean isMouseOver()
	{
		return isMouseOver;
	}

	public void changeMouseOver(ChangeMouseOverDirective change)
	{
		if (change == beginMouseOver)
		{
			isMouseOver = true;
		}
		else if (change == endMouseOver)
		{
			isMouseOver = false;
		}
	}

	public void mouseStateChange(EventPass pass, PendingTransaction transaction)
	{
		if (!pass.wasInContact(component))
		{
			transaction.contribute(beginMouseOver);
			component.requestRepaint();
		}
	}

	public void mouseStateTerminated(EventPassTermination termination, PendingTransaction transaction)
	{
		transaction.contribute(endMouseOver);
		component.requestRepaint();
	}
}
