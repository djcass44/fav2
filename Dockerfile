# STAGE 1 - BUILD
FROM gradle:jdk12 as GRADLE_CACHE
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

WORKDIR /app

# Dry run for caching
COPY . .

RUN gradle shadowJar -x test

# STAGE 2 - RUN
FROM adoptopenjdk/openjdk12:alpine-jre
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

ENV FAV_ALLOW_CORS=false \
    FAV_ALLOW_HTTP=false \
    FAV_DEBUG=false \
    FAV_HTTP_PORT=8080 \
    FAV_DATA="/data" \
    USER=fav

RUN addgroup -S ${USER} && adduser -S ${USER} -G ${USER}

WORKDIR /app
COPY --from=GRADLE_CACHE /app/build/libs/fav.jar .

EXPOSE $FAV_HTTP_PORT

RUN chown -R ${USER}:${USER} /app
USER ${USER}

ENTRYPOINT ["java", "-jar", "fav.jar"]