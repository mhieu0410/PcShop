package pcshop.duancanhan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/admin/**") // Disable CSRF cho admin endpoints
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cart/**", "/order/**", "/build-pc").authenticated()
                        .requestMatchers("/register", "/login").permitAll() // Cho phép truy cập đăng ký và đăng nhập
                        .anyRequest().permitAll()
                )
                .userDetailsService(userDetailsService) // Thêm dòng này
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)

                        .successHandler((request, response, authentication) -> {
                            System.out.println("=== DEBUG: Login SUCCESS for user: " + authentication.getName() + " ===");
                            System.out.println("=== DEBUG: User authorities: " + authentication.getAuthorities() + " ===");
                            
                            // Kiểm tra role của user
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                System.out.println("=== DEBUG: Redirecting ADMIN to /admin/dashboard ===");
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                System.out.println("=== DEBUG: Redirecting USER to / ===");
                                response.sendRedirect("/");
                            }
                        })
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}