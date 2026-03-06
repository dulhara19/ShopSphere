# User Service Docs

## OAuth2 Integration (Phase 5.1)

This service supports Google OAuth2 login. Configuration is in `application.yml` under `spring.security.oauth2.client.registration.google`.

### Flow Overview

1. User initiates login at `/oauth2/authorization/google`.
2. Google redirects to `/login/oauth2/code/google`.
3. `CustomOAuth2UserService` creates or updates the user by email (unique identifier).
4. `OAuth2AuthenticationSuccessHandler` issues a JWT and redirects to:
   `http://localhost:3000/login-success?token={jwt}`.

### Local Verification

Start the service and browse to:

```
http://localhost:3001/oauth2/authorization/google
```

After successful login, you should be redirected with a JWT token in the URL.

