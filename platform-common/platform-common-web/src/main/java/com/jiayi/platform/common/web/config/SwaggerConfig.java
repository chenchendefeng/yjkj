package com.jiayi.platform.common.web.config;

import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@ConfigurationProperties("swagger")
public class SwaggerConfig {

    private String title;
    private String description;
    private String version;
    private String email;
    private String author;

//    @Bean
//    public Docket createRestApi() {
////        return new Docket(DocumentationType.SWAGGER_2).apiInfo(
////                new ApiInfoBuilder().title(title).description(description)
////                        .version(version).contact(new Contact(author, null, email)).build()
////        ).select().apis(RequestHandlerSelectors.basePackage("com.jiayi.platform.collision"))
////                .paths(PathSelectors.any()).build();
//
//        ParameterBuilder tokenPar = new ParameterBuilder();
//        List<Parameter> pars = new ArrayList<Parameter>();
//        tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
//        pars.add(tokenPar.build());
//
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(
//                new ApiInfoBuilder().title(title).description(description)
//                        .version(version).contact(new Contact(author, null, email)).build()
//        ).globalOperationParameters(pars).select().apis(RequestHandlerSelectors.basePackage("com.jiayi.platform.collision"))
//                .paths(PathSelectors.any()).build();
//    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).
                useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("^(?!auth).*$"))
                .build().apiInfo(apiInfo())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts())
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().build();
    }


    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeys=  Lists.newArrayList();
        apiKeys.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeys;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts=  Lists.newArrayList();
        securityContexts.add( SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        List<SecurityReference> securityReferences=  Lists.newArrayList();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }
}
