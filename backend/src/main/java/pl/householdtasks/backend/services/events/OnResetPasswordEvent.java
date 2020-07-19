package pl.householdtasks.backend.services.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import pl.householdtasks.backend.model.User;

@Getter
@Setter
public class OnResetPasswordEvent extends ApplicationEvent {

    private String url;
    private User user;

    public OnResetPasswordEvent(String url, User user) {
        super(user);

        this.user = user;
        this.url = url;
    }
}
