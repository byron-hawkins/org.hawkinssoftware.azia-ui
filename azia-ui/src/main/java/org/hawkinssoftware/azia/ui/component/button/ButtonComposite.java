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
package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

/**
 * Composite container for an <code>AbstractButton</code> and its constituent characteristic behaviors. The
 * <code>ButtonComposite</code> is an empty shell until things are plugged into it (no label or anything).
 * 
 * @param <ButtonType>
 *            the generic type
 * @param <PainterType>
 *            the generic type
 * @author Byron Hawkins
 */
public class ButtonComposite<ButtonType extends AbstractButton, PainterType extends ComponentPainter<ButtonType>> extends
		AbstractComposite<ButtonType, PainterType>
{
	@InvocationConstraint
	public ButtonComposite(ButtonType component)
	{
		super(component);
	}
}
