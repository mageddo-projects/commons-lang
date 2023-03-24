## Commons Lang
A set of useful classes, evicting you repeating yourself, avoiding fatigue

### Regex

```java
Regexes.group("https://www.youtube.com/watch?v=pvZmzT7KR3I", "v=([\\w\\-\\_]+)", 1);
// pvZmzT7KR3I
```

## Installing

```groovy
implementation 'com.mageddo.commons:commons-lang:0.1.16'
```

## Building
### Build and install dep locally

```bash
./gradlew clean build publishToMavenLocal
```


### Build, Publish to Sonatype and Release

```bash
./gradlew clean release build publishToMavenCentral closeAndReleaseMavenCentralStagingRepository
```
