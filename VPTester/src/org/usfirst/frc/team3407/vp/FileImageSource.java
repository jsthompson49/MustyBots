package org.usfirst.frc.team3407.vp;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public abstract class FileImageSource implements ImageSource {

	protected abstract String nextFileName();
	
	@Override
	public Mat next() {
		String fileName = null;
		while(hasNext()) {
			fileName = nextFileName();
			System.out.println("Next Image=" + fileName);
			File file = new File(fileName);
			if(file.exists()) {
				break;
			}
			else {
				System.out.println("File does not exist");
			}
		}
		return (fileName == null) ? null : Imgcodecs.imread(fileName);
	}
}
