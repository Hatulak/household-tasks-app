package pl.householdtasks.backend.services.listeners;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.householdtasks.backend.model.User;
import pl.householdtasks.backend.services.MailService;
import pl.householdtasks.backend.services.UserService;
import pl.householdtasks.backend.services.events.OnRegistrationCompleteEvent;

import javax.mail.MessagingException;
import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;


    @SneakyThrows
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent onRegistrationCompleteEvent) {
        this.sendMailWithConfirmationLink(onRegistrationCompleteEvent);
    }

    private void sendMailWithConfirmationLink(OnRegistrationCompleteEvent event) throws MessagingException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipentAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getUrl() + "/regitrationConfirm?token=" + token;
        System.out.println("sending email ?");
        mailService.sendMail(recipentAddress, subject, confirmationUrl, false);

    }
}
