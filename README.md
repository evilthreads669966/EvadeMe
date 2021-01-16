[![Release](https://jitpack.io/v/evilthreads669966/evademe.svg)](https://jitpack.io/#evilthreads669966/evademe)&nbsp;&nbsp;[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=15)&nbsp;&nbsp;[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EvadeMe-brightgreen.svg?style=plastic)](https://android-arsenal.com/details/1/8172)&nbsp;&nbsp;[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://kotlin.link)
# EvadeMe
### An Android library for heuristics evasion that prevents your code from being tested.
## User Instructions
1. Add the maven repository to your project's build.gradle file
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency to your app's build.gradle file
```gradle
dependencies {
    implementation 'com.github.evilthreads669966:evademe:2.2'
}
```
3. Use the evade ktx function inside of any android context.
```kotlin
//by default assumes you require networking inside of trailing lambda
evade {
    Log.d("EVADE", "EVIL THREADS");
}.onEscape{
    Toast.makeText(this, "We evaded with networking", Toast.LENGTH_LONG).show()
}.onSuccess {
    Toast.makeText(this, "We executed the payload with networking", Toast.LENGTH_LONG).show()
}

//maby you don't require any networking for your payload inside of trailing lambda
evade(requiresNetworking = false) {
    Log.d("EVADE", "EVIL THREADS");
}
```
## Important To Know
- evade is a suspension function
- any code inside of the evade scoping function is safe from analysis.
- evade is a KTX function with a receiver of type context
- evade by default assumes that your are passing a function that uses internet
  - If you have a payload that does not require internet then you can pass false to requiresNetworking optional parameter
    - Passing in false to evade allows to skip evasion checks that are related to network analysis
## Talking about EvadeMe
### Articles
- [Published in the Start Up on Medium](https://medium.com/swlh/evademe-5c2e59083b43)
- [Featured in Android Weekly](https://www.androidweekly.io/android-dev-weekly-issue-297/)
## License
```
Copyright 2020 Chris Basinger

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
limitations under the License.
```
