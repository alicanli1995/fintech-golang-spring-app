FROM couchbase:latest

MAINTAINER Ali CANLI

COPY scripts/configure-node.sh /
COPY scripts/entrypoint.sh /

CMD ["couchbase-server"]

EXPOSE 8091 8092 8093 8094 8095 8096 11207 11210 11211 18091 18092 18093 18094 18095 18096
VOLUME /opt/couchbase/var