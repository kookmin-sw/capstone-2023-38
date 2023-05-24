package login.Controller;


//import login.oauthtest4.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

//    private final UserService userService;

    @GetMapping("/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }

//    @GetMapping("/")
//    public String index(){
//        return "index";
//    }
//
    @GetMapping("/test")
    public String user(){
        return "test";
    }

}
