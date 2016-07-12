package wepaht.SQLTasker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import wepaht.SQLTasker.interceptor.AttributeInterceptor;

@Configuration
public class AppConfiguration extends WebMvcConfigurerAdapter {
    
    @Bean
    public AttributeInterceptor navigationInterceptor() {
        return new AttributeInterceptor();
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(navigationInterceptor());
    }
}
