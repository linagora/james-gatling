FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.17_8_1.9.1_2.12.18

USER sbtuser

WORKDIR /home/sbtuser

RUN git clone https://github.com/linagora/james-gatling.git

WORKDIR /home/sbtuser/james-gatling
COPY run.sh /home/sbtuser/james-gatling/run.sh
USER root
RUN chmod 755 /home/sbtuser/james-gatling/run.sh
USER sbtuser

RUN sbt clean compile

ENTRYPOINT [ "bash", "/home/sbtuser/james-gatling/run.sh" ]