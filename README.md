# Smart home REST API

REST interface to control my home devices, such as Gree AC. 

The code is written in Java based on the hard work from the guys working on https://github.com/tomikaa87/gree-remote. I take no credit for the research that went into understanding how the interface works.

## Required

- maven
- java 17

## Build

```
mvn clean install
```

## Run

```
java -jar target/smart-home-VERSION.jar 
```

## Where can I see the API?

```
http://localhost:8081/swagger-ui.html
```

### Docker build AMD64 on ARM computers and upload 

```
change directory to project root
docker build -t zvirdaniel/smart-home:1.0 --platform=linux/amd64 .
docker push zvirdaniel/smart-home:1.0 
```