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

import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;

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
	public LabelComposite(LabelType component)
	{
		super(component);
	}
}
