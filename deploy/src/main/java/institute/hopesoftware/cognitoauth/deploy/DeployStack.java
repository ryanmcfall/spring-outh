package institute.hopesoftware.cognitoauth.deploy;

import software.constructs.Construct;

import static java.util.Arrays.asList;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cognito.AccountRecovery;
import software.amazon.awscdk.services.cognito.AttributeMapping;
import software.amazon.awscdk.services.cognito.AutoVerifiedAttrs;
import software.amazon.awscdk.services.cognito.CognitoDomainOptions;
import software.amazon.awscdk.services.cognito.Mfa;
import software.amazon.awscdk.services.cognito.OAuthFlows;
import software.amazon.awscdk.services.cognito.OAuthScope;
import software.amazon.awscdk.services.cognito.OAuthSettings;
import software.amazon.awscdk.services.cognito.ProviderAttribute;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientIdentityProvider;
import software.amazon.awscdk.services.cognito.UserPoolDomain;
import software.amazon.awscdk.services.cognito.UserPoolDomainOptions;
import software.amazon.awscdk.services.cognito.UserPoolIdentityProviderGoogle;

public class DeployStack extends Stack {
    private UserPool userPool;
    private UserPoolClient userPoolClient;
    private UserPoolDomain userPoolDomain;
    private UserPoolIdentityProviderGoogle userPoolIdentityProviderGoogle;

    public DeployStack(final Construct scope, final String id, String clientId, String clientSecret, final StackProps props) {
        super(scope, id, props);

        userPool = UserPool.Builder.create(this, "UserPool")
            .userPoolName("cognito-auth-user-pool")
            .selfSignUpEnabled(true)
            .signInAliases(SignInAliases.builder().email(true).build())
            .autoVerify(AutoVerifiedAttrs.builder().email(true).build())
            .mfa(Mfa.OFF)
            .accountRecovery(AccountRecovery.EMAIL_ONLY)
            
            .build();

        UserPoolDomainOptions options = UserPoolDomainOptions.builder()
            .cognitoDomain(CognitoDomainOptions.builder().domainPrefix("imhereauthtest").build())
            .build();

        userPool.addDomain("imhereauthtest", options);

        AttributeMapping attributeMapping = AttributeMapping.builder()
            .email(ProviderAttribute.GOOGLE_EMAIL)
            .givenName(ProviderAttribute.GOOGLE_GIVEN_NAME)
            .familyName(ProviderAttribute.GOOGLE_FAMILY_NAME)            
            .build();

        userPoolIdentityProviderGoogle = UserPoolIdentityProviderGoogle.Builder.create(this, "UserPoolIdentityProviderGoogle")
            .userPool(userPool)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .attributeMapping(attributeMapping)
            .scopes(asList("email", "profile", "phone", "openid"))
            .build();

        // userPool.registerIdentityProvider(userPoolIdentityProviderGoogle);  
        
        userPoolClient = UserPoolClient.Builder.create(this, "UserPoolClient")
            .userPool(userPool)
            .userPoolClientName("cognito-auth-user-pool-client")            
            .oAuth(
                OAuthSettings.builder()
                .flows(
                    OAuthFlows.builder()
                    .authorizationCodeGrant(true)
                    .build()
                )
                .scopes(asList(OAuthScope.EMAIL, OAuthScope.PROFILE, OAuthScope.COGNITO_ADMIN))
                .callbackUrls(asList("http://localhost:3000/", "myapp://callback"))
                .logoutUrls(asList("http://localhost:3000/", "myapp://logout"))
                .build()
            )
            //  According to the 
            .supportedIdentityProviders(asList(
                UserPoolClientIdentityProvider.COGNITO, UserPoolClientIdentityProvider.GOOGLE
                )
            )
            .build();

            userPoolClient.getNode().addDependency(userPoolIdentityProviderGoogle);                        
    }
}
