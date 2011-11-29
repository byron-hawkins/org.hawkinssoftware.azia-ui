package org.hawkinssoftware.azia.ui.component.cell.handler;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.transaction.state.SetFocusAction;
import org.hawkinssoftware.azia.ui.component.transaction.window.ApplicationFocusHandler;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

public class CellViewportFocusHandler extends BorderPlugin<CellViewport> implements UserInterfaceHandler
{
	@InvocationConstraint(domains = AssemblyDomain.class)
	public static void install(CellViewportComposite<?> viewport)
	{
		CellViewportFocusHandler handler = new CellViewportFocusHandler(viewport);

		viewport.getPainter().borderPlugins.insertPlugin(handler);
		viewport.installHandler(handler);

		ComponentRegistry.getInstance().getFocusHandler().registerComponent(viewport);
	}

	private static final Color FOCUS_COLOR = Color.black;
	private static final Color NON_FOCUS_COLOR = Color.gray;

	private final CellViewportComposite<?> viewport;
	private final ApplicationFocusHandler focusHandler;

	private final SetFocusAction action;

	public CellViewportFocusHandler(CellViewportComposite<?> viewport)
	{
		super(Inset.homogenous(2));

		this.viewport = viewport;
		focusHandler = ComponentRegistry.getInstance().getFocusHandler();

		action = new SetFocusAction(viewport);
	}

	@Override
	public void paintBorder(CellViewport component)
	{
		Canvas c = Canvas.get();

		if (focusHandler.getFocusedComponent(viewport) == viewport)
		{
			if (focusHandler.applicationHasFocus())
			{
				c.pushColor(FOCUS_COLOR);
			}
			else
			{
				c.pushColor(NON_FOCUS_COLOR);
			}
			BorderPlugin.Solid.paintBorder(c, inset);
		}
		else
		{
			c.pushColor(NON_FOCUS_COLOR);
			BorderPlugin.Solid.paintBorder(c, BorderPlugin.Solid.HAIRLINE);
		}
	}

	public void mouseEvent(MouseAware.EventPass pass, PendingTransaction transaction)
	{
		if (pass.event().getButtonPress() != null)
		{
			transaction.contribute(action);
		}
	}
}
