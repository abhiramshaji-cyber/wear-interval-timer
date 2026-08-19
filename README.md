# Intervals

A deliberately tiny Wear OS interval timer. Open it and it runs forever:

```
GET READY 3s  ->  WORK 60s  ->  REST 120s  ->  WORK 60s  ->  REST 120s  ->  ...
```

The 60s and 120s are defaults; both are adjustable in the app.

Built for a OnePlus Watch 2R (Wear OS 4), personal use.

60s caps a set near 30 reps at normal calisthenics tempo, which is where hypertrophy per set
starts falling off. 120s is where extra inter set rest stops paying for itself. Treat the 60s
as a ceiling, not a target: if you hit true failure early, stop and let the rest run.

- Distinct vibration on every phase change: double buzz for WORK, one long buzz for REST.
- Screen stays on while the app is open.
- Tap anywhere to pause or resume.
- Long press anywhere to reset back to GET READY and start the cycle over.
- Tap **SETTINGS** to change the work and rest durations in 5s steps, from 5s up to 10:00. The
  timer pauses while the panel is open; **DONE** (or a swipe back) saves and restarts the cycle
  from GET READY. Durations persist on the watch across restarts.
- Close the app to stop. Nothing runs in the background.

## Getting the APK

GitHub Actions builds it on every push to `main`. Go to the **Actions** tab, open the latest
`build` run, and download the `intervals-debug-apk` artifact. Unzip it to get `app-debug.apk`.

## Installing on the watch

The Watch 2R has no USB port, so use ADB over WiFi.

1. On the watch: **Settings > System > About > Build number**, tap 7 times to enable developer options.
2. **Settings > Developer options**, turn on **ADB debugging** and **Debug over WiFi**.
3. Note the IP and port shown under Debug over WiFi.
4. From this machine, with the watch on the same WiFi network:

```sh
adb connect <watch-ip>:<port>       # accept the prompt on the watch
adb install -r app-debug.apk
```

The app appears in the watch's app list as **Intervals**.

## Building locally (optional)

Requires JDK 17 and the Android SDK.

```sh
./gradlew assembleDebug
```

## Changing the intervals

Use the in-app **SETTINGS** panel. The defaults (60s work, 120s rest) live in
[`app/src/main/java/com/abhiram/intervaltimer/Intervals.kt`](app/src/main/java/com/abhiram/intervaltimer/Intervals.kt).

## Contributing

Issues and pull requests are welcome. Please test on a round display and say which watch you used. See [CONTRIBUTING.md](CONTRIBUTING.md).

## Licence

MIT. See [LICENSE](LICENSE).
