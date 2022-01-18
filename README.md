# TextRecognition
[![](https://jitpack.io/v/raipankaj/TextRecognition.svg)](https://jitpack.io/#raipankaj/TextRecognition)

A library to add text recognition capability to your app built with Jetpack Compose, it internally uses ML Kit text recognition.

To get started with TextRecognizer just add the maven url and the TextRecognition dependency

<b>build.gradle (Project level)</b>
```groovy
allprojects {
    repositories {
    ...
    //Add this url
    maven { url 'https://jitpack.io' }
    }
}
```
If you are using Android Studio Arctic Fox and above where you don't have allProjects in build.gradle then add following maven url in <b>settings.gradle</b> like below
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //Add this url
        maven { url 'https://jitpack.io' }
        jcenter() // Warning: this repository is going to shut down soon
    }
}
```

Once you have added the maven url now add the TextRecognition dependency in the <b>build.gradle (module level)</b>
```groovy
implementation 'com.github.raipankaj:TextRecognition:0.1.1'
```

Congratulations, you have successfully added the dependency. 
Now to get started with TextRecognizer add the following code snippet
```kotlin
TextRecognizer(
          onSuccess = {
              //Action based on Text
          },
          onException = {
              //Action based on exception
          }
)
```

<br>


Following is a full fledged working sample code
```kotlin
var text by remember {  mutableStateOf("")  }
Surface(color = MaterialTheme.colors.background) {
    TextRecognizer(
        onSuccess = {
            text = it.text
        },
        onException = { }
    )
}
```
<br>
Also do not forget to request for camera permission.
<br>
Note: If you like this library, then please hit the star button! :smiley:
