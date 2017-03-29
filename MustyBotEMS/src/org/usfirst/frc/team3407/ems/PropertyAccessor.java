package org.usfirst.frc.team3407.ems;

import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.collections.AspectCollection; 
import com.thingworx.types.constants.Aspects;
import com.thingworx.types.constants.DataChangeType;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

import edu.wpi.first.wpilibj.tables.ITable;

import org.joda.time.DateTime;

public abstract class PropertyAccessor {
	
	//private static int counter = 0;
	
	private String name;
	private String propertyName;
	private String description;
	
	public PropertyAccessor(String name, String description) {
		this.name = name;
		this.propertyName = name.replace(' ', '_');
		this.description = description;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public DateTime getTimestamp() {
		//counter++;
		//return new DateTime(2017, 12, 25, (counter / 3600000) % 24, (counter /60000) % 60, (counter /1000) % 60, counter % 1000);
		return new DateTime();
	}

	public abstract BaseTypes getType();
	public abstract void updateProperty(ITable table, VirtualThing thing) 
			throws Exception;

	PropertyDefinition getDefinition() {
		PropertyDefinition propertyDef = new PropertyDefinition(getPropertyName(), 
				getDescription(), getType());
		
		//Create an aspect collection to hold all of the different y 
		AspectCollection aspects = new AspectCollection();
		
		//Add the dataChangeType aspect 
		aspects.put(Aspects.ASPECT_DATACHANGETYPE, new StringPrimitive(DataChangeType.ALWAYS.name()));
		
		//Add the dataChangeThreshold aspect 
		aspects.put(Aspects.ASPECT_DATACHANGETHRESHOLD, new NumberPrimitive(0.0));
		
		//Add the cacheTime aspect 
		aspects.put(Aspects.ASPECT_CACHETIME, new IntegerPrimitive(-1));
		
		//Add the isPersistent aspect
		aspects.put(Aspects.ASPECT_ISPERSISTENT, new BooleanPrimitive(false));
		
		//Add the isReadOnly aspect 
		aspects.put(Aspects.ASPECT_ISREADONLY, new BooleanPrimitive(true));
		
		//Add the pushType aspect 
		aspects.put("pushType", new StringPrimitive(DataChangeType.ALWAYS.name()));
		
		//Add the defaultValue aspect 
		//aspects.put(Aspects.ASPECT_DEFAULTVALUE, new BooleanPrimitive(true));
		
		//Set the aspects of the property definition 
		propertyDef.setAspects(aspects);
		
		return propertyDef;
	}

	
}
