# STAGE 1 - BUILD
FROM gradle:jdk12 as GRADLE_CACHE
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

WORKDIR /app

# Dry run for caching
COPY . .

RUN gradle jlink -x test

# STAGE 2 - RUN
FROM ubuntu:bionic
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

ENV FAV_ALLOW_CORS=false \
    FAV_ALLOW_HTTP=false \
    FAV_DEBUG=false \
    FAV_HTTP_PORT=8080 \
    FAV_DATA="/data" \
    USER=fav

RUN useradd --system -u 1001 -U ${USER}

WORKDIR /app
COPY --from=GRADLE_CACHE /app/build/image .

EXPOSE $FAV_HTTP_PORT

RUN chown -R ${USER}:${USER} /app
USER ${USER}

ENTRYPOINT ["bash", "/app/bin/fav2"]