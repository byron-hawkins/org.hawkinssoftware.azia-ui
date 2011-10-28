package org.hawkinssoftware.azia.ui.paint.basic.button;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugins;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelContentPlugin;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public class PushButtonPainter extends ComponentPainter<PushButton> implements PushButton.Painter
{
	public static final Color PLAIN_BACKGROUND_COLOR = new Color(0xEAEAEA);
	private static final Color CLICKED_BACKGROUND = new Color(0x880000);
	public static final Color BORDER_COLOR = new Color(0x333333);
	private static final Color FOREGROUND = Color.black;
	private static final int GAP = 4;

	private BackgroundPlugin<PushButton> background = new ActiveSolidBackground<PushButton>();
	public final BorderPlugins<PushButton> borderPlugins = new BorderPlugins<PushButton>();
	private LabelContentPlugin textPlugin = null;
	private LabelContentPlugin iconPlugin = null;

	@ValidateWrite.Exempt
	private BoundsType boundsType = BoundsType.GLYPH;

	public PushButtonPainter()
	{
		borderPlugins.insertPlugin(new BorderPlugin.Solid<PushButton>(BORDER_COLOR));
		borderPlugins.insertPlugin(new BorderPlugin.Empty<PushButton>(Inset.homogenous(4)));
	}

	public void setBackground(BackgroundPlugin<PushButton> background)
	{
		this.background = background;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setTextPlugin(LabelContentPlugin textPlugin)
	{
		getComponent().changeHandler(this.textPlugin, textPlugin);

		this.textPlugin = textPlugin;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setIconPlugin(LabelContentPlugin iconPlugin)
	{
		getComponent().changeHandler(this.iconPlugin, iconPlugin);

		this.iconPlugin = iconPlugin;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setBoundsType(BoundsType boundsType)
	{
		this.boundsType = boundsType;
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		int size = 0;
		if (textPlugin != null)
		{
			size += textPlugin.getPackedSize(axis);
		}
		if (iconPlugin != null)
		{
			size += iconPlugin.getPackedSize(axis);
			if (textPlugin != null)
			{
				size += GAP;
			}
		}
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
	public void paint(PushButton button)
	{
		Canvas c = Canvas.get();

		if (background != null)
		{
			background.paint(button);
		}
		borderPlugins.paintAndNarrow(c, button);

		c.pushColor(FOREGROUND);

		if (iconPlugin != null)
		{
			iconPlugin.paint(button);
			c.pushBoundsPosition(iconPlugin.getPackedSize(Axis.H) + GAP, 0);
		}
		if (textPlugin != null)
		{
			textPlugin.paint(button);
		}
	}
}
