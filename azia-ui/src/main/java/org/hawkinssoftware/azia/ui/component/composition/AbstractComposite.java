package org.hawkinssoftware.azia.ui.component.composition;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

// TODO: moving a class to a different package causes RNS failures in the evaluation by FQN, b/c it is stale some places
@DomainRole.Join(membership = RenderingDomain.class)
public abstract class AbstractComposite<ComponentType extends AbstractComponent, PainterType extends InstancePainter<? extends ComponentType>> extends
		ComponentEnclosure<ComponentType, PainterType>
{
	// TODO: domain membership did not get applied to implementor ScrollPaneResizeHandler (had to duplicate it)
	@DomainRole.Join(membership = TransactionParticipant.class)
	public interface ResizeHandler extends UserInterfaceHandler
	{
		void resize(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction);

		void apply(UserInterfaceDirective action);
	}

	@DomainRole.Join(membership = TransactionParticipant.class)
	public interface PaintHandler extends UserInterfaceHandler
	{
		void paint(PaintIncludeNotification notification, PendingTransaction transaction);
	}

	private BoundedEntity sizeDelegate;

	protected AbstractComposite(ComponentType component)
	{
		super(component);
	}

	protected AbstractComposite(ComponentType component, PainterType painter)
	{
		super(component, painter);
	}

	@SuppressWarnings("unchecked")
	public <ServiceType> ServiceType getService(Class<ServiceType> serviceType)
	{
		if (serviceType.isAssignableFrom(AbstractComponent.class))
		{
			return (ServiceType) component;
		}
		return null;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void installSizeDelegate(BoundedEntity sizeDelegate)
	{
		this.sizeDelegate = sizeDelegate;
	}

	@Override
	public final Expansion getExpansion(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getExpansion(axis);
		}
		else
		{
			return sizeDelegate.getExpansion(axis);
		}
	}

	@Override
	public final int getPackedSize(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getPackedSize(axis);
		}
		else
		{
			return sizeDelegate.getPackedSize(axis);
		}
	}

	@Override
	public final BoundedEntity.MaximumSize getMaxSize(Axis axis)
	{
		if (sizeDelegate == null)
		{
			return super.getMaxSize(axis);
		}
		else
		{
			return sizeDelegate.getMaxSize(axis);
		}
	}
}
