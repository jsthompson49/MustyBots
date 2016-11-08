package org.usfirst.frc.team3407.networktables;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTableConsole {

	public static void main(String[] args) {

		final String TABLE_NAME = (args.length > 0) ? args[0] : "TestTableName";
		final String INPUT_KEY = "TestInput";
		final String STATUS_KEY = "TestStatus";
		
		System.out.println("LibraryPath=" + System.getProperties().getProperty("java.library.path"));
		NetworkTable.setClientMode();
	
		// Implicilty sets IP address and 
		NetworkTable.setTeam(3407);
		
		NetworkTable table = NetworkTable.getTable(TABLE_NAME);
		table.putString(INPUT_KEY, "Hello");
		
		while(true) {
			try {
				String value = table.getString(STATUS_KEY, "");
				System.out.println("Value=" + value);
				Thread.sleep(3000);
			}
			catch (Exception e) {
				System.err.println("ERROR: " + e.getMessage());
			}
			
		}
			
	}
}
