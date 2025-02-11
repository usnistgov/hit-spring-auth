package gov.nist.hit.auth.demo.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class ForwardingController {

    @RequestMapping(value = {"/{path:(?!resources$)[^.]*}/**", "/"})
    public String redirect() {
        return "forward:/resources/index.html";
    }
}