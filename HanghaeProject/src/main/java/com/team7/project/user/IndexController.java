package com.team7.project.user;

import com.team7.project.security.oauth.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RequiredArgsConstructor
@Controller
public class IndexController {

    //    private final PostsService postsService;
    private final HttpSession httpSession;

//    @GetMapping("/")
//    public String index(Model model) {
////        model.addAttribute("posts",postsService.findAllDesc());
//        // userName을 사용할 수 있게 model에 저장
//        SessionUser user = (SessionUser) httpSession.getAttribute("user");
//        if (user != null) {
//            model.addAttribute("userName", user.getName());
//            model.addAttribute("userPicture", user.getPicture());
//        }
//        return "index";
//    }

}

