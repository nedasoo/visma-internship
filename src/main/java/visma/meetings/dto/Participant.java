package visma.meetings.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Participant{
    private String name;
    private String meeting;

    //TODO: does not work with a space, requires 'T' instead
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    public visma.meetings.models.Participant produceParticipant(){
        return new visma.meetings.models.Participant(this.time,this.name);
    }
}
