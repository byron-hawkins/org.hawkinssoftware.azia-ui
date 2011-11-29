package org.hawkinssoftware.azia.ui.component.cell.transaction;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class SetSelectedRowDirective extends UserInterfaceDirective
{
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public final class Notification extends UserInterfaceNotification
	{
		public final int row;

		Notification(int row)
		{
			this.row = row;
		}
	}
	
	public final int row;

	public SetSelectedRowDirective(UserInterfaceActorDelegate actor, int row)
	{
		super(actor);
		this.row = row;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification(row);
	}
}