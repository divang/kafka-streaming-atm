FROM anapsix/alpine-java:latest

RUN mkdir app 
WORKDIR "/app" 
COPY  target/scala-2.13/atm-1.0.jar .
COPY init.sh .
RUN ["chmod", "+x", "/app/init.sh"]

EXPOSE 8080
CMD ["sh", "init.sh"]
