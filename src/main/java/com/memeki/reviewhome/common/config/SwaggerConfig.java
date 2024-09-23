package com.memeki.reviewhome.common.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.memeki.reviewhome.global.security.oauth2.UserPrincipal;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                // .securityContexts(Arrays.asList(securityContext()))
                // .securitySchemes(Arrays.asList(basicAuthScheme()))
                .ignoredParameterTypes(UserPrincipal.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.memeki.reviewhome"))
                .paths(PathSelectors.any())
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(basicAuthReference()))
                .build();
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    private SecurityReference basicAuthReference() {
        return new SecurityReference("basicAuth", new AuthorizationScope[0]);
    }

    @Bean
    public ParameterBuilderPlugin authenticationPrincipalHider() {
        return new ParameterBuilderPlugin() {
            @Override
            public void apply(ParameterContext parameterContext) {
                if (parameterContext.resolvedMethodParameter().hasParameterAnnotation(AuthenticationPrincipal.class) ||
                        parameterContext.resolvedMethodParameter().getParameterType().equals(UserPrincipal.class)) {
                    parameterContext.parameterBuilder().hidden(true);
                }
            }

            @Override
            public boolean supports(DocumentationType documentationType) {
                return true; // Support all documentation types
            }
        };
    }
}
