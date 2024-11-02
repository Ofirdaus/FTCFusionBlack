package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class LinearSlideOpMode extends LinearOpMode {

    private DcMotor motorSlide1;
    private DcMotor motorSlide2;

    private static final double SLIDE_UP_POWER = 0.5;  // Adjusted power to move up
    private static final double SLIDE_DOWN_POWER = -0.5; // Adjusted power to move down
    private static final int SLIDE_POSITION_UP = 1000; // Target position for up
    private static final int SLIDE_POSITION_DOWN = 0;   // Target position for down

    @Override
    public void runOpMode() {
        // Motor initialization with error handling
        try {
            motorSlide1 = hardwareMap.get(DcMotor.class, "motorSlide1");
            motorSlide2 = hardwareMap.get(DcMotor.class, "motorSlide2");
        } catch (Exception e) {
            telemetry.addData("Error", "Motor not found: " + e.getMessage());
            telemetry.update();
            sleep(2000);
            stop(); // Stop the op mode
        }

        // Reset encoders
        motorSlide1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set motor direction if needed (optional)
        // motorSlide1.setDirection(DcMotor.Direction.FORWARD);
        // motorSlide2.setDirection(DcMotor.Direction.REVERSE); // If you need motorSlide2 to be reverse

        motorSlide1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        // Move slide up
        moveSlide(SLIDE_POSITION_UP, SLIDE_UP_POWER);

        // Move slide down
        moveSlide(SLIDE_POSITION_DOWN, SLIDE_DOWN_POWER);
    }

    private void moveSlide(int targetPosition, double power) {
        motorSlide1.setTargetPosition(targetPosition);
        motorSlide2.setTargetPosition(targetPosition);

        // Set to RUN_TO_POSITION mode
        motorSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorSlide1.setPower(power);
        motorSlide2.setPower(power);

        while (motorSlide1.isBusy() || motorSlide2.isBusy()) {
            // Telemetry for debugging
            telemetry.addData("Target Position", targetPosition);
            telemetry.addData("Motor Slide 1 Position", motorSlide1.getCurrentPosition());
            telemetry.addData("Motor Slide 2 Position", motorSlide2.getCurrentPosition());
            telemetry.update();
        }

        // Stop the motors
        motorSlide1.setPower(0);
        motorSlide2.setPower(0);

        // Optionally reset to RUN_USING_ENCODER
        motorSlide1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}