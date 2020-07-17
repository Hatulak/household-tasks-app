package pl.householdtasks.backend.services.listeners;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.householdtasks.backend.model.VerificationToken;
import pl.householdtasks.backend.services.MailService;
import pl.householdtasks.backend.services.UserService;
import pl.householdtasks.backend.services.events.OnResendVerificationTokenEvent;

import javax.mail.MessagingException;

@Component
public class ResendVerificationTokenListener implements ApplicationListener<OnResendVerificationTokenEvent> {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;


    @SneakyThrows
    @Override
    public void onApplicationEvent(OnResendVerificationTokenEvent onResendVerificationTokenEvent) {
        this.sendMailWithConfirmationLink(onResendVerificationTokenEvent);
    }

    private void sendMailWithConfirmationLink(OnResendVerificationTokenEvent event) throws MessagingException {
        VerificationToken token = event.getToken();

        String recipentAddress = token.getUser().getEmail();
        String subject = "New Registration Confirmation";
        String confirmationUrl = "New token: " + event.getUrl() + "/registrationConfirm?token=" + token.getToken();
        mailService.sendMail(recipentAddress, subject, confirmationUrl, false);

    }
}
