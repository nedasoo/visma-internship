package visma.meetings.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class Participant extends Person{
    private LocalDateTime time;

    public Participant(LocalDateTime time, String name){
        this.setTime(time);
        this.setName(name);
    }
}
