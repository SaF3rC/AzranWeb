package me.invakid.azran.configurator;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.GzipResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.SessionCookieConfig;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public TomcatServletWebServerFactoryCustomizer customizeTomcatConnector() {
        return new TomcatServletWebServerFactoryCustomizer(new ServerProperties()) {
            @Override
            public void customize(TomcatServletWebServerFactory container) {
                if (container instanceof TomcatServletWebServerFactory) {
                    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
                    connector.setScheme("http");
                    connector.setPort(80);
                    connector.setSecure(false);
                    connector.setRedirectPort(443);
                    container.addAdditionalTomcatConnectors(connector);
                }
            }
        };
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            SessionCookieConfig config = servletContext.getSessionCookieConfig();
            config.setHttpOnly(false);
            config.setMaxAge(31556926);
            config.setSecure(true);
        };

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/css/**")
                .addResourceLocations("classpath:/css/")
                .setCacheControl(CacheControl.maxAge(360, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/img/**")
                .addResourceLocations("classpath:/img/")
                .setCacheControl(CacheControl.maxAge(360, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/videos/**")
                .addResourceLocations("classpath:/videos/")
                .setCacheControl(CacheControl.maxAge(360, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
        registry
                .addResourceHandler("/incl/**")
                .addResourceLocations("classpath:/incl/")
                .setCacheControl(CacheControl.maxAge(360, TimeUnit.HOURS).cachePublic())
                .resourceChain(true)
                .addResolver(new GzipResourceResolver())
                .addResolver(new PathResourceResolver());
    }

}