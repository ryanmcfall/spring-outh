package institute.hopesoftware.cognitoauth.deploy;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class DeployApp {
    public static void main(final String[] args) {
        App app = new App();

        String accountId = (String) app.getNode().tryGetContext("accountId");
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalArgumentException("context variable 'accountId' must not be null");
        }
            
        String region = (String) app.getNode().tryGetContext("region");
        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("context variable 'region' must not be null");
        }

        String clientId = (String) app.getNode().tryGetContext("clientId");
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("context variable 'clientId' must not be null");
        }

        String clientSecret = (String) app.getNode().tryGetContext("clientSecret");
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalArgumentException("context variable 'clientSecret' must not be null");
        }

        Environment env = Environment.builder()
                .account(accountId)
                .region(region)
                .build();

        new DeployStack(app, "DeployStack", clientId, clientSecret, StackProps.builder()
                .env(env)
                .build());

        app.synth();
    }
}
