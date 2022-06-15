# Jr. Portfolio-Microservice-Example

> This Project demonstrates a simple example of a **"Microservice"** Pattern with ***"Spring Boot"*** and ***"Maven"***.
> To implement security in this project, we will use **"Okta"** with a **"JWT-Token"** and an **"Opaque-Token"**. 
> The idea is to create a **"blog-post"** service, where people can **CRUD** (*Create-Read-Update-Delete*) blog-post's. 
> We connect this service to a **"NoSQL"** Database **"MongoDB"**. To play around with **"Amazon Webservices"**, 
> we want to implement a small **"notification"** service with **"SNS"** and **"SQS"**, where a user can 
> **"subscribe"** and **"publish"** messages to they subscribers.

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

Use Lombok, Devtools and Spring Validation to simplify the handling with the classes.

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
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

For our Testing purpose, we need also an additional tool, who makes TDD and BDD easier to read and write.

    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>{Use the Latest Version}</version> 
        <scope>test</scope>
    </dependency>

Now we have a good base to start...

### Records

Create also some **records** to retrieve only this information, what is really needed for our consumer...

For our normal Requests like to **create** a BlogPost use this...

    public record BlogPostRequest(String title,
                                  String content,
                                  String[] tags){}

For an **update**, we must specify the request a little more, so we create an extra record for that.

    public record BlogPostRequestUpdate(String oldTitle,
                                        String newTitle,
                                        String content,
                                        String[] tags) {}

When we get a response from our API, we would like to have our blog-post creators email address, 
for that we need a special record....

    public record BlogPostResponse(String title,
                                   String content,
                                   String email,
                                   String[] tags){}

### Repository

For our blog-post service we use this methodes....

    public interface IBlogPostRepository extends ReactiveMongoRepository<BlogPost, UUID> {
        Mono<BlogPost> findByTitleAndCreatorEmail(String title, String email);
        Mono<Boolean> existsBlogPostByTitleAndCreatorEmail(String title, String email);
        Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email);
        Flux<BlogPost> findAllByTags(Set<Tags> tags);
    }
### Exception Handling

To create our own exceptions create a utils folder and place some exception case classes there.

For Bad Requests....

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class BadRequestException extends RuntimeException{
        public BadRequestException(String message){
        super(message);
        }
    }

In Case of NoContent in our Database

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public class NoContentException extends RuntimeException{
        public NoContentException(String message){
        super(message);
        }
    }

If we don't find what we are looking for create...

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class NotFoundException extends RuntimeException{
        public NotFoundException(String message){
        super(message);
        }
    }


> If you like, create an enum with your messages

It could be something like this....

    public enum ExceptionResponse {
        BLOG_POST_NOT_FOUND,
        BLOG_POST_ALREADY_EXISTS,
        BLOG_POST_TAG_DOES_NOT_EXISTS,
        BLOG_POST_WITH_THESE_TAGS_NOT_FOUND,
        NO_CONTENT_IN_DB,
        REQUESTED_MODEL_INVALID,
        REQUESTED_MODEL_NOT_EXCEPTED,
        REQUEST_NOT_EXCEPTED
    }
### Other Utils

In this project we use a custom tool to show us the created Date.

    public interface IDateTimeCreator {
        @Bean
        static String createDateTime(){
            return LocalDateTime
            .now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

### Note
> Don't forget to create some **Tests** for our service **AssertJ** helps us to write well organized and good tests !

Here are a test cases example for our Repository....

        @Test
        @DisplayName("should retrieve BlogPost by findByTitleAndCreatorEmail() ")
        void should_retrieve_BlogPost_by_findByTitleAndCreatorEmail() {
            Mono<BlogPost> requestedBlogPost =
                    IBlogPostRepository.findByTitleAndCreatorEmail(
                            "The Weather Girls in New York City",
                            "soul-sisters@gmail.com");
    
            StepVerifier
                    .create(requestedBlogPost)
                    .assertNext(
                            check -> {
                                assertThat(check)
                                        .isNotNull();
                                assertThat(check.getTitle())
                                        .isEqualTo("The Weather Girls in New York City");
                                assertThat(check.getCreatorEmail())
                                        .isEqualTo("soul-sisters@gmail.com");
                            })
                    .verifyComplete();
    }

Here are a test cases example for our Service....

    @Test
    @DisplayName("should createBlockPost()")
    void should_createBlockPost() {
        given(blogPostRepository.existsBlogPostByTitleAndCreatorEmail(anyString(),anyString()))
        .willReturn(Mono.just(false));
        given(blogPostRepository.save(any()))
        .willReturn(Mono.just(blogPost));
    
            Mono<BlogPostResponse> requestedBlogPost =
                    blogPostService.createBlogPost(blogPostRequest,blogPost.getCreatorEmail());
    
            StepVerifier
                    .create(requestedBlogPost)
                    .assertNext(check -> {
                        assertThat(check.title())
                                .isEqualTo("This is the way");
                        assertThat(check.content())
                                .isEqualTo("A special sentence with force in it.");})
                    .verifyComplete();
    }

Here are a test cases example for our Controller....

    @Test
    @DisplayName("should getAllBlogPosts()")
    void should_getAllBlogPosts() {
        when(blogPostRepository.findAll())
                .thenReturn(Flux.just(blogPost));

        webTestClient
                .get()
                .uri(baseUrl)
                .exchange()
                .expectStatus()
                .isOk();

        verify(blogPostRepository,times(1))
                .findAll();
    }

More test scenarios and further details you can find in this repository....

### Service 

To handle our request well organized we create a Service-Interface and implement it to 
a class.

    public interface IBlogPostService {
        Mono<BlogPostResponse> createBlogPost(BlogPostRequest blogPostRequest, String email);
        Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(String title, String email);
        Flux<BlogPostResponse> getAllBlogPosts();
        Flux<BlogPostResponse> getAllBlogPostsWithTags(String[] tags);
        Mono<BlogPostResponse> updateBlogPost(BlogPostRequestUpdate blogPostRequest, String email);
        Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email);
    }

Create a class BlogPostServiceImpl and implement the created Interface into our service.

    @Service
    @RequiredArgsConstructor
    public class BlogPostServiceImpl implements IBlogPostService {

        private final IBlogPostRepository blogPostRepository;
    
        @Override
        public Mono<BlogPostResponse> createBlogPost(BlogPostRequest blogPostRequest, String email) {
            return blogPostRepository
                    .existsBlogPostByTitleAndCreatorEmail(
                            blogPostRequest.title(),
                            email)
                    .flatMap(exists -> {
                        if(exists){
                            return Mono.error(
                                    new BadRequestException(valueOf(BLOG_POST_ALREADY_EXISTS)));
                        }
                        return blogPostRepository
                                .save(new BlogPost(
                                        UUID.randomUUID(),
                                        blogPostRequest.title(),
                                        blogPostRequest.content(),
                                        IDateTimeCreator.createDateTime(),
                                        email,
                                        stream(blogPostRequest.tags())
                                                .map(tags -> Tags.valueOf(tags.toUpperCase()))
                                                .collect(Collectors.toSet())))
                                .map(blogPost -> new BlogPostResponse(
                                        blogPost.getTitle(),
                                        blogPost.getContent(),
                                        email,
                                        blogPost.getTags().stream()
                                                .map(Enum::toString)
                                                .toArray(String[]::new)))
                                .switchIfEmpty(Mono.error(
                                        new BadRequestException(valueOf(REQUESTED_MODEL_INVALID))));
    
                    }).switchIfEmpty(Mono.error(
                            new BadRequestException(valueOf(REQUEST_NOT_EXCEPTED))));
        }
    
        @Override
        public Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(String title, String email) {
            return blogPostRepository
                    .existsBlogPostByTitleAndCreatorEmail(title,email)
                    .flatMap(exists -> {
                        if(!exists){
                            return Mono.error(
                                    new NotFoundException(valueOf(BLOG_POST_NOT_FOUND)));
                        }
                        return blogPostRepository
                                .findByTitleAndCreatorEmail(title,email)
                                .map(request -> new BlogPostResponse(
                                        request.getTitle(),
                                        request.getContent(),
                                        email,
                                        request.getTags().stream()
                                                .map(Enum::toString)
                                                .toArray(String[]::new)));
                    }).switchIfEmpty(Mono.error(
                            new BadRequestException(valueOf(REQUEST_NOT_EXCEPTED))));
        }
    
        @Override
        public Flux<BlogPostResponse> getAllBlogPosts() {
            return blogPostRepository
                    .findAll()
                    .map(blogPost -> new BlogPostResponse(
                            blogPost.getTitle(),
                            blogPost.getContent(),
                            blogPost.getCreatorEmail(),
                            blogPost.getTags().stream()
                                    .map(Enum::toString)
                                    .toArray(String[]::new)))
                    .switchIfEmpty(Mono.error(
                            new NoContentException(valueOf(NO_CONTENT_IN_DB))));
        }
    
        @Override
        public Flux<BlogPostResponse> getAllBlogPostsWithTags(String[] tags) {
            return blogPostRepository
                    .findAllByTags(stream(tags)
                            .map(mytags ->
                                    Tags.valueOf(mytags.toUpperCase()))
                            .collect(Collectors.toSet()))
                    .map(response -> new BlogPostResponse(
                            response.getTitle(),
                            response.getContent(),
                            response.getCreatorEmail(),
                            response.getTags().stream()
                                    .map(Enum::toString)
                                    .toArray(String[]::new)))
                    .switchIfEmpty(Mono.error(
                            new NotFoundException(valueOf(BLOG_POST_WITH_THESE_TAGS_NOT_FOUND))));
        }
    
        @Override
        public Mono<BlogPostResponse> updateBlogPost(BlogPostRequestUpdate blogPostRequest, String creatorEmail) {
            return blogPostRepository
                    .existsBlogPostByTitleAndCreatorEmail(
                            blogPostRequest.oldTitle(),
                            creatorEmail)
                    .flatMap(exists -> {
                        if (!exists){
                            return Mono.error(new BadRequestException(valueOf(BLOG_POST_NOT_FOUND)));
                        }
                        Mono<BlogPost> requestedBlogPost = blogPostRepository
                                .findByTitleAndCreatorEmail(
                                        blogPostRequest.oldTitle(),
                                        creatorEmail);
                        return requestedBlogPost
                                .flatMap(update -> {
                                    update.setTitle(blogPostRequest.newTitle());
                                    update.setContent(blogPostRequest.content());
                                    update.setPublishedAt(IDateTimeCreator.createDateTime());
                                    update.setTags(stream(blogPostRequest.tags())
                                            .map(request -> Tags.valueOf(request.toUpperCase()))
                                            .collect(Collectors.toSet()));
    
                                    return blogPostRepository
                                            .save(update)
                                            .map(response -> new BlogPostResponse(
                                                    response.getTitle(),
                                                    response.getContent(),
                                                    response.getCreatorEmail(),
                                                    response.getTags().stream()
                                                            .map(Enum::toString)
                                                            .toArray(String[]::new)));
                                });
                    });
        }
    
        @Override
        public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(String title, String email) {
            return blogPostRepository
                    .existsBlogPostByTitleAndCreatorEmail(title,email)
                    .flatMap(exists -> {
                        if (!exists){
                            return Mono.error(
                                    new BadRequestException(valueOf(BLOG_POST_NOT_FOUND)));
                        }
                        return blogPostRepository
                                .deleteBlogPostByTitleAndCreatorEmail(title,email);
                    });
        }
    }

### Controller

To access to our API we need a RestController with some endpoints...

    @RestController
    @RequestMapping("/api/v1/blogpost")
    @RequiredArgsConstructor
    public class BlogPostController {

        private final BlogPostServiceImpl blogPostServiceImpl;
    
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<BlogPostResponse> createBlogPost(
                                            @RequestBody BlogPostRequest blogPostRequest,
                                            @RequestParam String creatorEmail){
           return blogPostServiceImpl.createBlogPost(blogPostRequest,creatorEmail);
        }

        @GetMapping("/find")
        @ResponseStatus(HttpStatus.OK)
        public Mono<BlogPostResponse> getBlogPostByTitleAndCreatorEmail(
                                            @RequestParam String title,
                                            @RequestParam String creatorEmail){
            return blogPostServiceImpl.getBlogPostByTitleAndCreatorEmail(title,creatorEmail);
        }

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        public Flux<BlogPostResponse> getAllBlogPosts(){
            return blogPostServiceImpl.getAllBlogPosts();
        }
    
        @GetMapping("/filter")
        @ResponseStatus(HttpStatus.OK)
        public Flux<BlogPostResponse> getBlogPostByTags(@RequestParam String[] tags){
            return blogPostServiceImpl.getAllBlogPostsWithTags(tags);
        }

        @PutMapping("/update")
        @ResponseStatus(HttpStatus.OK)
        public Mono<BlogPostResponse> updateBlogPost(
                                            @RequestBody BlogPostRequestUpdate blogPostRequest,
                                            @RequestParam String creatorEmail){
            return blogPostServiceImpl.updateBlogPost(blogPostRequest,creatorEmail);
        }

        @DeleteMapping
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public Mono<Void> deleteBlogPostByTitleAndCreatorEmail(
                                            @RequestParam String title,
                                            @RequestParam String creatorEmail){
            return blogPostServiceImpl.deleteBlogPostByTitleAndCreatorEmail(title,creatorEmail);
        }
    }

## Discovery Server

To register our services we need a Discovery-Server. In our case we'll use Eureka-Server.

Create a new Service with the following dependency

### Dependencies

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>

### Main-class
Make sure you added **"@EnableEurekaServer"**...

    @EnableEurekaServer
    @SpringBootApplication
    public class EurekaServerApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(EurekaServerApplication.class, args);
        }
    
    }

add the following properties to your Eureka application.yml file...

    server:
        port: 8761
    
    eureka:
        client:
        register-with-eureka: false
        fetch-registry: false

and to our **blog-post** service application.yml file...

    eureka:
        client:
            service-url:
                defaultZone: http://localhost:8761/eureka
        instance:
            instance-id: ${spring.application.name}:${random.uuid}

We also **"@EnableEurekaClient"** to our **"blog-post"** service...

    @EnableEurekaClient
    @SpringBootApplication
    public class BlogPostApplication {
        public static void main(String[] args) {
            SpringApplication.run(BlogPostApplication.class, args);
        }
    }

> You can run the two services and have a look on http://localhost:8761/eureka , if the registry is working.

## API Gateway

After we created our **"blog-post"** service and **"eureka-server"**, we'll create a gateway to unify our ports.
For that we choose the port http://localhost:8765 ....

### Dependencies

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

> Note: You can also add some tools like lombok or devtools to each service...

### Configuration
Add some properties to our <mark>**application.yml**</mark> file...

    server:
        port: 8765

    spring:
        cloud:
            gateway:
                discovery:
                    locator:
                        enabled: true
                routes:
                    - id: BlogPostController
                      uri: lb://blog-post
                      predicates:
                        - Path=/api/v1/blogpost**
                        - Path=/api/v1/blogpost/**
    eureka:
        client:
            service-url:
                defaultZone: http://localhost:8761/eureka

Make sure that you also added **"@EnableEurekaClient"** ....

    @EnableEurekaClient
    @SpringBootApplication
    public class ApiGatewayApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(ApiGatewayApplication.class, args);
        }
    
    }

> Now we can reach our blog-post service on http://localhost:8765/api/v1/blogpost

## Config Server

To keep our <mark>**application.yml**</mark> files well organized, we use a **Config-Server**. By adding a **Config Server** to
our **Microservice**, our services will fetch the needed **application.yml** files to they service.

### Dependencies
We only need one dependency here...

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>

### Main-Class
Don't forget to add **"@EnableConfigServer"** to the Main-Class

    @EnableConfigServer
    @SpringBootApplication
    public class ConfigServerApplication {
        public static void main(String[] args) {
            SpringApplication.run(ConfigServerApplication.class, args);
        }
    }

### Config Folder
Create a **config** folder in the **resources** folder and refactor the "application.yml" files to this folder. 
Make sure you **rename** the application.yml files and the name matches the service like ->  **api-gateway.yml**

### More Dependencies for our Services
Add the following dependency to our services (blog-post,eureka and api-gateway)...

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>

## Docker Compose

### In the end, we will use a <mark>docker-compose.yml</mark> to containerize our application

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