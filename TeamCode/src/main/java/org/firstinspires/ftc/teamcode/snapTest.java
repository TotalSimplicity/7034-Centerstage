package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.ArrayList;
import java.util.List;

@TeleOp
public class snapTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        Pose2d curPos = new Pose2d(12, -64, Math.toRadians(90));
        drive.setPoseEstimate(curPos);

        waitForStart();

        while (opModeIsActive()) {
            double robot_x = drive.getPoseEstimate().getX();
            double robot_y = drive.getPoseEstimate().getY();

            // Define the x and y coordinate values
            int[] x_values = {-36, -24, -12, 12, 24, 36};
            int[] y_values = {-36, -24, -12, 12, 24, 36};

            // Initialize a list to store all mat centers
            List<double[]> mat_centers = new ArrayList<>();

            // Generate mat centers based on the grid pattern
            for (int x : x_values) {
                for (int y : y_values) {
                    double[] center = {x, y};
                    mat_centers.add(center);
                }
            }

            // Find the closest mat center
            double closest_distance = Double.POSITIVE_INFINITY;
            double[] closest_mat_center = null;

            for (double[] mat_center : mat_centers) {
                double dist = distance(new double[]{robot_x, robot_y}, mat_center);
                if (dist < closest_distance) {
                    closest_distance = dist;
                    closest_mat_center = mat_center;
                }
            }

            // Print the closest mat center

            if (gamepad1.a && closest_mat_center != null) {
                Vector2d nearC = new Vector2d(closest_mat_center[0], closest_mat_center[1]);
                Trajectory traj1 = drive.trajectoryBuilder(curPos)
                        //.splineTo(nearC, Math.toRadians(180))
                        .lineTo(nearC)
                        .build();

                drive.followTrajectoryAsync(traj1);
            }

            drive.update();
            curPos = drive.getPoseEstimate();

            telemetry.addData("Nearest Center: ", String.valueOf(closest_mat_center[0]), closest_mat_center[1]);
            telemetry.update();
        }
    }

    public static double distance(double[] point1, double[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }
}
