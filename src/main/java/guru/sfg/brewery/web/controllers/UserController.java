package guru.sfg.brewery.web.controllers;

import org.jboss.jandex.Main;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/user")
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository      userRepository;
    private final GoogleAuthenticator googleAuthenticator;

    @GetMapping("/register2fa")
    public String register2fa(Model model) {
        User user = getUser();

        String url = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "SFG",
                user.getUsername(),
                googleAuthenticator.createCredentials(user.getUsername()));

        log.debug("Google QR URL: " + url);

        model.addAttribute("googleurl", url);
        return "user/register2fa";
    }

    @PostMapping("/register2fa")
    public String confirm2FA(@RequestParam Integer verifyCode) {
        log.debug("confirm2FA(" + verifyCode + ")");
        User user = getUser();
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            log.debug("Verify code accepted.");
            User savedUser = userRepository.findById(user.getId()).orElseThrow();
            savedUser.setUseGoogle2FA(true);
            userRepository.save(savedUser);
            return "/index";
        } else {
            log.debug("Bad verifyCode.");
            return "user/register2fa";
        }
    }

    @GetMapping("/verify2fa")
    public String verify2Fa() {
        return "user/verify2fa";
    }

    @PostMapping("/verify2fa")
    public String verifyFa(@RequestParam Integer verifyCode) {
        User user = getUser();
        if (googleAuthenticator.authorizeUser(user.getUsername(), verifyCode)) {
            log.debug("Verify code accepted.");
            user.setGoogle2FARequired(false);
            return "/index";
        } else {
            log.debug("Bad verifyCode.");
            return "user/verify2fa";
        }
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    public static final void main(String[] argv) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        System.out.println(googleAuthenticator.getTotpPassword("OTHMVAQ7SUQJJS3DP7FTDCTWYPVS6I64"));
    }
}
