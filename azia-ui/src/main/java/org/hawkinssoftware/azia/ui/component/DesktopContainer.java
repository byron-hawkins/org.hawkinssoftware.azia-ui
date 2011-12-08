package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.aop.InitializationAspect;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InitializationAspect(agent = DesktopContainer.FocusRegistrationAgent.class)
@DomainRole.Join(membership = TileLayoutDomain.class)
public interface DesktopContainer<KeyType extends LayoutEntity.Key<KeyType>> extends UserInterfaceHandler.Host
{
	LayoutEntity<KeyType> getLayoutEntity(KeyType key);
	
	public interface SingleFaced<KeyType extends LayoutEntity.Key<KeyType>> extends DesktopContainer<KeyType>, RepaintInstanceDirective.Host
	{
	}
	
	@DomainRole.Join(membership = InitializationDomain.class)
	public static class FocusRegistrationAgent implements InitializationAspect.Agent<DesktopContainer<?>>
	{
		public static final FocusRegistrationAgent INSTANCE = new FocusRegistrationAgent();
		
		@Override
		public void initialize(DesktopContainer<?> instance)
		{
			ComponentRegistry.getInstance().getFocusHandler().registerWindow(instance);
		}
	}
}
