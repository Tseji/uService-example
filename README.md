# uService Example

This code repository is trying to formulate a working example of Microservice architecture from front-end to back-end. 

As mentioned in [microservices.io](http://microservices.io/patterns/apigateway.html). API Gateway pattern should be applied to mediate the front-end application and back-end microservices.

![Alt text](http://microservices.io/i/apigateway.jpg "API Gateway Pattern in Microsevice")

and to facilitate the delegation between microservices, JWT (Json Web Token) has been used.

## uService Authentication and Authorization Flow

As quote from [Suresh Varman K] (https://www.cronj.com/blog/happy-building-microservices/) on "Happy building with Microservices" it depicted how things should put together.
![Alt text](https://www.cronj.com/blog/wp-content/uploads/2017/01/Microservices-1.png "Microservice API Gateway")

1. The user request for Shopping cart service, the API gateway validated the user using the stored session.
2. Redirects the user to the Auth service, asking to login, and validates the login.
3. Response to the API gateway callback function with JWT.
4. The session will be created for the particular user.
5. Redirects user to the shopping cart, and before requesting to Shopping cart service, the gateway will add the user information to the header so the shopping cart service knows the user details, which can be further used for communication between the services by passing the JWT.

## Frontend application with AngularJS

We want to build a small Angular application that is secured by a login page and where access is restricted to registered users only. Therefore every request which goes to the Angular application should be checked if it comes from a registered user or not. If a request does not come from a registered user, the request will be redirected to the login page where a user can register.

To interact with Keycloak from our AngularJS application, Keycloak is providing a JavaScript-Adapter directly on the Keycloak server. This adapter will be used to check if a request is authenticated and can be integrated in our application by including the JavaScript file into our html page:
```
<script src="//sso-esquel-test.eastasia.cloudapp.azure.com/auth/js/keycloak.js"></script>
```
Futhermore we have to configure the KeyCloak adapter to ensure it knows who our application is and where to find the Keycloak server. We can provide this information as a json file which can be downloaded directly from the Keycloak server.

To get the json file, open the Keycloak administration console and navigate to the frontend client page. Then open the “Installation” tab and choose “Keycloak OIDC JSON” as format. Then download the JSON file and store it in the angular application as /keycloak/keycloak.json.

One important thing to know is because we are only allowing registered users to have access to the application we have to manually bootstrap AngularJS and we cannot rely on the automatic bootstrapping with the ng-app directive.

The following code demonstrates how to authenticate a request and bootstrap angular only if the request comes from a registered user:
```
// on every request, authenticate user first
angular.element(document).ready(() => {
    window._keycloak = Keycloak('keycloak/keycloak.json');

    window._keycloak.init({
        onLoad: 'login-required'
    })
    .success((authenticated) => {
        if(authenticated) {
            window._keycloak.loadUserProfile().success(function(profile){
                angular.bootstrap(document, ['keycloak-tutorial']); // manually bootstrap Angular
            });
        }
        else {
            window.location.reload();
        }
    })
    .error(function () {
        window.location.reload();
    });
});
```
If an unregistered user opens the application he will be automatically redirected to the Keycloak login page. In the application we can access user information like login name by getting the user profile with loadUserProfile().

That is basically all what is needed to secure our Angular application. To ensure the user information is transmitted to the backend we also have to add the users access token to the request header while calling a backend REST service. This can be done like this application wide:
```
// use bearer token when calling backend
app.config(['$httpProvider', function($httpProvider) {
    var token = window._keycloak.token;     
    $httpProvider.defaults.headers.common['Authorization'] = 'BEARER ' + token;
}]);
```

## Backend application with Spring Boot

The backend application should be secured against unauthorized access. Therefore, like in the frontend, only requests coming from registered users should be accepted.

First, it is important to add the maven Keycloak dependencies for Tomcat and Spring Boot:
```
<dependency>
            <groupId>org.keycloak</groupId>
            <artifactId>keycloak-tomcat8-adapter</artifactId>
            <version>${keycloak.version}</version>
        </dependency>
        <dependency>
            <groupId>org.keycloak</groupId>
            <artifactId>keycloak-spring-boot-adapter</artifactId>
            <version>${keycloak.version}</version>
        </dependency>
```		
		
Then we have to configure keycloak just like we did in the frontend application. This time the configuration is done the Spring Boot way in the application.properties file:

```
keycloak.realm = Demo-Realm
keycloak.realmKey = MI...
keycloak.auth-server-url = http://sso-esquel-test.eastasia.cloudapp.azure.com/auth
keycloak.ssl-required = external
keycloak.resource = tutorial-backend
keycloak.bearer-only = true
keycloak.credentials.secret = e12cdacf-0d79-4945-a57a-573a833c1acc
```
The values can be retrieved from the “Installation” tab in the administration console of Keycloak for the backend client. One important thing here is to not forget the secret. The secret can be retrieved from the “Credentials” tab of the backend client.



To secure the REST API endpoints a few other entries in the application.properties file are important:

```
keycloak.securityConstraints[0].securityCollections[0].name = spring secured api
keycloak.securityConstraints[0].authRoles[0] = admin
keycloak.securityConstraints[0].authRoles[1] = manager
keycloak.securityConstraints[0].securityCollections[0].patterns[0] = /api/*
```

The patterns property defines the pattern of the API endpoints with * acting as wildcard. That means every endpoint under api like /api/contracts or /api/users is protected by Keycloak. Every other endpoint that is not explicitly listed is NOT secured by Keycloak and is publicly available.

The authRoles property defines which Keycloak roles are allowed to access the defined endpoints.

If everything is configured correctly the Keycloak adapter for Spring Boot should intercept incoming request automatically and reject unauthorized requests.



To access detailed user information in the backend we can use the KeycloakPrincipal class from the Keycloak-SpringBoot adapter. The KeycloakPrincipal will automatically be injected by Spring if used in a REST controller class as method parameter. Detailed user information can then be retrieved by using the AccessToken like this example in a REST controller:

```
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public void getUserInformation(KeycloakPrincipal<RefreshableKeycloakSecurityContext> principal) {
        AccessToken token = principal.getKeycloakSecurityContext().getToken();
        
        String id = token.getId();
        String firstName = token.getGivenName();
        String lastName = token.getFamilyName();

        // ...
    }
```	
In the full example you will see that I have build a MethodArgument Resolver to avoid having to deal with the Principal in the REST controllers but that is absolutely not necessary and just for convenience.

## Demo

 Use maven to start both applications with the following command:

 ```
mvn spring-boot:run
```

Then navigate to http://localhost:8000 and you should find yourself landing on the Keycloak login page. 

You can use default accounts to login:

admin:admin
manager:manager
flash:flash

You can also register yourself as a new user. After registering you will be redirected back to the Angular application and should see some details about your user.

Please note that currently your user is not associated to any role defined earlier. That means accessing the backend is impossible because we have only allowed managers and admins to access the backend. To give your user access to the backend we have to map your user to a role.

To do this, open the Keycloak admin console (http://sso-esquel-test.eastasia.cloudapp.azure.com/auth/admin/Demo-Realm/console) and navigate to “Users”. Then click on the button “View all users” and click on your username. After that navigate to the “Role Mappings” tab and assign the role “manager” to your user.


Now open up the Angular app again and you should see a “Call backend service” button that only managers can see. Click on it and some contracts from the backend should be returned together with your user information which comes also from the backend.

This proves that the frontend and the backend is correctly secured by Keycloak.
