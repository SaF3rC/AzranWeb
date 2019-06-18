package me.invakid.azran.controller;

import me.invakid.azran.entity.*;
import me.invakid.azran.service.AzranService;
import me.invakid.azran.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Optional;

@Controller
public class AzranController {

    private final AzranService service;
    private final UserService userService;
    private final PayPalClient payPalClient;

    @Autowired
    public AzranController(PayPalClient payPalClient, AzranService service, UserService userService) {
        this.payPalClient = payPalClient;
        this.service = service;
        this.userService = userService;
    }

    @ModelAttribute
    public void setVaryResponseHeader(HttpServletResponse response) {
        response.setHeader("X-XSS-Protection", "1");
        response.setDateHeader("Expires", 0);
    }

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("Videos", userService.getVideos());
        return "index";
    }

    @RequestMapping(value = "/watch/{id}", method = RequestMethod.GET)
    public String download(@PathVariable int id, Model model, HttpServletRequest request) {
        Videos video = userService.getVideo(id);

        if (video == null) {

            return "redirect:/";
        }

        model.addAttribute("video", video);

        return "watch";
    }

    @RequestMapping(value = "/wrong-token", method = RequestMethod.GET)
    public String wrongTokenMapping() {
        return "wrong-token";
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.GET)
    public String downloadHandler() {
        return "download";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(HttpServletRequest request, HttpSession session, Model model) {
        if (session.getAttribute("isAdmin") != null && session.getAttribute("isAdmin").toString().equalsIgnoreCase("true")) {
            model.addAttribute("isAdmin", true);
            return "admin";
        }
        return "admin";
    }

    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public String modal(Model model, HttpSession session, @RequestParam("action") String action, @RequestParam(value = "count") Optional<Integer> count, @RequestParam(value = "user") Optional<String> user) {
        if (session.getAttribute("isAdmin") != null && session.getAttribute("isAdmin").toString().equalsIgnoreCase("true")) {
            if (action.equalsIgnoreCase("latest")) {
                model.addAttribute("detects", true);
                ArrayList<Detections> decs = service.getDetections(count.isPresent() ? count.get() : 10);
                model.addAttribute("Detections", decs);
                return "output";
            } else if (action.equalsIgnoreCase("clients")) {
                model.addAttribute("clients", true);
                ArrayList<Client> clients = service.getClients(count.isPresent() ? count.get() : 10);
                model.addAttribute("Client", clients);
                return "output";
            } else if (action.equalsIgnoreCase("expclients")) {
                model.addAttribute("expclients", true);
                ArrayList<Client> clients = service.getExpireableClients(count.isPresent() ? count.get() : 10);
                model.addAttribute("expClient", clients);
                return "output";
            } else if (action.equalsIgnoreCase("remove")) {
                user.ifPresent(s -> service.removeClient(s));
                return "error";
            } else if (action.equalsIgnoreCase("add")) {
                if (user.isPresent())
                    if (count.isPresent()) {
                        service.addClientsFor30Days(user.get());
                    } else {
                        service.addClient(user.get());
                    }
                return "error";
            }
            return "error";
        }
        return "error";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public String downloadAdmin(@RequestParam("password") String password, HttpSession session, Model model) {
        if (service.isPwValid(password)) {
            String username = service.getUsername(password);
            service.removePw(password);
            session.setAttribute("username", username);
            session.setAttribute("isAdmin", true);
            model.addAttribute("isAdmin", true);
            return "admin";
        }
        model.addAttribute("error", new CustError("Invalid password!"));
        return "admin";
    }

    @PostMapping(value = "/{token}")
    public ResponseEntity<Object> downloadPost(@PathVariable("token") String token) throws Exception {
        if (service.isTokenValid(token)) {
            service.removeToken(token);

            File file = new File("Azran.exe");

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentDispositionFormData("attachment", "Azran.exe");
            respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            respHeaders.add("content-length", file.length() + "");
            InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
        }

        HttpHeaders errorHeaders = new HttpHeaders();
        errorHeaders.add("Location", "/wrong-token");

        return new ResponseEntity<>(errorHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping(value = "/favicon.ico")
    public ResponseEntity<InputStreamResource> download() throws Exception {
        File file = new File("favicon.ico");

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentDispositionFormData("attachment", "favicon.ico");

        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }

    @GetMapping("/generate")
    public String register(@RequestParam String ticketId, @RequestParam String ticketName, @RequestParam String price, Model model) {
        model.addAttribute("url", payPalClient.createPayment(price, ticketId, ticketName));
        service.addPayment(ticketId, ticketName, price);
        return "url";
    }

    @GetMapping(value = "/complete/payment/{ticketId}")
    public String completePayment(HttpServletRequest request, @PathVariable String ticketId, @RequestParam("ticketName") String ticketName) {
        String output = payPalClient.completePayment(request);
        if (output.startsWith("success")) {
            String[] split = output.split(":::");
            String email = split[1];
            String price = split[2];

            service.updatePayment(ticketId, email, price);

        }
        return "redirect:/";
    }

}
