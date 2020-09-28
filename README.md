# EvadeMe
### A heuristics evasion library for Android
1. Add the maven repository to your project's allprojects closure in the gradle.build file
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency to your app's build.gradle file
```groovy
	dependencies {
		implementation 'com.github.evilthreads669966:evademe:1.0'
	}
```