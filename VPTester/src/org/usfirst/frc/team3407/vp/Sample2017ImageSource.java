package org.usfirst.frc.team3407.vp;

public class Sample2017ImageSource extends FileImageSource {

	private static final int[][] BOILER_IMAGES = {
			{ 4, 2 },
			{ 10, 4 },
			{ 12, 7 },
			{ 11, 3 }
	};

	private static final int[][] PEG_IMAGES = {
			{ 9, 1 },
	};
	
	private String imageFilesBaseDir;
	private int angle;
	private int distance;
	private int endDistance;
	
	public Sample2017ImageSource(String imageFilesBaseDir, int angle) {
		this.imageFilesBaseDir = imageFilesBaseDir;
		if(imageFilesBaseDir.endsWith("Boiler")) {
			distance = BOILER_IMAGES[angle][0];
			endDistance = BOILER_IMAGES[angle][1];
		}
		else {
			distance = PEG_IMAGES[angle][0];
			endDistance = PEG_IMAGES[angle][1];			
		}
	}
	
	@Override
	public boolean hasNext() {
		return distance >= endDistance;
	}

	@Override
	public String nextFileName() {
		
		StringBuilder fileName = new StringBuilder();
		fileName.append(imageFilesBaseDir).append("/Angle").append(angle);
		fileName.append("/1ftH").append(distance).append("ftD").append(angle);
		fileName.append("Angle0Brightness.jpg");
			
		distance--;
		
		return fileName.toString();
	}
}
