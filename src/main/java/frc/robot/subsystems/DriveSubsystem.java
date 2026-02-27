package frc.robot.subsystems;

import frc.robot.Constants;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public class DriveSubsystem extends SubsystemBase {

  public enum DriveMode { NORMAL, SLOW, AGGRESSIVE }

  private final SparkMax leftLeader =
      new SparkMax(Constants.Drive.kLeftLeaderId, MotorType.kBrushed);
  private final SparkMax leftFollower =
      new SparkMax(Constants.Drive.kLeftFollowerId, MotorType.kBrushed);

  private final SparkMax rightLeader =
      new SparkMax(Constants.Drive.kRightLeaderId, MotorType.kBrushed);
  private final SparkMax rightFollower =
      new SparkMax(Constants.Drive.kRightFollowerId, MotorType.kBrushed);

  private final MotorControllerGroup leftGroup =
      new MotorControllerGroup(leftLeader, leftFollower);
  private final MotorControllerGroup rightGroup =
      new MotorControllerGroup(rightLeader, rightFollower);

  private final DifferentialDrive drive =
      new DifferentialDrive(leftGroup, rightGroup);

  // Limiters por modo (criamos e usamos o "ativo" para manter estado)
  private SlewRateLimiter forwardLimiter;
  private SlewRateLimiter turnLimiter;

  private DriveMode mode = DriveMode.NORMAL;

  public DriveSubsystem() {
    applyConfig(leftLeader, false);
    applyConfig(leftFollower, false);

    applyConfig(rightLeader, Constants.Drive.kInvertRightSide);
    applyConfig(rightFollower, Constants.Drive.kInvertRightSide);

    drive.setDeadband(Constants.Drive.kDeadband);
    drive.setMaxOutput(Constants.Drive.kMaxOutput);

    setMode(DriveMode.NORMAL);
  }

  public DriveMode getMode() {
    return mode;
  }

  public void setMode(DriveMode newMode) {
    mode = newMode;

    // Troca os limiters conforme modo (mantém comportamento previsível)
    switch (mode) {
      case SLOW:
        forwardLimiter = new SlewRateLimiter(Constants.Drive.kForwardSlewRateSlow);
        turnLimiter = new SlewRateLimiter(Constants.Drive.kTurnSlewRateSlow);
        break;
      case AGGRESSIVE:
        forwardLimiter = new SlewRateLimiter(Constants.Drive.kForwardSlewRateAggressive);
        turnLimiter = new SlewRateLimiter(Constants.Drive.kTurnSlewRateAggressive);
        break;
      default:
        forwardLimiter = new SlewRateLimiter(Constants.Drive.kForwardSlewRateNormal);
        turnLimiter = new SlewRateLimiter(Constants.Drive.kTurnSlewRateNormal);
        break;
    }
  }

  public void toggleSlowMode() {
    if (mode == DriveMode.SLOW) setMode(DriveMode.NORMAL);
    else setMode(DriveMode.SLOW);
  }

  public void toggleAggressiveMode() {
    if (mode == DriveMode.AGGRESSIVE) setMode(DriveMode.NORMAL);
    else setMode(DriveMode.AGGRESSIVE);
  }

  public void arcadeDrive(double forwardRaw, double turnRaw) {
    // Aplica slew conforme modo
    double forward = forwardLimiter.calculate(forwardRaw);
    double turn = turnLimiter.calculate(turnRaw);

    // Escalas por modo
    double scale;
    boolean squareInputs;

    switch (mode) {
      case SLOW:
        scale = Constants.Drive.kScaleSlow;
        squareInputs = true; // mais suave ainda perto do zero
        break;
      case AGGRESSIVE:
        scale = Constants.Drive.kScaleAggressive;
        squareInputs = false; // resposta mais direta/ágil
        break;
      default:
        scale = Constants.Drive.kScaleNormal;
        squareInputs = true;
        break;
    }

    forward *= scale;
    turn *= scale;

    drive.arcadeDrive(forward, turn, squareInputs);

    SmartDashboard.putString("Drive/Mode", mode.toString());
    SmartDashboard.putNumber("Drive/ForwardCmd", forward);
    SmartDashboard.putNumber("Drive/TurnCmd", turn);
  }

  public void stop() {
    drive.stopMotor();
  }

  private void applyConfig(SparkMax spark, boolean inverted) {
    SparkMaxConfig cfg = new SparkMaxConfig();
    cfg.idleMode(IdleMode.kBrake);
    cfg.inverted(inverted);
    cfg.secondaryCurrentLimit(60);
    spark.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }
}
