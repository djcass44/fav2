# STAGE 1 - BUILD
FROM gradle:5.5.1-jdk12 as GRADLE_CACHE
LABEL maintainer="Django Cass <dj.cass44@gmail.com>"

WORKDIR /app

# Dry run for caching
COPY . .

RUN gradle jlink

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

# Add Tini
ENV TINI_VERSION v0.18.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

RUN mkdir -p $FAV_DATA && \
    chown -R ${USER}:${USER} $FAV_DATA /tini /app && \
    chmod -R 755 $FAV_DATA
USER ${USER}

ENTRYPOINT ["/tini", "--"]
CMD ["bash", "/app/bin/fav2"]