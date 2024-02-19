package org.firstinspires.ftc.teamcode;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.sql.Array;

@TeleOp
public class mainTeleop extends LinearOpMode {

    public void runOpMode() throws InterruptedException {
        // Initializing devices
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        DcMotorEx leftSlide = hardwareMap.get(DcMotorEx.class, "leftSlide");
        DcMotorEx rightSlide = hardwareMap.get(DcMotorEx.class, "rightSlide");
        Servo rightFlip = hardwareMap.servo.get("rightFlip");
        Servo leftFlip = hardwareMap.servo.get("leftFlip");
        Servo airplane = hardwareMap.servo.get("airplane2");
        CRServo leftGrabber = hardwareMap.crservo.get("leftGrabber");
        CRServo rightGrabber = hardwareMap.crservo.get("rightGrabber");
        Servo leftSweep = hardwareMap.servo.get("leftSweep");
        Servo rightSweep = hardwareMap.servo.get("rightSweep");



        // Airplane timer
        ElapsedTime airplaneReset = new ElapsedTime();
        // Setting motor directions.
        rightSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFlip.setDirection(Servo.Direction.REVERSE);
        rightGrabber.setDirection(CRServo.Direction.REVERSE);
        leftSweep.setDirection(Servo.Direction.REVERSE);

        // Settings slides to 0 power on start
        leftSlide.setPower(0);
        rightSlide.setPower(0);

        // Servo starting positions
        leftSlide.setTargetPosition(0);
        rightSlide.setTargetPosition(0);
        airplane.setPosition(0);
        leftSweep.setPosition(0);
        rightSweep.setPosition(0);
        //leftFlip.setPosition(0);
        //rightFlip.setPosition(0);

        // Variables
        boolean launched = false;
        boolean controlFlip = false;
        boolean sweeped = true;

        int constantPower = 0;
        double drivingSpeed = 1;

        double[] flipLoc = {0, 0.25, 1};

        // Slide positions(1-5)
        int[] rightSlideTicks = {0, 487, 1946, 3271};
        int[] leftSlideTicks = {0, 500, 1963, 3304};
        //zeroed, first line, second line, third line, top
        int slideLocation = 0;
        int flipPos = 0;



        leftSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        waitForStart();



        while (opModeIsActive()) {
            // Save gamepad status from previous loop
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            // Driving code:
            if (!controlFlip){
                drive.setWeightedDrivePower(
                        new Pose2d(
                                -gamepad1.left_stick_y,
                                -gamepad1.left_stick_x,
                                -gamepad1.right_stick_x
                        )
                );
            } else {
                drive.setWeightedDrivePower(
                        new Pose2d(
                                gamepad1.left_stick_y,
                                gamepad1.left_stick_x,
                                -gamepad1.right_stick_x
                        )
                );
            }


            // Other controls:

            if (gamepad2.a && !previousGamepad2.a || gamepad1.b && !previousGamepad1.b){
                if (sweeped){
                    leftSweep.setPosition(0.6);
                    rightSweep.setPosition(0.6);
                    sweeped = false;
                } else {
                    leftSweep.setPosition(0.1);
                    rightSweep.setPosition(0.1);
                    sweeped = true;
                }

            }
            if (gamepad2.right_stick_button && !previousGamepad2.right_stick_button){
                if(rightSweep.getPosition() == 0.6) {
                    rightSweep.setPosition(0.1);
                } else {
                    rightSweep.setPosition(0.6);
                }
            }
            if (gamepad2.left_stick_button && !previousGamepad2.left_stick_button){
                if(leftSweep.getPosition() == 0.6) {
                    leftSweep.setPosition(0.1);
                } else {
                    leftSweep.setPosition(0.6);
                }
            }
            // Slide position changing
            if ((gamepad2.dpad_up && !previousGamepad2.dpad_up && flipPos != 0 )|| (gamepad1.dpad_up && !previousGamepad1.dpad_up && flipPos != 0)) {
                constantPower = 1;
                if (slideLocation < 3) {
                    slideLocation++;

                }
            } if ((gamepad2.dpad_down && !previousGamepad2.dpad_down && flipPos !=0) || (gamepad1.dpad_down && !previousGamepad1.dpad_up && flipPos !=0)){
                constantPower = 1;
                if (slideLocation > 0){
                    slideLocation=slideLocation-1;
                }
            }
            // Flip drive controls
            if (gamepad1.y && !previousGamepad1.y){
                controlFlip = !controlFlip;
                if (controlFlip){
                    drivingSpeed = 0.5;
                } else if (!controlFlip){
                    drivingSpeed = 1;
                }
            }
            // Auto-lower grabber if controls flipped
            // (gamepad1. && !previousGamepad1.)

            // Firing airplane
            if (gamepad1.left_trigger >= 0.1 && gamepad1.left_bumper) {
                airplane.setPosition(1);
                airplaneReset.reset();
                launched = true;
            }
            if (launched && (airplaneReset.seconds() >= 5.0)){
                airplane.setPosition(0);
            }

            // Closing/opening grabber
            if (gamepad1.right_trigger > 0.1 || gamepad1.right_bumper || gamepad2.left_trigger > 0.1 || gamepad2.right_trigger > 0.1 || gamepad2.left_bumper || gamepad2.right_bumper) {
                if (gamepad1.left_trigger >= 0.1) {
                    leftGrabber.setDirection(CRServo.Direction.REVERSE);
                    rightGrabber.setDirection(CRServo.Direction.FORWARD);
                    leftGrabber.setPower(1);
                    rightGrabber.setPower(1);
                } if (gamepad1.left_bumper) {
                    rightGrabber.setDirection(CRServo.Direction.REVERSE);
                    leftGrabber.setDirection(CRServo.Direction.FORWARD);
                    leftGrabber.setPower(1);
                    rightGrabber.setPower(1);

                }
                if (gamepad2.right_trigger > 0.1) {
                    leftGrabber.setDirection(CRServo.Direction.REVERSE);
                    leftGrabber.setPower(1);
                }
                if (gamepad2.right_bumper) {
                    leftGrabber.setDirection(CRServo.Direction.FORWARD);
                    leftGrabber.setPower(1);
                } if (gamepad2.left_trigger > 0.1) {
                    rightGrabber.setDirection(CRServo.Direction.FORWARD);
                    rightGrabber.setPower(1);
                } if (gamepad2.left_bumper) {
                    rightGrabber.setDirection(CRServo.Direction.REVERSE);
                    rightGrabber.setPower(1);
                }
            } else {
                leftGrabber.setPower(0);
                rightGrabber.setPower(0);
            }

            if ((gamepad2.b && !previousGamepad2.b) || (gamepad1.a && !previousGamepad1.a)){
                leftSweep.setPosition(1);
                rightSweep.setPosition(1);
            }



            // Flip lifting mechanism
            if (gamepad2.dpad_right && !previousGamepad2.dpad_right && flipPos < 2 || gamepad1.dpad_right && !previousGamepad1.dpad_right && flipPos < 2) {
                flipPos++;
            }
            if (gamepad2.dpad_left && !previousGamepad2.dpad_left && flipPos > 0 || gamepad1.dpad_left && !previousGamepad1.dpad_left && flipPos > 0){
                if (flipPos==1){
                    if(slideLocation==0){
                        flipPos--;
                    }
                } else {
                    flipPos--;
                }

            }

            // X to max out slides, b to zero them out
            if (gamepad2.x && !previousGamepad2.x && flipPos != 0){
                if (slideLocation == 0){
                    slideLocation = 2;
                } else {
                    slideLocation = 0;
                }

                constantPower = 1;
            }


            // Y to kill power to slides
            if (gamepad2.y && !previousGamepad2.y){
                if (constantPower == 1){
                    constantPower = 0;
                } else {
                    constantPower = 1;
                }

            }
            // Set slide positions and power when changed
            rightSlide.setTargetPosition(rightSlideTicks[slideLocation]);
            leftSlide.setTargetPosition(leftSlideTicks[slideLocation]);
            rightSlide.setPower(constantPower);
            leftSlide.setPower(constantPower);
            leftFlip.setPosition(flipLoc[flipPos]);
            rightFlip.setPosition(flipLoc[flipPos]);

            drive.updatePoseEstimate();

            //telemetry.addData("X position: ", drive.pose.position.x);
            //telemetry.addData("Y position: ", drive.pose.position.y);
            //telemetry.addData("Heading: ", drive.pose.heading);
            telemetry.addData("Slide ticks: ", "Left=%d Right=%d Power=%d", leftSlide.getCurrentPosition(), rightSlide.getCurrentPosition(), constantPower);
            telemetry.addData("Slide location: ", slideLocation);
            //telemetry.addData("Flip location: ", flipPos);

            telemetry.update();

        }}}
