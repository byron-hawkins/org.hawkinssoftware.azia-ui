package org.hawkinssoftware.azia.ui.component;

public abstract class ComponentIdentifier
{
	private final AbstractComponent component;

	protected ComponentIdentifier(AbstractComponent component)
	{
		this.component = component;
	}
	
	public AbstractComponent getComponent()
	{
		return component;
	}
}
