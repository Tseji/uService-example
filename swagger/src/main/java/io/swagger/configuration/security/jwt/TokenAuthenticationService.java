package io.swagger.configuration.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;



public class TokenAuthenticationService {

    private String secret = "VGhpc0lzQVNlY3JldA=="; // base64 encoded "ThisIsASecret"
    private String headerString = "Authorization";
 

    public Authentication getAuthentication(HttpServletRequest request)  
    {
    	   	
    	
        String token = request.getHeader(headerString);
        if(token != null)
        {
        	
        	System.out.println("Token Received:"+token);
        	
            // parse the token.
            String username = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();
            
            System.out.println(username);
            
            Jws<Claims> claims = Jwts.parser()
            		  .setSigningKey(secret)
            		  .parseClaimsJws(token);
            
            		String scope = (String) claims.getBody().get("scope");
            		//System.out.println(scope);
            		//assertEquals(scope, "self groups/admins");
            
             Date exp = Jwts.parser()
          		  .setSigningKey(secret)
          		  .parseClaimsJws(token).getBody().getExpiration();
             
                 // System.out.println("Expiry Date:" + exp);
            
            if(username != null) // we managed to retrieve a user
            {
                return new AuthenticatedUser(username);
            }
        }
        return null;
    }
}