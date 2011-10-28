package org.hawkinssoftware.azia.ui.paint.basic;

import org.hawkinssoftware.azia.core.layout.BoundedEntity.PanelRegion;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollSlider;
import org.hawkinssoftware.azia.ui.component.scalar.SliderKnob;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.component.text.TextArea;
import org.hawkinssoftware.azia.ui.component.text.TextViewport;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.PainterFactory;
import org.hawkinssoftware.azia.ui.paint.RegionPainter;
import org.hawkinssoftware.azia.ui.paint.basic.button.PushButtonPainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellViewportPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPanePainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderKnobPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderTrackPainter;
import org.hawkinssoftware.azia.ui.paint.basic.text.LabelPainter;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextAreaPainter;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextViewportPainter;

public class BasicPainterFactory extends PainterFactory
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <ComponentType extends AbstractComponent> InstancePainter<ComponentType> getComponentPainter(Class<? extends ComponentType> componentType)
	{
		InstancePainter<?> painter = null;
		if (componentType == PushButton.class)
		{
			painter = new PushButtonPainter();
		}
		else if (componentType == Label.class)
		{
			painter = new LabelPainter();
		}
		else if (componentType == TextArea.class)
		{
			painter = new TextAreaPainter();
		}
		else if (componentType == ScrollSlider.class)
		{
			painter = new SliderPainter();
		}
		else if (componentType == SliderTrack.class)
		{
			painter = new SliderTrackPainter();
		}
		else if (componentType == SliderKnob.class)
		{
			painter = new SliderKnobPainter();
		}
		else if (componentType == ScrollPane.class)
		{
			painter = new ScrollPanePainter();
		}
		else if (componentType == TextViewport.class)
		{
			painter = new TextViewportPainter();
		}
		else if (CellViewport.class.isAssignableFrom(componentType))
		{
			painter = new CellViewportPainter();
		}

		return (InstancePainter<ComponentType>) painter;
	}

	@Override
	public <RegionType extends PanelRegion> RegionPainter<RegionType> getRegionPainter(Class<? extends RegionType> regionType)
	{
		return new PlainRegionPainter<RegionType>();
	}
}
