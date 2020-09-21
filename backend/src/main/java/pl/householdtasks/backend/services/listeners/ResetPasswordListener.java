package pl.householdtasks.backend.services.listeners;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.services.MailService;
import pl.householdtasks.backend.services.UserService;
import pl.householdtasks.backend.services.events.OnResetPasswordEvent;

import javax.mail.MessagingException;
import java.util.UUID;

@Component
public class ResetPasswordListener implements ApplicationListener<OnResetPasswordEvent> {

    private final MailService mailService;

    private final UserService userService;

    @Autowired
    public ResetPasswordListener(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        this.sendEmailWithPasswordResetToken(event);
    }

    private void sendEmailWithPasswordResetToken(OnResetPasswordEvent event) throws MessagingException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);

        String recipentAddress = user.getEmail();
        String subject = "Password Reset Request";
        String confirmationUrl = event.getUrl() + "/changePassword?token=" + token;
        mailService.sendMail(recipentAddress, subject, confirmationUrl, false);
    }
}
