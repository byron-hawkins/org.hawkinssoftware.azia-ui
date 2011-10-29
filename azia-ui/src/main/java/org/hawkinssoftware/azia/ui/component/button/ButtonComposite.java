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
 * DOC comment task awaits.
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
