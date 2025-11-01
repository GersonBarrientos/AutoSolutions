package com.autosolutions.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// +++ IMPORTACIONES AÑADIDAS +++
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configuración de recursos estáticos (CSS, JS, imágenes)
     * Esto asegura que Spring Boot sirva correctamente los archivos desde /static/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(3600); // Cache de 1 hora en desarrollo
        
        // JavaScript
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600);
        
        // Imágenes
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600);
        
        // Webjars (Bootstrap, jQuery, etc. si los usas)
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(3600);
    }

    /**
     * Interceptor para verificar que el usuario haya pasado por Home antes de acceder a otras páginas
     */
    static class HomeVisitedInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
            String path = req.getRequestURI();

            // Rutas públicas que NO requieren haber pasado por Home
            if (path.equals("/") ||
                path.startsWith("/login") ||
                path.startsWith("/logout") ||
                path.startsWith("/error") ||
                path.startsWith("/css/") || 
                path.startsWith("/js/") ||
                path.startsWith("/images/") || 
                path.startsWith("/webjars/") ||
                path.equals("/favicon.ico")) {
                return true;
            }

            // Si no está autenticado, deja que Security lo maneje
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
                return true;
            }

            // Si está autenticado, exige haber pasado por Home
            HttpSession session = req.getSession(false);
            boolean visitedHome = (session != null) && Boolean.TRUE.equals(session.getAttribute("visitedHome"));
            if (!visitedHome) {
                res.sendRedirect("/");
                return false;
            }

            return true;
        }
    }

    /**
     * Registra el interceptor para todas las rutas
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HomeVisitedInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**", 
                        "/js/**", 
                        "/images/**", 
                        "/webjars/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    // +++ BEAN AÑADIDO PARA SOPORTE DE _method (PUT/DELETE) +++
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}