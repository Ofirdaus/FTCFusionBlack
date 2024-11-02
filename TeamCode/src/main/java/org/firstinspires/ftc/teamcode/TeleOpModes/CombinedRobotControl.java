//This shit better work on monday
//sooooo i kinda asked gpt to combine all my files into one.
//it also conveniently added comments for each one LET'S GOOOOOOO ;-;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="CombinedRobotControl", group="TeleOp")
public class CombinedRobotControl extends LinearOpMode {

    // Motors for arms
    private DcMotor leftArm;
    private DcMotor rightArm;

    // Motor for bucket
    private DcMotor bucketMotor;

    // Motors for linear slide
    private DcMotor motorSlide1;
    private DcMotor motorSlide2;

    // Motors for robot movement
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    // Bucket positions
    private static final double BUCKET_TIP_POSITION = 1.0;
    private static final double BUCKET_RESET_POSITION = 0.0;

    // Slide positions
    private static final double SLIDE_UP_POWER = 1.0;
    private static final double SLIDE_DOWN_POWER = -1.0;
    private static final int SLIDE_POSITION_UP = 1000;
    private static final int SLIDE_POSITION_DOWN = 0;

    @Override
    public void runOpMode() {
        // Initialize motors
        leftArm = hardwareMap.get(DcMotor.class, "left_arm");
        rightArm = hardwareMap.get(DcMotor.class, "right_arm");
        bucketMotor = hardwareMap.get(DcMotor.class, "bucket_motor");
        motorSlide1 = hardwareMap.get(DcMotor.class, "motorSlide1");
        motorSlide2 = hardwareMap.get(DcMotor.class, "motorSlide2");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Set motor modes
        leftArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bucketMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorSlide1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorSlide1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive())
        {
            // Robot movement control
            driveRobot();

            // Arm control
            controlArms();

            // Bucket control
            controlBucket();

            // Linear slide control (if needed)
            controlLinearSlide();
        }
    }

    private void driveRobot(){
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    private void controlArms(){
        double leftArmPower = -gamepad1.left_stick_y;
        double rightArmPower = -gamepad1.right_stick_y;

        leftArm.setPower(leftArmPower);
        rightArm.setPower(rightArmPower);

        telemetry.addData("Left Arm Power", leftArmPower);
        telemetry.addData("Right Arm Power", rightArmPower);
    }

    private void controlBucket(){

        if (gamepad1.a) {
            tipBucket();
        }
        else if (gamepad1.b){
            resetBucket();
        }
        else{
            bucketMotor.setPower(0);
        }

        telemetry.addData("Bucket Status", bucketMotor.getPower() == 0 ? "Stable" : "Moving");
    }

    private void tipBucket(){
        bucketMotor.setPower(BUCKET_TIP_POSITION);
        sleep(500);
        bucketMotor.setPower(0);
    }

    private void resetBucket(){
        bucketMotor.setPower(-BUCKET_TIP_POSITION);
        sleep(500);
        bucketMotor.setPower(0);
    }

    private void controlLinearSlide(){
        if (gamepad1.dpad_up) {
            moveSlide(SLIDE_POSITION_UP, SLIDE_UP_POWER);
        }
        else if(gamepad1.dpad_down) {
            moveSlide(SLIDE_POSITION_DOWN, SLIDE_DOWN_POWER);
        }
    }

    private void moveSlide(int targetPosition, double power){
        motorSlide1.setTargetPosition(targetPosition);
        motorSlide2.setTargetPosition(targetPosition);

        motorSlide1.setPower(power);
        motorSlide2.setPower(power);

        while (motorSlide1.isBusy() && motorSlide2.isBusy()){
            // Wait for the slide to reach the target position
        }

        motorSlide1.setPower(0);
        motorSlide2.setPower(0);
    }
}