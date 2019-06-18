package me.invakid.azran.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AzranErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/";
    }

}
