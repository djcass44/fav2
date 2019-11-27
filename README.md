# FAV2

Fav2 is a simple application used to scrape the url of [favicons](https://en.wikipedia.org/wiki/Favicon) from websites. 

*Note: Fav2 will not find icons on websites using HTTP*

### Setup

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
    restart: always
    ports:
      - 8080:8080
    environment:
      - FAV_URL=https://icon.mydomain.org
      - FAV_PATH=/mnt/myvolume
```

**Configuration**

See [application.yaml](src/main/resources/application.yaml) for configuration details

#### Usage

* GET: `/icon?site=https://example.org`
    * Try to load the favicon for this url
    * If the favicon hasn't been downloaded yet, the application will internally make the above POST request
* GET: `/icon/cache`
    * Shows the current contents of the cache
    * **Note**: will not return actual image data, just site names and cache age
* DELETE `/icon/cache?site=https://example.org`
    * Will remove the favicon for this site from the cache
    * **Note**: this will not remove the favicon if it's been saved to permanent storage

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
