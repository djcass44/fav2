# FAV2

Fav2 is a simple application used to scrape the url of [favicons](https://en.wikipedia.org/wiki/Favicon) from websites. 

*Note: Fav2 will not find icons on websites using HTTP*

### Setup

The recommended method of running fav2 is via docker.
The application has full JPMS support and can therefore run standalone *without a JDK or JRE*

Standalone (with or without JRE)

```shell script
./gradlew jlink # build
./build/image/bin/fav2 # run
```

Docker
```shell script
docker run -p 8080:8080 djcass44/fav2
```
Docker Compose
```yaml
version: '3'
services:
  fav:
    image: djcass44/fav
    ports:
      - 8080:8080
    environment:
      - FAV_BASE_URL=https://icon.mydomain.org
      - FAV_DATA=/mnt/myvolume
```

**Configuration**

`FAV_ALLOW_CORS`: Allow CORS requests (default `false`)

`FAV_DEBUG`: Show additional debug information (default `false`)

`FAV_HTTP_PORT`: The port for the HTTP server to listen on (default `8080`)

`FAV_DATA`: The directory to use for storage of images (default `/data`)

`FAV_BASE_URL`: The url that the application is reachable on (default `http://localhost:8080`). This is required if you're running it behind a reverse-proxy

#### Usage

* POST: `/icon?site=https://example.org`
    * This will tell the application to download the favicon at `https://example.org`
* GET: `/icon?site=https://example.org`
    * Try to load the favicon for this url
    * If the favicon hasn't been downloaded yet, the application will internally make the above POST request
* GET: `/healthz`
    * Returns "200 OK" to signify that the HTTP is up and capable of responding to requests

### License

Fav2 is released under the [Apache 2.0 license](LICENSE)
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
