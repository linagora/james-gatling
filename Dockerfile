FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.17_8_1.9.1_2.12.18

USER sbtuser

RUN mkdir /home/sbtuser/james-gatling

WORKDIR /home/sbtuser/james-gatling

COPY --chown=sbtuser:sbtuser . .

RUN sbt clean compile

CMD sbt