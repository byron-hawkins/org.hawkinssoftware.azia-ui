package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import org.hawkinssoftware.azia.ui.component.PaintableActor;

public class RepaintInstanceDirective extends RepaintDirective
{
	protected final PaintableActor actor;

	public RepaintInstanceDirective(PaintableActor actor)
	{
		this.actor = actor;
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
