# Project Plan

Simple Dice Rolling app in Jetpack Compose for Android that captures scroll/swipe gesture events on the dice face or physical device shake events using accelerometer sensors, and displays a dynamic dice roll animation with sound/vibration feedback and result history.

## Project Brief

# Project Brief: Rollit - Dice Rolling App

## Features
1. **Interactive Roll Triggers**: Trigger dice rolls using touch swipe/scroll gestures on the dice face or physical device shake detection via the accelerometer sensor.
2. **Dynamic Dice Animation & Audio-Haptic Feedback**: Display dynamic rolling animations accompanied by low-latency sound effects and tactile vibration feedback.
3. **Roll Results & History Log**: Instantly display the rolled outcome and maintain a session history log of previous rolls.
4. **Dice Configuration Options**: Customize dice rolls by selecting different dice types (e.g., standard D6, D20) or rolling multiple dice simultaneously.

## High-Level Tech Stack
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose with Material 3
* **Navigation Strategy**: Jetpack Navigation 3 (State-driven navigation)
* **Adaptive Strategy**: Jetpack Compose Material Adaptive (`androidx.compose.material3.adaptive`)
* **Architecture**: MVVM (Model-View-ViewModel) with `StateFlow`
* **Concurrency**: Kotlin Coroutines & Flow
* **Sensors & Hardware Integration**:
  * `SensorManager` (Accelerometer sensor listener for device shake detection)
  * `SoundPool` (Low-latency audio playback for roll sound effects)
  * `VibratorManager` / `HapticFeedback` (Tactile feedback)

## Implementation Steps
**Total Duration:** 3h 36m 43s

### Task_1_CoreModelsAndHardwareServices: Implement core data models (DiceType, RollResult), SensorManager ShakeDetector for shake gestures, and SoundPool / Haptics engine for dice roll audio and tactile feedback.
- **Status:** COMPLETED
- **Updates:** Data models (DiceType, RollResult, DiceRollHistoryItem), ShakeDetector, and FeedbackManager (SoundPool/AudioTrack synthesis + Haptics) successfully implemented and verified with passing unit tests.
- **Acceptance Criteria:**
  - DiceType (D6, D20) and RollResult data models defined
  - Accelerometer ShakeDetector helper implemented
  - SoundPool audio manager and Haptic feedback helper created
- **Duration:** 11m 42s

### Task_2_RollViewModelAndState: Implement RollViewModel using StateFlow to manage selected dice configuration, current roll outcomes, roll animations state, roll history log, and gesture/shake event handlers.
- **Status:** COMPLETED
- **Updates:** RollUiState, RollTrigger, and RollViewModel created with StateFlow managing dice configuration, animated rolling logic, sound/haptic triggers, roll history, and settings. Verified with passing unit tests.
- **Acceptance Criteria:**
  - RollViewModel exposes StateFlow for dice selection, current results, and history log
  - ViewModel integrates dice roll calculation and trigger handling
- **Duration:** 2m 20s

### Task_3_ComposeUIAndAnimations: Build Jetpack Compose screens and components: Interactive Dice view with custom rolling animation and swipe/scroll gesture triggers, Dice configuration selector (D4/D6/D20/etc., count), and Roll History list.
- **Status:** COMPLETED
- **Updates:** Created Jetpack Compose UI components: AnimatedDiceView with gesture swipe/drag triggers and 3D graphicsLayer animations, DiceConfigPanel, ShakeSensorEffect DisposableEffect integration, RollHistoryList, SettingsSheet, MainRollScreen, and MainActivity integration. Verified with gradle assembleDebug.
- **Acceptance Criteria:**
  - Dice view displays rolling animation and handles touch swipe/scroll gestures
  - Dice configuration panel allows changing dice type and quantity
  - Roll history view displays previous rolls with state updates
- **Duration:** 5m 59s

### Task_4_RunAndVerify: Integrate ShakeDetector and SoundPool with Activity lifecycle, assemble full app UI in MainActivity, run the application, and verify stability, UI fidelity, and performance.
- **Status:** COMPLETED
- **Updates:** Enhanced app with immersive fullscreen 3D dice view (reduced paddings, 3D cube/polyhedral shading & rotation), extracted all UI strings into strings.xml, and added complete Brazilian Portuguese (pt-BR) localization in values-pt/strings.xml. Verified build and unit tests successfully.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - critic_agent verifies application stability (no crashes), confirms alignment with user requirements, and reports critical UI issues
- **Duration:** 3h 16m 42s

