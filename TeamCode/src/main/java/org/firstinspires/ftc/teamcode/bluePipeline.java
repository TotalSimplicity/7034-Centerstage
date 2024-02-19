package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class bluePipeline extends OpenCvPipeline {
    private static String position;
    public int bluePixelsLeft;
    public int bluePixelsMid;
    public int bluePixelsRight;

    @Override
    public Mat processFrame(Mat input) {
        Mat camPreview = new Mat();
        input.copyTo(camPreview);
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

        // Get amount of blue pixels
        bluePixelsLeft = countBluePixels(leftImage);
        bluePixelsMid = countBluePixels(midImage);
        bluePixelsRight = countBluePixels(rightImage);

        // Check which ROI has the most blue pixels and return the position
        if (bluePixelsLeft >= bluePixelsMid && bluePixelsLeft >= bluePixelsRight) {
            position = "left";
        } else if (bluePixelsMid >= bluePixelsLeft && bluePixelsMid >= bluePixelsRight) {
            position = "mid";
        } else {
            position = "right";
        }

        // Release Mats
        leftImage.release();
        midImage.release();
        rightImage.release();
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGRA2RGB);
        return camPreview;
    }

    private int countBluePixels(Mat image) {
        // Convert the image to HSV
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        // Define blue color range in HSV with handling for circular/wrap around hue values
        Scalar lowerBlue = new Scalar(80, 90, 120);
        Scalar upperBlue = new Scalar(130, 255, 255);

        // Check for circular hue values and create a mask accordingly
        if (lowerBlue.val[0] > upperBlue.val[0]) {
            Mat mask1 = new Mat();
            Mat mask2 = new Mat();

            Scalar upperBlue1 = new Scalar(upperBlue.val[0], upperBlue.val[1], upperBlue.val[2]);
            Scalar lowerBlue2 = new Scalar(lowerBlue.val[0], lowerBlue.val[1], lowerBlue.val[2]);

            Core.inRange(image, lowerBlue, upperBlue1, mask1);
            Core.inRange(image, lowerBlue2, upperBlue, mask2);

            Core.bitwise_or(mask1, mask2, image);

            mask1.release();
            mask2.release();
        } else {
            // Threshold the image to get blue pixels
            Core.inRange(image, lowerBlue, upperBlue, image);
        }

        // Count non-zero pixels in the mask (blue pixels)
        int bluePixelCount = Core.countNonZero(image);

        return bluePixelCount;
    }

    public static String getPosition() {
        // Allow access to position by auto opmode
        return position;
    }
}
