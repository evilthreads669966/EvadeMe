[![Release](https://jitpack.io/v/evilthreads669966/evademe.svg)](https://jitpack.io/#evilthreads669966/evademe)&nbsp;&nbsp;[![API](https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=plastic)](https://android-arsenal.com/api?level=15)&nbsp;&nbsp;[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EvadeMe-brightgreen.svg?style=plastic)](https://android-arsenal.com/details/1/8172)&nbsp;&nbsp;[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://kotlin.link)
# EvadeMe
### An asynchronous heuristics evasion library for Android with a KTX scoping function.
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
    implementation 'com.github.evilthreads669966:evademe:2.0'
}
```
3. Use the evade ktx function inside of any android context.
```kotlin
evade(coroutineScope) {
    Log.d("EVADE", "EVIL THREADS");
}.onEscape{
    Toast.makeText(this, "We evaded with networking", Toast.LENGTH_LONG).show()
}.onSuccess {
    Toast.makeText(this, "We executed the payload with networking", Toast.LENGTH_LONG).show()
}
```
## Important To Know
- evade is a suspension function
- any code inside of the evade scoping function is safe from analysis.
- evade is a KTX function with a receiver of type context
- evade by default assumes that your are passing a function uses internet
    - If you have a payload that does not require internet then you can pass in false to evade
        - Passing in false to evade allows to skip evasion checks that are related to network analysis
## Ask a Question?
- Use [Github issues](https://github.com/evilthreads669966/evademe/issues)
- Send an email to evilthreads669966@gmail.com

## Reporting issues
Found a bug on a specific feature? Open an issue on [Github issues](https://github.com/evilthreads669966/evademe/issues)

## Contributing

SMS Backdoor is released under the [Apache 2.0 license](https://github.com/evilthreads669966/evademe/blob/master/LICENSE). If you would like to contribute
something, or simply want to hack then this document should help you get started.

### Code of Conduct
- Please refrain from using any profanity
- Please be respectful on [GitHub Issues](https://github.com/evilthreads669966/evademe/issues)
- Have fun

### [Pull Requests](https://github.com/evilthreads669966/evademe/pulls)
- Please create a branch prefixed with what you're working on.
    - FEATURE_ADDING_SOMETHING
    - BUG_FIXING_SOMETHING
    - REFACTOR_CHANGING_SOMETHING
- Once you're done with your commits to this branch hit a [pull request](https://github.com/evilthreads669966/evademe/pulls) off and I'll look at it and most likely accept it if it looks good.

### Using [GitHub Issues](https://github.com/evilthreads669966/evademe/issues)
We use [GitHub issues](https://github.com/evilthreads669966/evademe/issues) to track bugs and enhancements.
- If you find a bug please fill out an issue report. Provide as much information as possible.
- If you think of a great idea please fill out an issue as a proposal for your idea.

### Code Conventions
None of these is essential for a pull request, but they will all help.  They can also be
added after the original pull request but before a merge.

- We use idiomatic kotlin conventions
- Add yourself as an `@author` to the `.kt` files that you modify or create.
- Add some comments
- A few unit tests would help a lot as well -- someone has to do it.
- If you are able to provide a unit test then do.
    - Because of the types of libraries I develop often times it is hard to test.


### Working with the code
If you don't have an IDE preference we would recommend that you use
[Android Studio](https://developer.android.com/studio/)
## Contributors
This project exists thanks to all the people who contribute.
<a href="https://github.com/evilthreads669966/evademe/graphs/contributors"><img src="https://opencollective.com/evademe/contributors.svg?width=890&button=false" /></a>
## Talking about EvadeMe
### Articles
- [Android Heuristic Evasion](https://medium.com/swlh/evademe-5c2e59083b43)
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
