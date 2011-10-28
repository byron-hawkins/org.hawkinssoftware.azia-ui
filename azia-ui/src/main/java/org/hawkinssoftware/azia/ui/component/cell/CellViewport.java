package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellContentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public class CellViewport extends ScrollPaneViewport
{
	public static abstract class Assembly<CellPainterType extends AbstractCellContentPainter, ViewportCompositeType extends CellViewportComposite<CellPainterType>>
			extends ComponentAssembly<CellViewport, CellViewport.Painter, ViewportCompositeType>
	{
		public <ViewportType extends CellViewport> Assembly(Class<ViewportType> viewportType, Class<ViewportCompositeType> compositeType)
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent(new AbstractComponent.Key<ViewportType>(viewportType));
			setEnclosure(new ComponentEnclosure.Key(compositeType));
		}

		protected abstract CellPainterType createCellPainter();

		@Override
		public void assemble(ViewportCompositeType enclosure)
		{
			super.assemble(enclosure);

			CellPainterType cellContentPainter = createCellPainter();
			enclosure.setCellPainter(cellContentPainter);
		}
	}

	public interface Painter extends ScrollPaneViewport.Painter
	{
		// marker
	}
	
	@ValidateRead.Exempt
	@ValidateWrite.Exempt
	private CellViewportComposite<?> viewport;

	@InvocationConstraint
	public CellViewport()
	{
		installHandler(new MouseHandler());
	}

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();
		
		viewport = CompositionRegistry.getComposite(CellViewportComposite.class);
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FILL;
	}

	// the coordinates need to be adjusted for the ScrollPaneViewport(CellViewport) (x,y). The viewport doesn't
	// intervene in this call, it is as if this handler were directly on the viewport. Trouble is, I can't see the
	// viewport's scroll position from here... In general, a painter is allowed to see its component.
	@DomainRole.Join(membership = { TransactionParticipant.class, FlyweightCellDomain.class })
	public class MouseHandler implements UserInterfaceHandler
	{
		public void mouseStateChange(final EventPass pass, final PendingTransaction transaction)
		{
			new InstantiationTask.SubordinateTask(CellViewport.this) {
				@Override
				protected void execute()
				{
					MouseAware cellHandle = viewport.getCellPainter().getMouseAwareCellHandle(pass);
					if (cellHandle != null)
					{
						transaction.contribute(new Forward(cellHandle));
					}
				}
			}.start();
		}
	}
}
