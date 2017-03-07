# uService Example

This code repository is trying to formula a working example of Microservice architecture from front-end to back-end. 

As menttioned in [microservices.io](http://microservices.io/patterns/apigateway.html). API Gateway pattern should be applied to mediate the front-end application and back-end microservices.

![Alt text](http://microservices.io/i/apigateway.jpg "API Gateway Pattern in Microosevice")

and to facilitate the delegation between microservices, JWT (Json Web Token) has been used.

## uService Autenticateion and Authorization Flow

As quote from [Suresh Varman K] (https://www.cronj.com/blog/happy-building-microservices/) on "Happy building with Microservices" it depicted how things should put together.
![Alt text](https://www.cronj.com/blog/wp-content/uploads/2017/01/Microservices-1.png "Microservice API Gateway")

1. The user request for Shopping cart service, the API gateway validated the user using the stored session.
2. Redirects the user to the Auth service, asking to login, and validates the login.
3. Response to the API gateway callback function with JWT.
4. The session will be created for the particular user.
5. Redirects user to the shopping cart, and before requesting to Shopping cart service, the gateway will add the user information to the header so the shopping cart service knows the user details, which can be further used for communication between the services by passing the JWT.

### Securing uServices with JWT tokens
In the Swagger folder of this repository it provided an example on how to secure your Spring boot uService genereated by Swagger using spring boot security and [jjwt](https://github.com/jwtk/jjwt).

#### Adding dependancies
First of all you will need to add the following dependnacies in your pom.xml of your Swagger generated spring boot project.
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.6.0</version>
</dependency>
```
Next the `WebSecurityConfig` class is created to define the secuirty confgiruation (e.g. which path should be protected by which method.

In the example,`WebSecurityConfig` is located in package `io.swagger.configuration.security`.

Three classes `JWTAuthenticationFilter`, `TokenAuthenticationService` and `AuthenticatedUser` in package `io.swagger.configuration.security.jwt` are also created to facilitate the validation and authroization of microserice via JWT.


