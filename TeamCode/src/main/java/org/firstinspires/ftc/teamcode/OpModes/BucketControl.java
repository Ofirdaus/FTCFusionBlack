import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class BucketControl extends LinearOpMode {
    //bucket motor
    private DcMotor bucketMotor;

    //bucket positions
    private static final double BUCKET_TIP_POSITION = 1.0; // Adjust based on your mechanism
    private static final double BUCKET_RESET_POSITION = 0.0; // Initial position

    @Override
    public void runOpMode() {
        //bucket motor intitialized
        bucketMotor = hardwareMap.get(DcMotor.class, "bucket_motor");

        //setting the motor to run without encoders for continuous operation
        //wow that made me sound so cool lmao (jkjk)
        bucketMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //wait for the start button to be pressed
        waitForStart();

        while (opModeIsActive()) {
            //checking the gamepad inputs for bucket control
            if (gamepad1.a) {
                //tipping the bucket
                tipBucket();
            }
            else if (gamepad1.b) {
                //resetting the bucket
                resetBucket();
            } else {
                //stopping the bucket to maintain position
                bucketMotor.setPower(0);
            }

            //again, optional, but adding telemetry for debugging
            telemetry.addData("Bucket Status", bucketMotor.getPower() == 0 ? "Stable" : "Moving");
            telemetry.update();
        }
    }

    private void tipBucket() {
        //setting motor power to tip the bucket
        bucketMotor.setPower(BUCKET_TIP_POSITION);
        sleep(500); //time can be adjusted based on our mechanism
        bucketMotor.setPower(0); //stopping them motor to maintain its position
    }

    private void resetBucket() {
        //setting motor power to reset the bucket
        bucketMotor.setPower(-BUCKET_TIP_POSITION);
        sleep(500); //time can be adjusted based on our mechanism
        bucketMotor.setPower(0); //stopping them motor to maintain its position
    }
}