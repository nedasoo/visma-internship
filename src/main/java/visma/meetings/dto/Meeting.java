package visma.meetings.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import visma.meetings.models.MeetingCategory;
import visma.meetings.models.MeetingType;
import visma.meetings.models.Participant;
import visma.meetings.models.Person;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Accessors(chain = true)
public class Meeting {
    private String name;
    private Person responsiblePerson;
    private String description;
    private MeetingCategory category;
    private MeetingType type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public visma.meetings.models.Meeting constructMeeting(){
        return new visma.meetings.models.Meeting(
                name,
                responsiblePerson,
                description,
                category,
                type,
                startDate,
                endDate,
                new ArrayList<Participant>()
        );
    }
}