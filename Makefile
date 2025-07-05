integration-test:
	./gradlew :app:connectedAndroidTest --info -P android.testInstrumentationRunnerArguments.size=small

test:
	./gradlew :app:testDebugUnitTest

enable-dev-events:
	adb shell setprop debug.firebase.analytics.app com.fstengineering.exportcalendars.dev

enable-prod-events:
	adb shell setprop debug.firebase.analytics.app com.fstengineering.exportcalendars

disable-events:
	adb shell setprop debug.firebase.analytics.app .none.

enable-debug-crashlytics:
	adb shell setprop log.tag.FirebaseCrashlytics DEBUG

disable-debug-crashlytics:
	adb shell setprop log.tag.FirebaseCrashlytics INFO
