package org.usfirst.frc.team3407.ems;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.structs.VTQ;
import com.thingworx.types.constants.QualityStatus;

import edu.wpi.first.wpilibj.tables.ITable;

public class NumberPropertyAccessor extends PropertyAccessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(NumberPropertyAccessor.class);

	private Double defaultValue;
	
	public NumberPropertyAccessor(String name) {
		this(name, new Double(0.0));
	}

	public NumberPropertyAccessor(String name, Double defaultValue) {
		this(name, name, defaultValue);
	}
	
	public NumberPropertyAccessor(String name, String description,
			Double defaultValue) {
		super(name, description);
		this.defaultValue = defaultValue;
	}

	@Override
	public BaseTypes getType() {
		return BaseTypes.NUMBER;
	}
	
	public void updateProperty(ITable table, VirtualThing thing) 
			throws Exception {

		final String name = getName();
		
		Double value = table.getNumber(name, defaultValue);
		if(LOGGER.isInfoEnabled()) {
			LOGGER.debug("updateProperty() name=" + name + " value=" + value);
		}
		
		VTQ thingValue = new VTQ(new NumberPrimitive(value), getTimestamp(), QualityStatus.GOOD);

		thing.setPropertyVTQ(getPropertyName(), thingValue, true /* force */);		
	}
}
