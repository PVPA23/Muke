package frc.robot.subsystems;

import frc.robot.Constants;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

public class IntakeSubsystem extends SubsystemBase {

  public enum NeoState { STOP, FORWARD, REVERSE }

  // CAN 5 = CIM (Brushed) - sempre ligado no máximo em TELEOP ENABLED
  private final SparkMax motorAlwaysOn =
      new SparkMax(Constants.Intake.kMotorAlwaysOnId, MotorType.kBrushed);

  // CAN 6 = NEO (Brushless) - toggle por A/Y
  private final SparkMax neoMotor =
      new SparkMax(Constants.Intake.kNeoMotorId, MotorType.kBrushless);

  private NeoState neoState = NeoState.STOP;

  public IntakeSubsystem() {
    applyConfigAlwaysOn();
    applyConfigNeo();

    motorAlwaysOn.set(0.0);
    neoMotor.set(0.0);
  }

  // ============================
  // Config CAN 5 (CIM)
  // ============================
  private void applyConfigAlwaysOn() {
    SparkMaxConfig cfg = new SparkMaxConfig();

    // Corrente (se sua REVLib suportar)
    cfg.secondaryCurrentLimit(Constants.Intake.kAlwaysOnCurrentLimit);

    cfg.idleMode(Constants.Intake.kUseCoastOnAlwaysOn ? IdleMode.kCoast : IdleMode.kBrake);
    cfg.inverted(false);

    motorAlwaysOn.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  // ============================
  // Config CAN 6 (NEO) + desabilitar limit switches
  // ============================
  private void applyConfigNeo() {
    SparkMaxConfig cfg = new SparkMaxConfig();

    cfg.secondaryCurrentLimit(Constants.Intake.kNeoCurrentLimit);
    cfg.idleMode(IdleMode.kBrake);
    cfg.inverted(false);

    // ✅ Isso evita o travamento que você tinha no REV Client:
    // Hard Forward/Reverse Limit ENABLED sem limit switch conectado.
    cfg.limitSwitch.forwardLimitSwitchEnabled(false);
    cfg.limitSwitch.reverseLimitSwitchEnabled(false);

    neoMotor.configure(cfg, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  // ============================
  // CAN 6 (NEO) - TOGGLE/LATCH
  // ============================
  public void toggleNeoForward() {
    if (neoState == NeoState.FORWARD) {
      setNeoState(NeoState.STOP);
    } else {
      setNeoState(NeoState.FORWARD);
    }
  }

  public void toggleNeoReverse() {
    if (neoState == NeoState.REVERSE) {
      setNeoState(NeoState.STOP);
    } else {
      setNeoState(NeoState.REVERSE);
    }
  }

  private void setNeoState(NeoState newState) {
    neoState = newState;
    applyNeoOutput();
  }

  private void applyNeoOutput() {
    switch (neoState) {
      case FORWARD:
        neoMotor.set(+Constants.Intake.kNeoMaxSpeed);
        break;
      case REVERSE:
        neoMotor.set(-Constants.Intake.kNeoMaxSpeed);
        break;
      default:
        neoMotor.set(0.0);
        break;
    }
  }

  public NeoState getNeoState() {
    return neoState;
  }

  // Opcional: parar manualmente se quiser
  public void stopNeo() {
    setNeoState(NeoState.STOP);
  }

  @Override
  public void periodic() {
    // ============================
    // CAN 5 — sempre ligado no máximo EM TELEOP ENABLED
    // ============================
    double can5Cmd = DriverStation.isTeleopEnabled() ? Constants.Intake.kAlwaysOnSpeed : 0.0;
    motorAlwaysOn.set(can5Cmd);

    // ============================
    // Segurança: se desabilitar, zera NEO e reseta estado
    // ============================
    if (!DriverStation.isEnabled() && neoState != NeoState.STOP) {
      neoState = NeoState.STOP;
      neoMotor.set(0.0);
    }

    // Telemetria útil
    SmartDashboard.putNumber("Intake/CAN5_Cmd", can5Cmd);
    SmartDashboard.putString("Intake/NEO_State", neoState.toString());
  }
}
