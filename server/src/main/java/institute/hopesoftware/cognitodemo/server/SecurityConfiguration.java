package institute.hopesoftware.cognitodemo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug=true)
public class SecurityConfiguration {

    private final String issuerUri;
    private final UserAuthenticationFilter userAuthenticationFilter;

    @Autowired
    public SecurityConfiguration(@Value("${cognito.issuer-uri}") String issuerUri,
                                 UserAuthenticationFilter userAuthenticationFilter) {
        this.issuerUri = issuerUri;
        this.userAuthenticationFilter = userAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                // .addFilterAfter(userAuthenticationFilter, 
                // BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests((httpRequestsAuthorizer) -> 
                     httpRequestsAuthorizer
                         .requestMatchers(HttpMethod.GET, "/faq").permitAll()
                         .requestMatchers("/any/other/public/apis").permitAll()
                         .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oAuth2ResourceServerConfigurerCustomizer())
                .build();
    }

    private Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> oAuth2ResourceServerConfigurerCustomizer() {
        final JwtDecoder decoder = JwtDecoders.fromIssuerLocation(issuerUri);
        return (resourceServerConfigurer) -> resourceServerConfigurer.jwt(
                jwtConfigurer -> jwtConfigurer.decoder(decoder)
        );
    }
}
