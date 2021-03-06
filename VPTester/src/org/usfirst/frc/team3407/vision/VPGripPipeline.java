package org.usfirst.frc.team3407.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.*;

import edu.wpi.first.wpilibj.vision.VisionPipeline;

import org.usfirst.frc.team3407.vp.Tester;

/**
* GripPipeline class.
*
*/
public abstract class VPGripPipeline implements VisionPipeline, Tester.VisionPipelineOutput  {

	private Mat	source;
	
	//Outputs
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	// Find Countours 
	private boolean findContoursExternalOnly = false;

	// Filter Contours
	private double filterContoursMinArea = 300.0;
	private double filterContoursMinPerimeter = 100.0;
	private double filterContoursMinWidth = 0.0;
	private double filterContoursMaxWidth = 1000.0;
	private double filterContoursMinHeight = 0.0;
	private double filterContoursMaxHeight = 1000.0;
	private double[] filterContoursSolidity = {0, 100};
	private double filterContoursMaxVertices = 1000.0;
	private double filterContoursMinVertices = 4.0;
	private double filterContoursMinRatio = 0.0;
	private double filterContoursMaxRatio = 100000.0;

	// HSL Thresholds
	protected double[] getHslThresholdHue() {
		return new double[] { 47.0, 98.0 };
	}

	protected double[] getHslThresholdSaturation() {
		return new double[] { 126.0, 255.0 };
	}

	protected double[] getHslThresholdLuminance() {
		return new double[] { 19.0, 155.0 };
	}
	
	@Override
	public ArrayList<MatOfPoint> getContours() {
		return filterContoursOutput;
	}

	public abstract Rect getTarget();
		
	public Point getTargetPointFromCenter() {
		Point centerPoint = new Point(source.width() / 2, source.height() / 2);
		Rect targetRect = getTarget();
		Point targetPoint = new Point(targetRect.x + (targetRect.width / 2), 
				targetRect.y + (targetRect.height / 2));
		
		return new Point(targetPoint.x - centerPoint.x, targetPoint.y - centerPoint.y);
	}
	
	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	public void process(Mat image) {
		
		source = image;
		
		// Step HSL_Threshold0:
		hslThreshold(source, getHslThresholdHue(), getHslThresholdSaturation(), getHslThresholdLuminance(), hslThresholdOutput);

		// Step Find_Contours0:
		findContours(hslThresholdOutput, findContoursExternalOnly, findContoursOutput);

		// Step Filter_Contours0:
		filterContours(findContoursOutput, filterContoursMinArea, 
				filterContoursMinPerimeter, filterContoursMinWidth, 
				filterContoursMaxWidth, filterContoursMinHeight, 
				filterContoursMaxHeight, filterContoursSolidity, 
				filterContoursMaxVertices, filterContoursMinVertices, 
				filterContoursMinRatio, filterContoursMaxRatio, 
				filterContoursOutput);
	}

	/**
	 * This method is a generated getter for the output of a HSL_Threshold.
	 * @return Mat output from HSL_Threshold.
	 */
	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a Find_Contours.
	 * @return ArrayList<MatOfPoint> output from Find_Contours.
	 */
	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	/**
	 * This method is a generated getter for the output of a Filter_Contours.
	 * @return ArrayList<MatOfPoint> output from Filter_Contours.
	 */
	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}


	/**
	 * Segment an image based on hue, saturation, and luminance ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param lum The min and max luminance
	 * @param output The image in which to store the output.
	 */
	private void hslThreshold(Mat input, double[] hue, double[] sat, double[] lum,
		Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HLS);
		Core.inRange(out, new Scalar(hue[0], lum[0], sat[0]),
			new Scalar(hue[1], lum[1], sat[1]), out);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly,
		List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		}
		else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}


	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
		double minPerimeter, double minWidth, double maxWidth, double minHeight, double
		maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
		minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
			final double ratio = bb.width / (double)bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
	}




}

