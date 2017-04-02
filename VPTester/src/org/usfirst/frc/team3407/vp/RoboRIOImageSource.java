package org.usfirst.frc.team3407.vp;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.wpi.first.smartdashboard.net.TCPImageFetcher;

public class RoboRIOImageSource implements ImageSource {

	private TCPImageFetcher source = new TCPImageFetcher(3407);
	
	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public Mat next() {
		try {
			BufferedImage image = source.fetch();
			return bufferedImageToMat(image);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
}
