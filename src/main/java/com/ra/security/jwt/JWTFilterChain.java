package com.ra.security.jwt;

import com.ra.security.AccountDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Lớp Filter kiểm tra JWT Token trong mỗi request
 * - Lấy token từ Header "Authorization"
 * - Kiểm tra tính hợp lệ của token
 * - Nếu hợp lệ, đặt thông tin user vào SecurityContext
 */
@Component

public class JWTFilterChain extends OncePerRequestFilter {
    @Autowired
    private  JWTProvider jwtProvider;
    @Autowired
    private  AccountDetailService accountDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            //  1. Lấy token từ request
            String token = getTokenFromRequest(request);

            //  2. Kiểm tra token hợp lệ
            if (token != null && jwtProvider.validateToken(token)) {
                //  3. Lấy username từ token
                String username = jwtProvider.getUsernameFromToken(token);

                //  4. Nếu chưa có Authentication trong SecurityContext
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = accountDetailService.loadUserByUsername(username);

                    // 🔹 5. Tạo đối tượng Authentication
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // 🔹 6. Thêm thông tin request vào authentication
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 🔹 7. Đưa vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xử lý JWT Filter: " + e.getMessage());
        }

        // 🔹 8. Cho request đi tiếp
        filterChain.doFilter(request, response);
    }

    /**
     * Lấy token từ Header Authorization
     * Ví dụ: Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}
