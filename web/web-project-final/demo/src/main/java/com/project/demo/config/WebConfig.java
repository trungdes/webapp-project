package com.project.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.project.demo.model.UserEntity;
import com.project.demo.repository.NotificationRepository;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter("yyyy-MM-dd"));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
                if (modelAndView != null) {
                    HttpSession session = request.getSession();
                    UserEntity user = (UserEntity) session.getAttribute("user");
                    if (user != null) {
                        int unreadUserNotifications = notificationRepository.findByTenantEmailAndIsReadFalse(user.getEmail()).size();
                        modelAndView.addObject("unreadUserNotifications", unreadUserNotifications);
                    }
                }
            }
        });
    }
} 