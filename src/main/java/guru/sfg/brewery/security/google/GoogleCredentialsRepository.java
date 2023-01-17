package guru.sfg.brewery.security.google;

import java.util.List;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.ICredentialRepository;

import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleCredentialsRepository implements ICredentialRepository {

    private final UserRepository userRepository;
    
    @Override
    public String getSecretKey(String userName) {
        log.debug("getSecretKey("+userName+")");
        User user = userRepository.findByUsername(userName).orElseThrow();
        return user.getGoogle2FASecret();
    }

    @Override
    public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
        log.debug("saveUserCredentials("+userName+","+secretKey+","+validationCode+","+scratchCodes+")");
        User user = userRepository.findByUsername(userName).orElseThrow();
        user.setGoogle2FASecret(secretKey);
        user.setUseGoogle2FA(true);
        userRepository.save(user);
    }

}
