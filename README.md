## Commons Lang
A set of common utils for jdbc use

### Regex

```java
Regexes.group("https://www.youtube.com/watch?v=pvZmzT7KR3I", "v=([\\w\\-\\_]+)", 1);
// pvZmzT7KR3I
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