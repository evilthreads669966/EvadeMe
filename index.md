# EvadeMe
A heurstics evasion library for Android with an Android context KTX scoping function that prevents your code from testing and analysis.
Any code inside of the evade scoping function is safe from analysis. This allows you to develop software that is not able to be tested by people.
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
    implementation 'com.github.evilthreads669966:evademe:1.0'
}
```
3. Use the evade ktx function inside of any android context.
```kotlin
evade {
    Log.d("EVADE", "EVIL THREADS");
}.onEscape{
    Toast.makeText(this, "We evaded with networking", Toast.LENGTH_LONG).show()
}.onSuccess {
    Toast.makeText(this, "We executed the payload with networking", Toast.LENGTH_LONG).show()
}
```
## Contributing
This library is very hard to test in code as the intent of the code being written is to avoid testing.
1. Fork the repository
2. Create a branch using these examples
  - feature_some_feature
  - refactor_some_refactor
  - bugfix_some_bug
3. Commit your code change to the branch explaining what the code does
4. Do a pull request
All contributors will be listed in the README.md file with links to their profile.
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

### Support or Contact
<a href="mailto:evilthreads669966@gmail.com">evilthreads669966@gmail.com</a>
