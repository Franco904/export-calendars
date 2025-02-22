integration-test:
	./gradlew :app:connectedAndroidTest --info -P android.testInstrumentationRunnerArguments.size=small

test:
	./gradlew :app:testDebugUnitTest
