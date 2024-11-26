package cn.nolaurene.cms.intercepter;

import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import io.swagger.v3.oas.models.OpenAPI;

import javax.annotation.Resource;

@Configuration
public class BizWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${image.path-prefix}")
    private String imageStoragePath;

    @Resource
    private LoginInterceptor loginInterceptor;

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Case Management System")
                        .description("This is a simple case management system.")
                        .version("1.8.0"));
    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/index.html",
                        "/user/login",
                        "/user/register",
                        "/maintenance/**",
                        "/static/**",
                        "/favicon.ico",
                        "/resources/**",
                        "/webjars/**",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.svg",
                        "/**/*.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error/**",
                        "/v2/**"
                );
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加对静态资源的处理
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/static/images/**")
                .addResourceLocations("file:" + imageStoragePath + "/");
        // swagger and springdoc
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}