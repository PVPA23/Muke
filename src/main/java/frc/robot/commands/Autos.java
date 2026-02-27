package frc.robot.commands;

import java.util.Set;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import frc.robot.Constants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.DriveSubsystem.DriveMode;
import frc.robot.subsystems.VisionSubsystem;

public final class Autos {

  private Autos() {}

  /** Autônomo mínimo só pra validar deploy + motores. */
  public static Command simpleDriveForward(DriveSubsystem drive) {
    return Commands.sequence(
            // Garante modo previsível no auton
            Commands.runOnce(() -> drive.setMode(DriveMode.NORMAL)),

            // Anda reto por 1.2s
            Commands.run(() -> drive.arcadeDrive(0.35, 0.0), drive).withTimeout(1.2),

            // Para
            Commands.runOnce(drive::stop))
        .finallyDo(interrupted -> drive.stop());
  }


  public static Command tagReactiveTimedMoves(DriveSubsystem drive, VisionSubsystem vision) {
    final double pwr = 0.60;

    // PhotonVision direto
    final PhotonCamera camera = new PhotonCamera(Constants.Vision.kCameraName);

    Command forward3s =
        Commands.run(() -> drive.arcadeDrive(pwr, 0.0), drive).withTimeout(3.0);

    Command backward3s =
        Commands.run(() -> drive.arcadeDrive(-pwr, 0.0), drive).withTimeout(3.0);

    // Em DifferentialDrive, turn + costuma girar para a direita (horário visto de cima).
    Command spinClockwise8s =
        Commands.run(() -> drive.arcadeDrive(0.0, pwr), drive).withTimeout(8.0);

    // Espera até aparecer uma tag 2/3/4
    Command waitForTag234 =
        new WaitUntilCommand(
            () -> {
              PhotonPipelineResult result = camera.getLatestResult();
              if (!result.hasTargets()) return false;

              PhotonTrackedTarget t = result.getBestTarget();
              if (t == null) return false;

              int id = t.getFiducialId();
              return id == 2 || id == 3 || id == 4;
            });

    // Escolhe o movimento conforme a tag (avaliado na hora, depois que a tag apareceu)
    Command chooseAndRun =
        Commands.defer(
            () -> {
              PhotonPipelineResult result = camera.getLatestResult();
              if (!result.hasTargets()) return Commands.none();

              PhotonTrackedTarget t = result.getBestTarget();
              if (t == null) return Commands.none();

              int id = t.getFiducialId();
              if (id == 2) return forward3s;
              if (id == 3) return backward3s;
              if (id == 4) return spinClockwise8s;

              return Commands.none();
            },
            Set.of(drive));

    return Commands.sequence(
            // Garante modo previsível no auton
            Commands.runOnce(() -> drive.setMode(DriveMode.NORMAL)),

            // Espera tag e executa
            waitForTag234,
            chooseAndRun,

            // Para ao final
            Commands.runOnce(drive::stop))
        .finallyDo(interrupted -> drive.stop());
  }
}
