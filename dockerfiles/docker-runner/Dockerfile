FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.17_8_1.9.1_2.12.18

USER sbtuser

WORKDIR /home/sbtuser

RUN git clone https://github.com/linagora/james-gatling.git

WORKDIR /home/sbtuser/james-gatling

RUN sbt clean compile

CMD sbt