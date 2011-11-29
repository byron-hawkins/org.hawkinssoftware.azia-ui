package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.rns.core.aop.InitializationAspect;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InitializationAspect(agent = DesktopContainer.FocusRegistrationAgent.class)
public interface DesktopContainer extends UserInterfaceHandler.Host, UserInterfaceActor
{
	public interface SingleFaced extends DesktopContainer, RepaintInstanceDirective.Host
	{
	}
	
	@DomainRole.Join(membership = InitializationDomain.class)
	public static class FocusRegistrationAgent implements InitializationAspect.Agent<DesktopContainer>
	{
		public static final FocusRegistrationAgent INSTANCE = new FocusRegistrationAgent();
		
		@Override
		public void initialize(DesktopContainer instance)
		{
			ComponentRegistry.getInstance().getFocusHandler().registerWindow(instance);
		}
	}
}
