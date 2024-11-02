//workign on making this better, dont fuck with it. I fits messign up anythign else comment it out dont delete it.
package org.firstinspires.ftc.teamcode.OpModes.Angle_PID_Tutorial;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

@TeleOp(name = "OpenCV Testing")

public class opencv extends LinearOpMode {

    double cX = 0;
    double cY = 0;
    double width = 0;

    private OpenCvCamera controlHubCam;
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 360;

    public static final double objectWidthInRealWorldUnits = 3.75;
    public static final double focalLength = 728;

    @Override
    public void runOpMode() {
        // Initialize OpenCV
        initOpenCV();

        // Setup telemetry with the dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        FtcDashboard.getInstance().startCameraStream(controlHubCam, 30);

        // Wait for the start of the match
        waitForStart();

        // Main loop
        while (opModeIsActive()) {
            // Telemetry to display coordinates and distance
            telemetry.addData("Coordinate", "(" + (int) cX + ", " + (int) cY + ")");
            telemetry.addData("Distance in Inch", (getDistance(width)));
            telemetry.update();

            // Only move if we have a valid width (meaning an object was detected)
            if (width > 0) {
                double distance = getDistance(width);
                double speed = 0.5; // Set desired speed

                // Define distance threshold to start moving forward
                double distanceThreshold = 12.0; // Change based on your setup

                if (distance < distanceThreshold) {
                    // If the object is close enough, move forward
                    frontLeft.setPower(speed);
                    frontRight.setPower(speed);
                    backLeft.setPower(speed);
                    backRight.setPower(speed);
                } else {
                    // Stop the robot if the object is too far
                    frontLeft.setPower(0);
                    frontRight.setPower(0);
                    backLeft.setPower(0);
                    backRight.setPower(0);
                }

                // Control the turning based on the position of the object
                if (cX > CAMERA_WIDTH / 2 + 20) { // Object is to the right
                    frontLeft.setPower(speed * 0.5); // Slow turn left
                    frontRight.setPower(speed); // Move right
                    backLeft.setPower(speed * 0.5); // Slow turn left
                    backRight.setPower(speed); // Move right
                } else if (cX < CAMERA_WIDTH / 2 - 20) { // Object is to the left
                    frontLeft.setPower(speed); // Move left
                    frontRight.setPower(speed * 0.5); // Slow turn right
                    backLeft.setPower(speed); // Move left
                    backRight.setPower(speed * 0.5); // Slow turn right
                } else {
                    // Move straight if centered
                    frontLeft.setPower(speed);
                    frontRight.setPower(speed);
                    backLeft.setPower(speed);
                    backRight.setPower(speed);
                }
            } else {
                // If no object detected, stop the robot
                frontLeft.setPower(0);
                frontRight.setPower(0);
                backLeft.setPower(0);
                backRight.setPower(0);
            }
        }

        // Stop streaming the camera after the op mode ends
        controlHubCam.stopStreaming();
    }


    private void initOpenCV() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        controlHubCam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);

        controlHubCam.setPipeline(new BlobDetectionPipeline());
        controlHubCam.openCameraDevice();
        controlHubCam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPRIGHT);
    }

    class BlobDetectionPipeline extends OpenCvPipeline {
        @Override
        public Mat processFrame(Mat input) {
            Mat yellowMask = preprocessFrame(input, new Scalar(20, 100, 100), new Scalar(30, 255, 255)); // Adjusted for yellow
            Mat redMask = preprocessFrame(input, new Scalar(0, 100, 100), new Scalar(10, 255, 255)); // Adjusted for red
            Mat blueMask = preprocessFrame(input, new Scalar(110, 100, 100), new Scalar(130, 255, 255)); // Adjusted for blue

            List<MatOfPoint> yellowContours = new ArrayList<>();
            List<MatOfPoint> redContours = new ArrayList<>();
            List<MatOfPoint> blueContours = new ArrayList<>();

            // Find contours for each color
            Imgproc.findContours(yellowMask, yellowContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.findContours(redMask, redContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.findContours(blueMask, blueContours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Detect largest contour for each color
            detectLargestContour(input, yellowContours, new Scalar(255, 255, 0)); // Draw yellow
            detectLargestContour(input, redContours, new Scalar(0, 0, 255)); // Draw red
            detectLargestContour(input, blueContours, new Scalar(255, 0, 0)); // Draw blue

            return input;
        }

        private Mat preprocessFrame(Mat frame, Scalar lowerBound, Scalar upperBound) {
            Mat hsvFrame = new Mat();
            Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
            Mat mask = new Mat();
            Core.inRange(hsvFrame, lowerBound, upperBound, mask);
            return mask;
        }

        private void detectLargestContour(Mat input, List<MatOfPoint> contours, Scalar color) {
            if (!contours.isEmpty()) {
                MatOfPoint largestContour = findLargestContour(contours);
                if (largestContour != null) {
                    Imgproc.drawContours(input, contours, contours.indexOf(largestContour), color, 2);
                    width = calculateWidth(largestContour);
                    Moments moments = Imgproc.moments(largestContour);
                    cX = moments.get_m10() / moments.get_m00();
                    cY = moments.get_m01() / moments.get_m00();
                    drawCentroid(input);
                }
            }
        }

        private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
            double maxArea = 0;
            MatOfPoint largestContour = null;
            for (MatOfPoint contour : contours) {
                double area = Imgproc.contourArea(contour);
                if (area > maxArea) {
                    maxArea = area;
                    largestContour = contour;
                }
            }
            return largestContour;
        }

        private double calculateWidth(MatOfPoint contour) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            return boundingRect.width;
        }

        private void drawCentroid(Mat input) {
            String label = "(" + (int) cX + ", " + (int) cY + ")";
            Imgproc.putText(input, label, new Point(cX + 10, cY), Imgproc.FONT_HERSHEY_COMPLEX, 0.5, new Scalar(0, 255, 0), 2);
            Imgproc.circle(input, new Point(cX, cY), 5, new Scalar(0, 255, 0), -1);
        }
    }

    private static double getDistance(double width) {
        return (objectWidthInRealWorldUnits * focalLength) / width;
    }
}