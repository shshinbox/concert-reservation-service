package me.songha.concert.config;

import me.songha.concert.shared.security.UserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@EnableMongoRepositories(basePackages = "me.songha.concert.shared.mongo")
@EnableAspectJAutoProxy
@EnableJpaAuditing
@Configuration
public class AppConfig implements WebMvcConfigurer {
    private final UserArgumentResolver userArgumentResolver;

    public AppConfig(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}