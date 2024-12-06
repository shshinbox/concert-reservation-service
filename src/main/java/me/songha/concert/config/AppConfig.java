package me.songha.concert.config;

import lombok.RequiredArgsConstructor;
import me.songha.concert.shared.security.UserArgumentResolver;
import me.songha.concert.shared.timezone.TimeZoneInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = "me.songha.concert.shared.mongo")
@EnableAspectJAutoProxy
@EnableJpaAuditing
@Configuration
public class AppConfig implements WebMvcConfigurer {
    private final UserArgumentResolver userArgumentResolver;
    private final TimeZoneInterceptor timeZoneInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeZoneInterceptor);
    }
}