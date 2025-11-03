package com.ra.security;

import com.ra.security.jwt.JWTEntryPoint;
import com.ra.security.jwt.JWTFilterChain;
import com.ra.security.jwt.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    @Lazy
    private  JWTFilterChain jwtFilterChain;
    @Autowired
    private  JWTEntryPoint jwtEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/accounts/login").permitAll();
                    auth.requestMatchers("/api/v1/accounts/register").hasRole("ADMIN");
                    auth.anyRequest().authenticated();


                })
                /*Vì JWT không dùng session (token nằm ở client),
nên bạn phải đặt chế độ là stateless — Spring Security sẽ không lưu session user.

Mỗi request đều được xác thực độc lập bằng token.*/
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*CSRF chỉ dùng cho ứng dụng có session (web form truyền thống).

Với REST API + JWT → bạn bắt buộc tắt vì token đã bảo vệ rồi.*/
                .csrf(AbstractHttpConfigurer::disable)
                /*Đặt jwtFilterChain chạy trước filter mặc định UsernamePasswordAuthenticationFilter
(vì JWT sẽ xử lý xác thực trước khi Spring xử lý username/password login).*/
                .addFilterBefore(jwtFilterChain, UsernamePasswordAuthenticationFilter.class)
                /*Khi có lỗi xác thực (token sai, thiếu, hết hạn, v.v.),
Spring sẽ tự động gọi jwtEntryPoint → trả JSON { "code": 401, "error": "Unauthorized" }.*/
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(jwtEntryPoint));
        return http.build();
    }


}
