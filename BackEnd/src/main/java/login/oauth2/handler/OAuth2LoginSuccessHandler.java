package login.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.oauth2.CustomOAuth2User;
import login.user.Role;
import login.user.repository.UserRepository;
import login.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login Success!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            oAuth2User.setRole(Role.USER);
            loginSuccess(response, oAuth2User);

            userRepository.findByEmail(oAuth2User.getEmail())
                    .ifPresentOrElse(
                            user -> response.addHeader("recogid", user.getRecogID()),
                            () -> new Exception("Can Not Found.")
                    );
            String url ="http://192.168.200.194:3000/main";
            String Authorization = response.getHeader("Authorization");
            String recogid = response.getHeader("recogid");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.sendRedirect(url + "?authorization=" + Authorization + "&recogid=" + recogid);
          //  response.sendRedirect("/");
            System.out.println("login success!!!");
        } catch (Exception e) {
            throw e;
        }

    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
       // return jwtService.extractEmail(accessToken).toString();
    }
}
