# STAGE 1 - BUILD
FROM gradle:jdk12 as GRADLE_CACHE
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

WORKDIR /app

COPY . .

# build the jar
RUN gradle build -x test

# STAGE 2 - RUN
FROM adoptopenjdk/openjdk12:alpine-jre
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

# create the non-root user to run as
ENV USER=fav
RUN addgroup -S ${USER} && adduser -S ${USER} -G ${USER}
# install latest packages
RUN apk upgrade --no-cache -q

WORKDIR /app
COPY --from=GRADLE_CACHE /app/build/libs/fav2.jar .

# set user permissions
RUN chown -R ${USER}:${USER} /app
# drop from root
USER ${USER}

# run the app
ENTRYPOINT ["java", "-jar", "fav2.jar"]