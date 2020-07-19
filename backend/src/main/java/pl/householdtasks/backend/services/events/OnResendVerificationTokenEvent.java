package pl.householdtasks.backend.services.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import pl.householdtasks.backend.model.VerificationToken;

@Setter
@Getter
public class OnResendVerificationTokenEvent extends ApplicationEvent {

    private String url;
    private VerificationToken token;

    public OnResendVerificationTokenEvent(String url, VerificationToken token) {
        super(token);

        this.token = token;
        this.url = url;
    }
}
