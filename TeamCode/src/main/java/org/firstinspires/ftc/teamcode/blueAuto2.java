package org.firstinspires.ftc.teamcode;
import static java.lang.Math.toRadians;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
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
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.sql.Array;
import java.util.Objects;
import java.util.Vector;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Config
@Autonomous(preselectTeleOp = "mainTeleop", name = "Blue Far side")
public class blueAuto2 extends LinearOpMode {
    OpenCvCamera camera;
    redPipeline redPipeline = new redPipeline();
    String elementPos;
    public static int actLocation = 0;

    private SampleMecanumDrive drive = null;
    private DcMotorEx leftSlide = null;
    private DcMotorEx rightSlide = null;
    private Servo rightFlip = null;
    private Servo leftFlip = null;
    private Servo airplane = null;
    private CRServo leftGrabber = null;
    private CRServo rightGrabber = null;
    private Servo leftSweep = null;
    private Servo rightSweep = null;

    // RR/FTC Dash config vals:

    @Override
    public void runOpMode() throws InterruptedException {
        //INIT
        drive = new SampleMecanumDrive(hardwareMap);
        Pose2d curPos = new Pose2d(12, -64, Math.toRadians(90));
        drive.setPoseEstimate(curPos);

        leftSlide = hardwareMap.get(DcMotorEx.class, "leftSlide");
        rightSlide = hardwareMap.get(DcMotorEx.class, "rightSlide");
        rightFlip = hardwareMap.servo.get("rightFlip");
        leftFlip = hardwareMap.servo.get("leftFlip");
        airplane = hardwareMap.servo.get("airplane");
        leftGrabber = hardwareMap.crservo.get("leftGrabber");
        rightGrabber = hardwareMap.crservo.get("rightGrabber");
        leftSweep = hardwareMap.servo.get("leftSweep");
        rightSweep = hardwareMap.servo.get("rightSweep");


        rightSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFlip.setDirection(Servo.Direction.REVERSE);
        rightGrabber.setDirection(CRServo.Direction.REVERSE);
        leftSweep.setDirection(Servo.Direction.REVERSE);

        leftSlide.setPower(0);
        rightSlide.setPower(0);
        leftSweep.setPosition(0);
        rightSweep.setPosition(0);
        leftSlide.setTargetPosition(0);
        rightSlide.setTargetPosition(0);
        airplane.setPosition(0);

        TrajectorySequence middleSpike = drive.trajectorySequenceBuilder(curPos)
                .forward(27.5)
                .turn(Math.toRadians(180))
                .build();
        TrajectorySequence middleBoard = drive.trajectorySequenceBuilder(curPos)
                .forward(10)
                .turn(Math.toRadians(90))
                .forward(35)
                .strafeLeft(12.75)
                .build();
        TrajectorySequence rightSpike = drive.trajectorySequenceBuilder(curPos)
                .strafeRight(22)
                .forward(28.5)
                .turn(Math.toRadians(-90))
                .build();
        TrajectorySequence leftBoard = drive.trajectorySequenceBuilder(curPos)
                .forward(20)
                .strafeRight(2.2)
                .forward(15)
                .build();
        TrajectorySequence leftSpike = drive.trajectorySequenceBuilder(curPos)
                .forward(36)
                .turn(Math.toRadians(-90))
                .build();
        TrajectorySequence rightBoard = drive.trajectorySequenceBuilder(curPos)
                .forward(10)
                .strafeRight(20)

                .build();
        TrajectorySequence forwardBit = drive.trajectorySequenceBuilder(curPos)
                .forward(12)
                .build();
        TrajectorySequence backBit = drive.trajectorySequenceBuilder(curPos)
                .back(2)
                .build();
        TrajectorySequence returnStart = drive.trajectorySequenceBuilder(curPos)
                .splineTo(new Vector2d(12, -63), Math.toRadians(90))
                .build();
        TrajectorySequence parkLeft = drive.trajectorySequenceBuilder(curPos)
                .back(3)
                .strafeLeft(28)
                .forward(20)
                .build();





        // Camera Init
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "mainCam"), cameraMonitorViewId);
        FtcDashboard.getInstance().startCameraStream(camera, 60);
        camera.setPipeline(redPipeline); // Select pipeline
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });



        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // RR Actions to push pixel to spike
        waitForStart();
        rightFlip.setPosition(0.75);
        leftFlip.setPosition(0.75);
        if (actLocation == 0){
            elementPos = bluePipeline.getPosition();
        }  else{
            if(actLocation == 1){
                elementPos = "left";
            }
            else if(actLocation == 2){
                elementPos = "mid";
            }
            else if(actLocation == 3){
                elementPos = "right";
            }
        }
        if (Objects.equals(elementPos, "right")){
            ElapsedTime flipTime = new ElapsedTime();
            raiseFlip();
            drive.followTrajectorySequence(rightSpike);
            lowerFlip();
            rightSweep.setPosition(0.15);
            leftFlip.setPosition(0.15);
            rightFlip.setPosition(0.15);
            leftOuttake();
            openSweep();
        } else if (Objects.equals(elementPos, "mid")){
            ElapsedTime flipTime = new ElapsedTime();
            raiseFlip();
            drive.followTrajectorySequence(middleSpike);
            openSweep();
            lowerFlip();
            rightSweep.setPosition(0.15);
            leftFlip.setPosition(0.15);
            rightFlip.setPosition(0.15);
            leftOuttake();
            openSweep();
        } else if (Objects.equals(elementPos, "left")) {
            ElapsedTime flipTime = new ElapsedTime();
            raiseFlip();
            drive.followTrajectorySequence(leftSpike);
            openSweep();
            lowerFlip();
            rightSweep.setPosition(0.15);
            leftFlip.setPosition(0.15);
            rightFlip.setPosition(0.15);
            leftOuttake();
            openSweep();

        }

        while (opModeIsActive()) {

            elementPos = redPipeline.getPosition();
            int leftPixels = redPipeline.redPixelsLeft;
            int midPixels = redPipeline.redPixelsMid;
            int rightPixels = redPipeline.redPixelsRight;
            telemetry.addData("Element Position: ", elementPos);
            telemetry.addData("Pixels: ", "%d, %d, %d", leftPixels, midPixels, rightPixels);
            telemetry.update();

        }
    }
    public void leftIntake(){
        ElapsedTime leftIntakeTime = new ElapsedTime();
        leftIntakeTime.reset();
        while (leftIntakeTime.seconds() < 3){
            leftGrabber.setDirection(CRServo.Direction.REVERSE);
            leftGrabber.setPower(1);
        }
        leftGrabber.setPower(0);

    }
    public void rightIntake(){
        ElapsedTime rightIntakeTime = new ElapsedTime();
        rightIntakeTime.reset();
        while (rightIntakeTime.seconds() < 3){
            rightGrabber.setDirection(CRServo.Direction.FORWARD);
            rightGrabber.setPower(1);
        }
        rightGrabber.setPower(0);

    }
    public void leftOuttake(){
        ElapsedTime leftOuttakeTime = new ElapsedTime();
        leftOuttakeTime.reset();
        while (leftOuttakeTime.seconds() < 3){
            leftGrabber.setDirection(CRServo.Direction.FORWARD);
            leftGrabber.setPower(0.3);
        }
        leftGrabber.setPower(0);

    }
    public void rightOuttake(){
        ElapsedTime rightOuttakeTime = new ElapsedTime();
        rightOuttakeTime.reset();
        while (rightOuttakeTime.seconds() < 3){
            rightGrabber.setDirection(CRServo.Direction.REVERSE);
            rightGrabber.setPower(0.125);
        }
        rightGrabber.setPower(0);

    }
    public void rightOuttakeTog(){
        rightGrabber.setDirection(CRServo.Direction.REVERSE);
        if(rightGrabber.getPower() != 0){
            rightGrabber.setPower(1);
        } else {
            rightGrabber.setPower(0);
        }
    }
    public void leftOuttakeTog(){
        leftGrabber.setDirection(CRServo.Direction.FORWARD);
        if(leftGrabber.getPower() != 0){
            leftGrabber.setPower(1);
        } else {
            leftGrabber.setPower(0);
        }
    }

    public void rightIntakeTog(){
        rightGrabber.setDirection(CRServo.Direction.FORWARD);
        rightGrabber.setPower(1);
    }
    public void leftIntakeTog(){
        leftGrabber.setDirection(CRServo.Direction.FORWARD);
        if(leftGrabber.getPower() != 0){
            leftGrabber.setPower(1);
        } else {
            leftGrabber.setPower(0);
        }
    }

    public void closeSweep(){
        leftSweep.setPosition(0.1);
        rightSweep.setPosition(0.1);
    }
    public void openSweep(){
        leftSweep.setPosition(0.6);
        rightSweep.setPosition(0.6);
    }
    public void lowerFlip() {
        leftFlip.setPosition(0);
        rightFlip.setPosition(0);
    }
    public  void raiseFlip(){
        leftFlip.setPosition(0.25);
        rightFlip.setPosition(0.25);
    }
    public void flip(){
        double servoPos = leftFlip.getPosition();
        while (servoPos < 1){
            servoPos = servoPos + 0.01;
            leftFlip.setPosition(servoPos);
            rightFlip.setPosition(servoPos);
        }
        leftFlip.setPosition(1);
        rightFlip.setPosition(1);
    }

}
