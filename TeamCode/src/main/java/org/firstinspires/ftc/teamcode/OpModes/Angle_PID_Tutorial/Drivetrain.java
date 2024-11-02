//libraries
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="RobotMovementControls", group="TeleOp")
public class RobotMovementControls extends OpMode {

    //motor names 😭
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    @Override
    public void init() {
        // Initialize motors using hardware mapping (based on a logitech controller lmao)
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        // Set the right side motors to reverse direction (copied this, forgot link)
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        //getting joystick values for driving controls
        double drive = -gamepad1.left_stick_y;  // Forward and backward movement
        double strafe = gamepad1.left_stick_x;  // Strafing (left-right movement)
        double turn = gamepad1.right_stick_x;   // Turning (rotation in place)

        //power for each motor (these are the calculations)
        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        //setting power to motors
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}