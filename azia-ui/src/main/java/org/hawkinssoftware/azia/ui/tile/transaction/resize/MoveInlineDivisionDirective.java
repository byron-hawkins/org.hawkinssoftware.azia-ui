package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeComponentStateDirective;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.PairTile;
import org.hawkinssoftware.rns.core.role.DomainRole;

@DomainRole.Join(membership = TileLayoutDomain.class)
public class MoveInlineDivisionDirective extends ChangeComponentStateDirective
{
	public class Notification extends UserInterfaceNotification
	{
		public int getInlineDivision()
		{
			return inlineDivision;
		}
	}

	public final int inlineDivision;

	public MoveInlineDivisionDirective(PairTile<?> actor, int inlineDivision)
	{
		super(actor);

		this.inlineDivision = inlineDivision;
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
