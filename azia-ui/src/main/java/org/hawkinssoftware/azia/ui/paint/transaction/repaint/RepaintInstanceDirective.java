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
package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.PaintableActorDelegate;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class RepaintInstanceDirective extends RepaintDirective
{
	protected final PaintableActor actor;

	public RepaintInstanceDirective(PaintableActorDelegate actor)
	{
		this.actor = actor.getActor();
	}

	@Override
	public PaintableActor getActor()
	{
		return actor;
	}
	
	@Override
	public Object getInstanceKey()
	{
		return actor;
	}
}
