# Jr. Portfolio-Microservice-Example

> This Project demonstrates a simple example of a **"Microservice"** Pattern with ***Spring Boot*** and ***Maven***.
> To implement security in this project we use **"Okta"**. We will use a **"JWT-token"** to verify us in our
> we will use an **"opaque-token"**. The idea is to create a **"blog-post"** service, 
> where people can **CRUD** (*Create-Read-Update-Delete*) blog-post's. We connect this service to a **"NoSQL"** 
> Database **"MongoDB"**. To play around with **"Amazon Webservices"**, we want to implement a small 
> **"notification"** service with **"SNS"** and **"SQS"**, where a user can **"subscribe"** and **"publish"** messages 
> to they subscribers.

### We will use this Architecture to build our project...

![Shows a Microservice Architecture](assets/images/Architecture_small.png)


Let's have a look to our Services...

## Blog Post Service

### Blogpost Service Model
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @RequiredArgsConstructor
    @Document(collection = "blog_post")
    public class BlogPost {
        @Id
        private UUID id;
    
        @Size(min = 10, max = 40)
        @NotBlank(message = "Title can't be empty")
        private @NonNull String title;
    
        @Size(min = 20, max = 1000)
        @NotBlank(message = "Content can't be empty")
        private @NonNull String content;
    
        private String publishedAt;
    
        @Email(message = "Mail-Address invalid")
        private String creatorEmail;
        private Set<Tags> tags = new HashSet<>();
    }

### Dependencies 

Use Lombok and Spring Validation to simplify the handling.

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

Also, we need our MongoDB dependency in our non-blocking case...

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>

Don't forget to add also our Webflux dependency

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

Because we want to implement our Service to a discovery server in our case **"Eureka Server"**, 
we need the following dependency.

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

### Records
Create also some **records** to speak just with this information, what is really needed from backend to our consumer...

For our normal Requests like to **Create** a BlogPost use this...

    public record BlogPostRequest(String title,
                                  String content,
                                  String[] tags){}

For an update, we must specify the request a little more, so we create an extra record for that.

    public record BlogPostRequestUpdate(String oldTitle,
                                        String newTitle,
                                        String content,
                                        String[] tags) {}

When we get a response from our API, we need also a special record....

    public record BlogPostResponse(String title,
                                   String content,
                                   String email,
                                   String[] tags){}

### In the end, we will use a docker-compose.yml to containerize our application

    version: '3.9'

    services:
    #<========== Gateway ==========>
        api-gateway:
            build: .
            image: api-gateway:latest
            container_name: api-gateway_container
            mem_reservation: 700m
            ports:
                 - "8765:8765"
            networks:
                - portfolio
            depends_on:
                - config-server
                - eureka-server
            environment:
                - eureka.client.serviceurl.defaultzone=http://eureka-server:8761/eureka
                - spring.zipkin.base-url=http://zipkin-server:9411
    #<========== BlogPost ==========>
        blog-post:
            build: .
            image: blog-post:latest
            container_name: blog-post_container
            mem_reservation: 700m
            ports:
                - "8080:8080"
            networks:
                - portfolio
            depends_on:
                - config-server
                - eureka-server
                - mongo
            environment:
                - eureka.client.serviceurl.defaultzone=http://eureka-server:8761/eureka
                - spring.zipkin.base-url=http://zipkin-server:9411
                - spring.data.mongodb.uri=mongodb://mongo:27017/portfolio
    #<========== Notification ==========>
        notification:
            build: .
            image: notification:latest
            container_name: notification_container
            mem_reservation: 700m # Memories limit
            ports:
                - "8081:8081"
            networks:
                - portfolio
            depends_on:
                - config-server
                - eureka-server
                - api-gateway
            environment:
                - eureka.client.serviceurl.defaultzone=http://eureka-server:8761/eureka
                - spring.zipkin.base-url=http://zipkin-server:9411
    #<========== User-Management ==========>
        user-management:
            build: .
            image: user-management:latest
            container_name: user-management_container
            mem_reservation: 700m # Memories limit
            ports:
                - "1997:1997"
            networks:
                - portfolio
            depends_on:
                - config-server
                - eureka-server
                - api-gateway
            environment:
                - eureka.client.serviceurl.defaultzone=http://eureka-server:8761/eureka
                - spring.zipkin.base-url=http://zipkin-server:9411
    #<========== Mongo ==========>
        mongo:
            build: .
            image: mongo:latest
            container_name: mongo_container
            restart: always
            environment:
                MONGO_INITDB_DATABASE: portfolio
            volumes:
                - mongodb_data_container:/data/db
            networks:
                - portfolio
            ports:
                - "27017:27017"
    #<========== Server ==========>
    #<- Eureka ->
        eureka-server:
        build: .
        image: eureka-server:latest
        container_name: eureka-server_container
        mem_reservation: 700m # Memories limit
        depends_on:
            - config-server
        ports:
            - "8761:8761"
        networks:
            - portfolio
    #<- Config ->
        config-server:
        build: .
        image: config-server:latest
        container_name: config-server_container
        mem_reservation: 300m
        ports:
            - "8888:8888"
        networks:
            - portfolio
    #<========== Zipkin ==========>
        zipkin-server:
        build: .
        image: openzipkin/zipkin:latest
        mem_reservation: 300m
        container_name: zipkin_container
        ports:
            - "9411:9411"
        networks:
            - portfolio
        restart: on-failure
    #<========== Swagger UI ==========>
        swagger-ui:
            build: .
            image: swaggerapi/swagger-ui:latest
            mem_reservation: 700m
            container_name: swagger-ui_container
            ports:
                - "9999:9999"
            depends_on:
                - api-gateway
            networks:
                - portfolio

    networks:
        portfolio:

    volumes:
        mongodb_data_container:

### Resources:

**Other**
+ [Spring Boot Documentation 8.2. Reactive Web Applications](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#web.reactive)
+ [Microservices API Documentation with Springdoc OpenAPI](https://piotrminkowski.com/2020/02/20/microservices-api-documentation-with-springdoc-openapi/)

**Okta Blog:**
+ [Better Testing with Spring Security Test](https://developer.okta.com/blog/2021/05/19/spring-security-testing)
+ [OAuth 2.0 Patterns with Spring Cloud Gateway](https://developer.okta.com/blog/2020/08/14/spring-gateway-patterns)