package pl.householdtasks.backend.services.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import pl.householdtasks.backend.model.User;

@Setter
@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String url;
    private User user;

    public OnRegistrationCompleteEvent(String url, User user) {
        super(user);

        this.user = user;
        this.url = url;
    }
}
