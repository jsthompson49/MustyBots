package org.usfirst.frc.team3407.ems;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

import org.usfirst.frc.team3407.ems.simulator.SimulatedNumber;
import org.usfirst.frc.team3407.ems.simulator.SimulatedTable;

public class ServerMain extends ConnectedThingClient {

	private static final Logger LOG = LoggerFactory.getLogger(ServerMain.class);
	
	private static final String THING_NAME = "RobotData";
	private static final String TABLE_NAME = "SmartDashboard/DB";
	
	private static String APP_KEY = System.getProperty("appKey");

	public ServerMain(ClientConfigurator config) throws Exception {
		super(config);
	}

	public static void main(String[] args) {
	
		ClientConfigurator config = new ClientConfigurator();
	
		// Set the URI of the server that we are going to connect to
		config.setUri("ws://localhost:8080/Thingworx/WS");
		
		// Set the ApplicationKey. This will allow the client to authenticate with the server.
		// It will also dictate what the client is authorized to do once connected.
		config.setAppKey(APP_KEY);
		
		// This will allow us to test against a server using a self-signed certificate.
		// This should be removed for production systems.
		config.ignoreSSLErrors(true); // All self signed certs
	
		NetworkTable.setClientMode();
		
		// Implicilty sets IP address and 
		NetworkTable.setTeam(3407);
		
		try {
			
			// Create our client.
			ServerMain server = new ServerMain(config);
			
			// Start the client. The client will connect to the server and authenticate
			// using the ApplicationKey specified above.
			server.start();
			
			// Wait for the client to connect.
			if (server.waitForConnection(30000)) {
				
				LOG.info("The client is now connected.");
				
				//
				// Create a VirtualThing and bind it to the client
				///////////////////////////////////////////////////////////////
				
				HashMap<String,String> tableStringMap = new HashMap<String,String>();
				tableStringMap.put("Encoder", "String 7");
				tableStringMap.put("Speed", "String 9");
				tableStringMap.put("Error", "String 8");

				ArrayList<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>();
				accessors.add(new NumberPropertyAccessor("Encoder"));
				accessors.add(new NumberPropertyAccessor("Speed"));
				accessors.add(new NumberPropertyAccessor("Error"));
				for(PropertyAccessor accessor : accessors) {
					accessor.setTableStringMap(tableStringMap);
				}
				
				NetworkTable netTable = NetworkTable.getTable("SmartDashboard");

				// Wait for communication
				Thread.sleep(4000);
				
				LOG.debug("NetworkTable: name=" + netTable + " connected=" + netTable.isConnected());
				ITable table;
				if(netTable.isConnected()) {
					table = netTable.getSubTable("DB");
					LOG.debug("NetworkTable: name=" + table);
				}
				else {
					HashMap<String,SimulatedNumber> map = new HashMap<String,SimulatedNumber>();
					map.put("Encoder", new SimulatedNumber(1500, 50));
					map.put("Speed", new SimulatedNumber(0.8, 0.04));
					map.put("Error", new SimulatedNumber(0, 50));
					table = new SimulatedTable(map);				
				}
							
				NetworkTableThing tableThing = new NetworkTableThing(THING_NAME, THING_NAME,
						server, table, accessors); 
				server.bindThing(tableThing);
				
				// This will prevent the main thread from exiting. It will be up to another thread
				// of execution to call client.shutdown(), allowing this main thread to exit.
				while (!server.isShutdown()) {
					
					//Thread.sleep(1000);
					
					// Every 1 seconds we tell the thing to process a scan request. This is
					// an opportunity for the thing to query a data source, update property
					// values, and push new property values to the server.
					//
					// This loop demonstrates how to iterate over multiple VirtualThings
					// that have bound to a client. In this simple example the things
					// collection only contains one VirtualThing.
					for (VirtualThing vt : server.getThings().values()) {
						vt.processScanRequest();
					}
				}
				
			} else {
				// Log this as a warning. In production the application could continue
				// to execute, and the client would attempt to reconnect periodically.
				LOG.warn("Client did not connect within 30 seconds. Exiting");
			}
			
		} catch (Exception e) {
			LOG.error("An exception occured during execution.", e);
		}
		
		LOG.info("ServerMain is done. Exiting");
	}
}
