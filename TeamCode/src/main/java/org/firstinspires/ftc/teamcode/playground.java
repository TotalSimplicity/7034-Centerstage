package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@TeleOp
public class playground extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Pose2d curPos = new Pose2d(12, -64, Math.toRadians(90));
        drive.setPoseEstimate(curPos);
        TrajectorySequence myTrajectory = drive.trajectorySequenceBuilder(curPos)
                .forward(30)
                .turn(Math.toRadians(90))
                .turn(Math.toRadians(270))
                .strafeRight(4)
                .forward(40)
                .build();
        TrajectorySequence myTrajectorya = drive.trajectorySequenceBuilder(curPos)
                .splineToConstantHeading(new Vector2d(24, 0), 0)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(myTrajectory);
        //drive.followTrajectorySequence(myTrajectorya);
    }
}