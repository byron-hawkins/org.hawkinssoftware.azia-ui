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
package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.UpdateLayoutHandler;
import org.hawkinssoftware.rns.core.log.Log;

/**
 * DOC comment task awaits.
 * 
 * @param <LabelType>
 *            the generic type
 * @param <PainterType>
 *            the generic type
 * @author Byron Hawkins
 */
public class LabelComposite<LabelType extends Label, PainterType extends ComponentPainter<LabelType>> extends AbstractComposite<LabelType, PainterType>
{
	public static class UpdateHandler<KeyType extends LayoutEntity.Key<KeyType>> extends UpdateLayoutHandler<KeyType>
	{
		public UpdateHandler(KeyType tileKey)
		{
			super(tileKey);
		}

		public void textChanging(ChangeTextDirective.Notification change, PendingTransaction transaction)
		{
			try
			{
				executeUpdate();
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.DEBUG, e, "Failed to update the layout of a label after data change.");
			}
		}
	}

	public LabelComposite(LabelType component)
	{
		super(component);
	}
}
