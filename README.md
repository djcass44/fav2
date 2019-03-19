# FAV2

Fav2 is a simple library used to scrape the url of [favicons](https://en.wikipedia.org/wiki/Favicon) from websites. 

*Note: Fav2 will not find icons on websites using HTTP unless you specifically enable it*
### Download

Gradle:

1. In your root build.gradle
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2. In your project build.gradle
```gradle
dependencies {
    implementation 'com.github.djcass44:fav2:0.1.1'
}
```
Maven:

1. Add the JitPack repository
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
2. Add the dependency
```xml
<dependency>
    <groupId>com.github.djcass44</groupId>
    <artifactId>fav2</artifactId>
    <version>0.1.1</version>
</dependency>
```

### Usage
Synchronous:
```kotlin
val link = Fav.loadDomain("https://github.com")
println("Found link: $link")
```

Asynchronous:
```kotlin
Fav.loadDomain(domain, object : Fav.OnLoadedCallback {
    override fun onLoad(favicon: String?) {
        println("Found link: $favicon")
    }
})
```

**Additional options**

```Fav.DEBUG```(default: false): Show additional logging

```Fav.ALLOW_HTTP``` (default: false): Allow scraping of icons on insecure origins

Explanation:
```kotlin
Fav.ALLOW_HTTP = false
val link = Fav.loadDomain("http://github.com") // will always return null

Fav.ALLOW_HTTP = true
val link = Fav.loadDomain("http://github.com") // will return an icon if found
```

### License

Fav2 is releasesed under the [Apache 2.0 license](LICENSE)
```
Copyright 2019 Django Cass

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

### Contributing

I will happily accept contributions, just fork and open a pull request!
