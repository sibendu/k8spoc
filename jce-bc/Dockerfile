FROM ubuntu:20.04

RUN apt-get update && \
  apt-get upgrade -y && \
  apt-get install -q -y openjdk-8-jdk

#RUN java -version

ENV S3_URL=https://bouncycastle.s3.ap-south-1.amazonaws.com
ENV JDK_HOME=/usr/lib/jvm/java-8-openjdk-amd64

#COPY *.jar $JDK_HOME/jre/lib/ext/

ADD $S3_URL/bcjmail-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bcjmail-jdk18on-172.jar
ADD $S3_URL/bcmail-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bcmail-jdk18on-172.jar
ADD $S3_URL/bcpg-jdk18on-172.2.jar 	$JDK_HOME/jre/lib/ext/bcpg-jdk18on-172.2.jar
ADD $S3_URL/bcpkix-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bcpkix-jdk18on-172.jar
ADD $S3_URL/bcprov-ext-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bcprov-ext-jdk18on-172.jar
ADD $S3_URL/bcprov-jdk18on-172.jar	$JDK_HOME/jre/lib/ext/bcprov-jdk18on-172.jar
#ADD $S3_URL/bctest-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bctest-jdk18on-172.jar
ADD $S3_URL/bctls-jdk18on-172.jar	$JDK_HOME/jre/lib/ext/bctls-jdk18on-172.jar
ADD $S3_URL/bcutil-jdk18on-172.jar 	$JDK_HOME/jre/lib/ext/bcutil-jdk18on-172.jar

RUN cp $JDK_HOME/jre/lib/security/java.security $JDK_HOME/jre/lib/security/java.security.bkup

RUN sed -i '/^security.provider.9=.*/a security.provider.10=org.bouncycastle.jce.provider.BouncyCastleProvider' $JDK_HOME/jre/lib/security/java.security 

RUN echo Bouncy Castle installed and enabled

COPY BouncyCastleDemo.java  ./

RUN javac BouncyCastleDemo.java

RUN java BouncyCastleDemo