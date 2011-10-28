package org.hawkinssoftware.azia.ui.paint.basic.text;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugins;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelContentPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelTextPlugin;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

public class LabelPainter<LabelType extends Label> extends ComponentPainter<LabelType> implements Label.Painter
{
	private static final Color FOREGROUND_COLOR = Color.black;

	private BackgroundPlugin<LabelType> background = new BackgroundPlugin<LabelType>();
	public final BorderPlugins<LabelType> borderPlugins = new BorderPlugins<LabelType>();
	private LabelContentPlugin labelPlugin = new LabelTextPlugin.Center();

	public LabelPainter()
	{
		borderPlugins.insertPlugin(new BorderPlugin.Empty<LabelType>(Inset.homogenous(2)));
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setBackground(BackgroundPlugin<LabelType> background)
	{
		this.background = background;
	}

	// TODO: could get rid of this setter if all plugins can be compositionally registered, without risk of
	// mis-registration
	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setLabelPlugin(LabelContentPlugin labelPlugin)
	{
		getComponent().changeHandler(this.labelPlugin, labelPlugin);

		this.labelPlugin = labelPlugin;
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		int size = labelPlugin.getPackedSize(axis);
		switch (axis)
		{
			case H:
				size += borderPlugins.getCumulativeInset(Axis.H);
				break;
			case V:
				size += borderPlugins.getCumulativeInset(Axis.V);
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
		return size;
	}

	@Override
	public void paint(LabelType label)
	{
		Canvas c = Canvas.get();

		background.paint(label);
		borderPlugins.paintAndNarrow(c, component);

		c.pushColor(FOREGROUND_COLOR);
		labelPlugin.paint(label);
	}
}
