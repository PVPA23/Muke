package frc.robot.subsystems;

import frc.robot.Constants;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class VisionSubsystem extends SubsystemBase {

  private final PhotonCamera camera = new PhotonCamera(Constants.Vision.kCameraName);

  private boolean hasTarget = false;
  private double yawDeg = 0.0;
  private double rangeMeters = 0.0;

  public VisionSubsystem() {}

  public boolean hasTarget() {
    return hasTarget;
  }

  public double getYawDeg() {
    return yawDeg;
  }

  public double getRangeMeters() {
    return rangeMeters;
  }

  @Override
  public void periodic() {
    PhotonPipelineResult result = camera.getLatestResult();

    hasTarget = result.hasTargets();
    if (hasTarget) {
      PhotonTrackedTarget best = result.getBestTarget();
      yawDeg = best.getYaw();

      // Estimativa simples de distância (depende do pipeline/cam)
      rangeMeters = best.getBestCameraToTarget().getTranslation().getNorm();

      SmartDashboard.putBoolean("Vision/HasTarget", true);
      SmartDashboard.putNumber("Vision/YawDeg", yawDeg);
      SmartDashboard.putNumber("Vision/RangeMeters", rangeMeters);
      SmartDashboard.putNumber("Vision/TargetId", best.getFiducialId());
    } else {
      yawDeg = 0.0;
      rangeMeters = 0.0;

      SmartDashboard.putBoolean("Vision/HasTarget", false);
    }
  }
}
