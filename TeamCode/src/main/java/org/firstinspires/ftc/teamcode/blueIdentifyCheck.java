package org.firstinspires.ftc.teamcode;
import static java.lang.Math.toRadians;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.sql.Array;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@Autonomous(preselectTeleOp = "mainTeleop")
public class blueIdentifyCheck extends LinearOpMode {
    OpenCvCamera camera;
    bluePipeline bluePipeline = new bluePipeline();
    @Override
    public void runOpMode() throws InterruptedException {
        //INIT
        DcMotorEx leftSlide = hardwareMap.get(DcMotorEx.class, "leftSlide");
        DcMotorEx rightSlide = hardwareMap.get(DcMotorEx.class, "rightSlide");
        Servo rightFlip = hardwareMap.servo.get("rightFlip");
        Servo leftFlip = hardwareMap.servo.get("leftFlip");
        Servo airplane = hardwareMap.servo.get("airplane");
        CRServo leftGrabber = hardwareMap.crservo.get("leftGrabber");
        CRServo rightGrabber = hardwareMap.crservo.get("rightGrabber");


        rightSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFlip.setDirection(Servo.Direction.REVERSE);

        leftSlide.setPower(0);
        rightSlide.setPower(0);

        leftSlide.setTargetPosition(0);
        rightSlide.setTargetPosition(0);
        airplane.setPosition(0);


        // Camera Init
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "mainCam"), cameraMonitorViewId);
        FtcDashboard.getInstance().startCameraStream(camera, 60);
        camera.setPipeline(bluePipeline); // Select pipeline
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
        String elementPos = bluePipeline.getPosition();


        waitForStart();



        //START
        while (opModeIsActive()) {

            elementPos = bluePipeline.getPosition();
            int leftPixels = bluePipeline.bluePixelsLeft;
            int midPixels = bluePipeline.bluePixelsMid;
            int rightPixels = bluePipeline.bluePixelsRight;
            telemetry.addData("Element Position: ", elementPos);
            telemetry.addData("Pixels: ", "%d, %d, %d", leftPixels, midPixels, rightPixels);
            telemetry.update();

        }
    }
}
