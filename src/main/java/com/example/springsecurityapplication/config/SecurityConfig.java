package com.example.springsecurityapplication.config;

import com.example.springsecurityapplication.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    //метод кот позволяет отключить хэширование паролей
    //    @Bean
    //    public PasswordEncoder getPasswordEncode(){
    //        return NoOpPasswordEncoder.getInstance();
    //    }

    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    //конфигурируем работу SS
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //отключаем защиту от межсайтовой подделки запросов
//        http.csrf().disable()
        http
                //указываем, что все страницы должны быть защищены аут-цией
                .authorizeHttpRequests()
                //с помощью permitAll указываем, что не аут-нные юзеры могут заходить только на перечисленные страницы
                .requestMatchers("/authentication", "/error", "/registration", "/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/product", "/product/info/{id}", "/product/search").permitAll()
                //для всех остальных страниц необходимо вызывать метод authenticated(), кот открывает форму аут-ции
                //.anyRequest().authenticated()
                //указываем что страница /admin доступна тольок юзеру с ролью ADMIN
                .requestMatchers("/admin").hasRole("ADMIN")
                //указываем что остальные страница доступны как юзеру, так и админу
                .anyRequest().hasAnyRole("USER", "ADMIN")
                //указываем что дальше настраивается аут-ция и соединяем ее с настройкой доступа
                .and()
                //указываем какой url будет отправляться при заходе на защищенные страницы
                .formLogin().loginPage("/authentication")
                //указываем на какой адрес будут отправляться данные с формы. Нам уже не нужно будет создавать метод в контроллере и обрабатывать данные с формы. Мы задали url, кот исп-ся по умолчанию для обработки формы аут-ции по средствам SS. SS будет ждать объект с формы аут-ции и затем сверять логин и пароль с данными в БД
                .loginProcessingUrl("/process_login")
                //на этот url необходимо направить юзера после успешной аут-ции. Вторым аргументом указывается true чтобы перенаправление шло в любом случае после успешной аут-ции
                .defaultSuccessUrl("/personal_account", true)
                //куда перенаправлять юзера после проваленной аут-ции. В запросе будет передан объект error, который будет проверяться на форме и при наличии данного объекта в запросе выводится сообщение "Неправильный логин или пароль"
                .failureUrl("/authentication?error")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        // отключить CSRF защиту для эндпоинта
//        http
//                .cors().and()
//                .csrf()
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .ignoringRequestMatchers("/logout");

        return http.build();
    };

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }


//    private final AuthenticationProvider authenticationProvider;

//    public SecurityConfig(AuthenticationProvider authenticationProvider) {
//        this.authenticationProvider = authenticationProvider;
//    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(personDetailsService)
                .passwordEncoder(getPasswordEncode());
    }
}
