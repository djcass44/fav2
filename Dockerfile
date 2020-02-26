# STAGE 1 - BUILD
FROM harbor.v2.dcas.dev/library/gradle:jdk13 as GRADLE_CACHE
LABEL maintainer="Django Cass <django@dcas.dev>"

WORKDIR /app

COPY . .

# build the jar
RUN gradle build -x test

# STAGE 2 - RUN
FROM harbor.v2.dcas.dev/djcass44/adoptopenjdk-spring-base:13-alpine-jre
LABEL maintainer="Django Cass <django@dcas.dev>"

# create the non-root user to run as
ENV USER=fav
RUN addgroup -S ${USER} && adduser -S ${USER} -G ${USER}

WORKDIR /app
COPY --from=GRADLE_CACHE /app/build/libs/fav2.jar .

# set user permissions
RUN chown -R ${USER}:${USER} /app
# drop from root
USER ${USER}

# run the app
ENTRYPOINT ["java", "-jar", "fav2.jar"]