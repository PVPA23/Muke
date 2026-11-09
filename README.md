# 🤖 MUKeka — FRC Team Prodixy #9195 (2026)

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![WPILib](https://img.shields.io/badge/WPILib-2026-red?style=flat-square)
![REVLib](https://img.shields.io/badge/REVLib-SparkMax-orange?style=flat-square)
![PhotonVision](https://img.shields.io/badge/PhotonVision-Vision-00A8E8?style=flat-square)

Repositório oficial do código de controle do robô **MUKeka**, desenvolvido em **Java (WPILib Command-Based)** para a equipe **Prodixy (#9195)** na temporada **FRC 2026**.

---

## ⚙️ Arquitetura e Subsistemas

O projeto utiliza a estrutura **Command-Based** da WPILib, dividindo responsabilidades em três subsistemas principais:

### 1. `DriveSubsystem` (Tração Diferencial com Modos de Pilotagem)
* **Hardware:** 4 controladores **REV SparkMax** configurados para motores *Brushed (CIM)* em topologia Líder/Seguidor (`CAN IDs 1, 2, 3, 4`) com limite de corrente de `60A` e modo `Brake`.
* **Modos de Condução (`DriveMode`):** Alternância em tempo real durante a partida com filtros de aceleração (`SlewRateLimiter`) independentes:
  * **`NORMAL`:** Escala `100%`, *Slew Rate* de `3.0` e entradas quadráticas ativadas para controle suave.
  * **`SLOW` (Precisão):** Escala reduzida para `50%` e *Slew Rate* de `2.0` para aproximações finas.
  * **`AGGRESSIVE` (Resposta Rápida):** Escala `100%`, sem filtragem lenta (*Slew Rate* `100.0`) e resposta linear direta para manobras ágeis.

### 2. `IntakeSubsystem` (Coleta e Acionamento Híbrido)
* **Motor Contínuo (`CAN 5` - CIM Brushed):** Acionado automaticamente em potência máxima durante o período **Teleoperado** e desligado automaticamente por segurança ao desabilitar o robô.
* **Motor Controlado (`CAN 6` - NEO Brushless):** Gerenciado por uma máquina de estados (`STOP`, `FORWARD`, `REVERSE`) operando em modo **Toggle/Latch** (um toque liga, outro toque desliga), com limite de corrente de `60A` e desativação explícita de *hard limit switches* via `SparkMaxConfig`.

### 3. `VisionSubsystem` (Visão Computacional)
* Integração com **PhotonVision** (`PhotonCamera`) para detecção de **AprilTags**.
* Calcula e publica continuamente no `SmartDashboard`:
  * Presença de alvo válido (`Vision/HasTarget`)
  * Ângulo horizontal `Yaw` em graus (`Vision/YawDeg`)
  * Estimativa de distância 3D até o alvo em metros (`Vision/RangeMeters`)
  * ID da AprilTag rastreada (`Vision/TargetId`)

---

## 🎮 Mapeamento de Controles (Xbox Controller - Porta 0)

| Controle / Botão | Ação | Subsistema |
| :--- | :--- | :--- |
| **Analógico Esquerdo (Eixo Y)** | Aceleração (Frente / Ré) | `DriveSubsystem` |
| **Analógico Direito (Eixo X)** | Rotação (Giro) | `DriveSubsystem` |
| **Botão `LB`** | Alterna modo **SLOW** (Toggle) | `DriveSubsystem` |
| **Gatilho Esquerdo (`LT`)** | Alterna modo **AGGRESSIVE** (Toggle) | `DriveSubsystem` |
| **Botão `A`** | Liga / Para o motor NEO no sentido **Forward** (Toggle) | `IntakeSubsystem` |
| **Botão `Y`** | Liga / Para o motor NEO no sentido **Reverse** (Toggle) | `IntakeSubsystem` |

---

## 🚀 Como Compilar e Executar

1. Certifique-se de ter o **WPILib VS Code (2026)** e o **JDK 17** instalados.
2. Clone este repositório:
   ```bash
   git clone https://github.com/PVPA23/MUKeka.git
   ```
3. Para compilar o código:
   ```bash
   ./gradlew build
   ```
4. Para enviar o código para o roboRIO (Equipe **#9195**):
   ```bash
   ./gradlew deploy
   ```

---

Desenvolvido por **Vinicius Albuquerque** — Equipe **Prodixy #9195** 🚀
