# uService Example

This code repository is trying to formula a working example of Microservice architecture from front-end to back-end. 

As menttioned in [microservices.io](http://microservices.io/patterns/apigateway.html). API Gateway pattern should be applied to mediate the front-end application and back-end microservices.

![Alt text](http://microservices.io/i/apigateway.jpg "API Gateway Pattern in Microosevice")

and to facilitate the delegation between microservices, JWT (Json Web Token) has been used.

As quote from [Suresh Varman K] (https://www.cronj.com/blog/happy-building-microservices/) on "Happy building with Microservices" it depicted how things should put together.

![Alt text](https://www.cronj.com/blog/wp-content/uploads/2017/01/Microservices-1.png "Microservice API Gateway")

1. The user request for Shopping cart service, the API gateway validated the user using the stored session.
2. Redirects the user to the Auth service, asking to login, and validates the login.
3. Response to the API gateway callback function with JWT.
4. The session will be created for the particular user.
5. Redirects user to the shopping cart, and before requesting to Shopping cart service, the gateway will add the user information to the header so the shopping cart service knows the user details, which can be further used for communication between the services by passing the JWT.

