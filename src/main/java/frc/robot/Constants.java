package frc.robot;

public final class Constants {

  private Constants() {}

  public static final class Drive {
    // CAN IDs (Tração - CIM Brushed)
    public static final int kLeftLeaderId = 1;
    public static final int kLeftFollowerId = 2;
    public static final int kRightLeaderId = 3;
    public static final int kRightFollowerId = 4;

    // Direção
    public static final boolean kInvertRightSide = true;

    // Drive base
    public static final double kDeadband = 0.05;
    public static final double kMaxOutput = 1.0;

    // Slew rates por modo (quanto maior, mais "solto")
    public static final double kForwardSlewRateNormal = 3.0;
    public static final double kTurnSlewRateNormal = 3.0;

    public static final double kForwardSlewRateSlow = 2.0;
    public static final double kTurnSlewRateSlow = 2.0;

    // Agressivo: praticamente sem filtro (bem ágil)
    public static final double kForwardSlewRateAggressive = 100.0;
    public static final double kTurnSlewRateAggressive = 100.0;

    // Escalas por modo
    public static final double kScaleNormal = 1.0;
    public static final double kScaleSlow = 0.5;
    public static final double kScaleAggressive = 1.0;
  }

  public static final class Intake {
    // CAN IDs
    public static final int kMotorAlwaysOnId = 5; // CIM (Brushed)
    public static final int kNeoMotorId = 6;      // NEO (Brushless)

    // CAN 5: sempre ligado no máximo em TELEOP ENABLED
    // Se o sentido estiver invertido, troque o sinal.
    public static final double kAlwaysOnSpeed = -1;

    // CAN 6: máximo
    public static final double kNeoMaxSpeed = 1;

    // Corrente (se sua REVLib realmente aplicar esse tipo de limit)
    public static final int kAlwaysOnCurrentLimit = 80;
    public static final int kNeoCurrentLimit = 60;

    public static final boolean kUseCoastOnAlwaysOn = false; // false = Brake
  }

  public static final class OI {
    public static final int kDriverPort = 0;

    // Eixos (ajuste se necessário)
    public static final int kForwardAxis = 1; // Left Stick Y
    public static final int kTurnAxis = 4;    // Right Stick X
    public static final double kJoystickDeadband = 0.08;

    // Botões do Xbox/F310 (XInput)
    public static final int kNeoForwardToggleButton = 1; // A
    public static final int kNeoReverseToggleButton = 4; // Y

    public static final int kSlowModeToggleButton = 5;   // LB
    public static final int kAggressiveToggleAxis = 2;   // LT (gatilho esquerdo)
    public static final double kTriggerPressedThreshold = 0.6;
  }

  public static final class Vision {
    public static final String kCameraName = "USB_Camera";
    public static final int kTargetTagId = 1;

    public static final double kTurnKp = 0.020;
    public static final double kForwardKp = 0.45;

    public static final double kMaxTurn = 0.45;
    public static final double kMaxForward = 0.40;

    public static final double kDesiredRangeMeters = 1.20;

    public static final double kYawToleranceDeg = 2.0;
    public static final double kRangeToleranceMeters = 0.12;

    public static final double kSearchTurn = 0.22;
  }
}
