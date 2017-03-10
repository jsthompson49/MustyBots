package org.usfirst.frc.team3407.vp;

import java.io.File;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.vision.VisionPipeline;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import org.usfirst.frc.team3407.robot.vision.BoilerEvaluator;
import org.usfirst.frc.team3407.robot.vision.GripPipeline;
import org.usfirst.frc.team3407.robot.vision.PegEvaluator;
import org.usfirst.frc.team3407.robot.vision.TargetEvaluator;
import org.usfirst.frc.team3407.vision.BoilerPipeline;

public class Tester {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
			    
		String imageFileName = args[0];
		
		System.out.println("Vision Processing Tester");
		System.out.println("Processing: " + imageFileName);

		ImageSource source = null;
		File file = new File(imageFileName);
		if(file.exists() && file.isDirectory()) {
			int angle = (args.length > 1) ? Integer.parseInt(args[1]) : 0;
			source = new Sample2017ImageSource(args[0], angle);
		}
		
		if (source == null) {
			//URL.
			//source = image(args[0]);
		}
		
		if (source == null) {
			System.out.println("No input source for images");
		}
		
		TargetEvaluator targetEvaluator = imageFileName.endsWith("Boiler") ? new BoilerEvaluator() :
				new PegEvaluator();
		boolean robotVision = true;
		if (robotVision) {
			GripPipeline pipeline = new GripPipeline();
			VisionPipelineAdapter pipelineOutput = new VisionPipelineAdapter(pipeline);		
			processImages(source, pipeline, pipelineOutput, targetEvaluator);
		}
		else {
			BoilerPipeline pipeline = new BoilerPipeline();
			processImages(source, pipeline, pipeline, targetEvaluator);
		}
	}
		  
	public static void processImages(ImageSource source, VisionPipeline pipeline, VisionPipelineOutput pipelineOutput, 
			TargetEvaluator targetEvaluator) {

		while(source.hasNext()) {
			Mat image = source.next();
			pipeline.process(image);
			ArrayList<MatOfPoint> contours = pipelineOutput.getContours();
			boolean targetAcquired = targetEvaluator.process(contours);
			System.out.println("Target acquired: " + targetAcquired);
		
			Rect target = targetEvaluator.getTarget();
			if(target == null) {
				System.out.println("No target identified");
			}
			else {
				System.out.println("Target Rectangle: " + target + 
						" targetArea=" + target.area() + 
						" fromCenter=" + targetEvaluator.getTargetHorizontalOffset(new Point(image.width() / 2, image.height() / 2)));
			}
		}
	}
	
	public interface VisionPipelineOutput {
		public ArrayList<MatOfPoint> getContours();
	}
	
	private static class VisionPipelineAdapter implements VisionPipelineOutput {

		private GripPipeline pipeline;
		
		public VisionPipelineAdapter(GripPipeline pipeline) {
			this.pipeline = pipeline;
		}
		
		@Override
		public ArrayList<MatOfPoint> getContours() {			
			return pipeline.filterContoursOutput();
		}
	}
}
