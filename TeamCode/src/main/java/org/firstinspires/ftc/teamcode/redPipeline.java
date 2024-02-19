package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class redPipeline extends OpenCvPipeline {
    private String position;
    public int redPixelsLeft;
    public int redPixelsMid;
    public int redPixelsRight;

    @Override
    public Mat processFrame(Mat input) {
        // Create unaltered copy of cam for preview
        Mat camPreview = input.clone();

        // Convert input image format if needed
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGBA2BGR);

        // Define ROI locations
        Rect leftRoi = new Rect(0, 0, 426, 720);
        Rect midRoi = new Rect(426, 0, 426, 720);
        Rect rightRoi = new Rect(852, 0, 426, 720);

        // Split ROIs into images
        Mat leftImage = new Mat(input, leftRoi);
        Mat midImage = new Mat(input, midRoi);
        Mat rightImage = new Mat(input, rightRoi);

        // Get amount of red pixels
        redPixelsLeft = countRedPixels(leftImage);
        redPixelsMid = countRedPixels(midImage);
        redPixelsRight = countRedPixels(rightImage);

        // Create a black mask for red pixels
        Mat redMask = new Mat();
        Imgproc.cvtColor(camPreview, redMask, Imgproc.COLOR_BGR2HSV);
        Scalar lowerRed = new Scalar(0, 100, 100); // Adjusted lower threshold
        Scalar upperRed = new Scalar(10, 255, 255); // Adjusted upper threshold
        Core.inRange(redMask, lowerRed, upperRed, redMask);

        // Overlay the black mask on the camPreview
        camPreview.setTo(new Scalar(0, 0, 0), redMask);

        // Check which ROI has the most red pixels and return the position
        if (redPixelsLeft >= redPixelsMid && redPixelsLeft >= redPixelsRight) {
            position = "left";
        } else if (redPixelsMid >= redPixelsLeft && redPixelsMid >= redPixelsRight) {
            position = "mid";
        } else {
            position = "right";
        }

        // Release Mats
        leftImage.release();
        midImage.release();
        rightImage.release();
        redMask.release();

        return camPreview;
    }

    private int countRedPixels(Mat image) {
        // Convert the image to HSV
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        // Define red color range in HSV with handling for circular/wrap around hue values
        Scalar lowerRed = new Scalar(0, 100, 100); // Adjusted lower threshold
        Scalar upperRed = new Scalar(10, 255, 255); // Adjusted upper threshold

        // Check for circular hue values and create a mask accordingly
        if (lowerRed.val[0] > upperRed.val[0]) {
            Mat mask1 = new Mat();
            Mat mask2 = new Mat();

            Scalar upperRed1 = new Scalar(upperRed.val[0], upperRed.val[1], upperRed.val[2]);
            Scalar lowerRed2 = new Scalar(lowerRed.val[0], lowerRed.val[1], lowerRed.val[2]);

            Core.inRange(image, lowerRed, upperRed1, mask1);
            Core.inRange(image, lowerRed2, upperRed, mask2);

            Core.bitwise_or(mask1, mask2, image);

            mask1.release();
            mask2.release();
        } else {
            // Threshold the image to get red pixels
            Core.inRange(image, lowerRed, upperRed, image);
        }

        // Count non-zero pixels in the mask (red pixels)
        int redPixelCount = Core.countNonZero(image);

        return redPixelCount;
    }

    public String getPosition() {
        // Allow access to position by auto opmode
        return position;
    }
}
