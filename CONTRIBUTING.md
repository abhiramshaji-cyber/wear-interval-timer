# Contributing

Issues and pull requests are welcome. This is a small project, so the bar is
simple: keep each change focused on one thing, and say why in the description.

## Getting set up

```sh
./gradlew assembleDebug
```

Then install the APK on a round Wear OS watch or the Wear OS emulator. GitHub
Actions also builds a debug APK on every push to `main`.

## Before opening a pull request

- Test on a round display. A layout that only looks right on a square emulator is
  not finished.
- Say which watch and which Wear OS version you tested on.
- Vibration and the phase cycle are the product. If you touch either, confirm the
  distinct patterns still fire on every transition, and that the screen stays on
  while the app is open.

The app is deliberately tiny. Features that add configuration screens or
background services will likely be declined, so please open an issue first.

Issues labelled `good first issue` are self contained and a good place to start.
