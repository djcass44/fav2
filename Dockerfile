# STAGE 1 - BUILD
FROM harbor.v2.dcas.dev/library/base/gradle
LABEL maintainer="Django Cass <django@dcas.dev>"

USER root

WORKDIR /app
RUN chown -R gradle:0 /app && \
    chmod -R g=u /app

USER gradle

COPY --chown=gradle:0 . .

# build the jar
RUN gradle build -x test

# STAGE 2 - RUN
FROM harbor.v2.dcas.dev/library/base/tomcat-native:master
LABEL maintainer="Django Cass <django@dcas.dev>"

WORKDIR /app
COPY --from=0 /app/build/libs/fav2.jar .

# set user permissions
RUN chown -R somebody:0 /app
# drop from root
USER somebody

# run the app
ENTRYPOINT ["java", "-jar", "fav2.jar"]