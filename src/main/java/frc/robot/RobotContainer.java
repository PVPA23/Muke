package frc.robot;

import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.VisionSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {

  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  private final XboxController driverController =
      new XboxController(Constants.OI.kDriverPort);

  public RobotContainer() {
    configureDefaultCommands();
    configureBindings();
  }

  private void configureDefaultCommands() {
    driveSubsystem.setDefaultCommand(
        new RunCommand(
            () -> {
              double forward =
                  -MathUtil.applyDeadband(
                      driverController.getRawAxis(Constants.OI.kForwardAxis),
                      Constants.OI.kJoystickDeadband);

              double turn =
                  MathUtil.applyDeadband(
                      driverController.getRawAxis(Constants.OI.kTurnAxis),
                      Constants.OI.kJoystickDeadband);

              driveSubsystem.arcadeDrive(forward, turn);
            },
            driveSubsystem));
  }

  private void configureBindings() {

    // ============================
    // CAN 6 (NEO): TOGGLE/LATCH
    // ============================
    new JoystickButton(driverController, Constants.OI.kNeoForwardToggleButton)
        .onTrue(Commands.runOnce(intakeSubsystem::toggleNeoForward, intakeSubsystem));

    new JoystickButton(driverController, Constants.OI.kNeoReverseToggleButton)
        .onTrue(Commands.runOnce(intakeSubsystem::toggleNeoReverse, intakeSubsystem));

    // ============================
    // Drive: 3 modos
    // - NORMAL (inicia assim)
    // - SLOW: toggle no LB
    // - AGGRESSIVE: toggle no LT (gatilho)
    // ============================

    // LB toggle SLOW
    new JoystickButton(driverController, Constants.OI.kSlowModeToggleButton)
        .onTrue(Commands.runOnce(driveSubsystem::toggleSlowMode, driveSubsystem));

    // LT toggle AGGRESSIVE (gatilho é eixo analógico)
    Trigger leftTriggerPressed = new Trigger(
        () -> driverController.getRawAxis(Constants.OI.kAggressiveToggleAxis)
              > Constants.OI.kTriggerPressedThreshold);

    // Faz "borda de subida": só dispara quando cruza o threshold
    leftTriggerPressed.onTrue(Commands.runOnce(driveSubsystem::toggleAggressiveMode, driveSubsystem));
  }

  public Command getAutonomousCommand() {
    return null;
  }
}
