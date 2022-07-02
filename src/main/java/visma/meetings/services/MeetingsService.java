package visma.meetings.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import visma.meetings.dto.Participant;
import visma.meetings.exceptions.APIException;
import visma.meetings.models.Meeting;
import visma.meetings.models.Person;
import visma.meetings.repositories.MeetingsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MeetingsService {
    private MeetingsRepository repo;

    public MeetingsService(MeetingsRepository repo){
        this.repo = repo;
    }

    public List<Meeting> getAllMeetings(
            String description,
            String responsiblePerson,
            String category,
            String type,
            String dateFrom,
            String dateTo,
            Integer participantsFrom,
            Integer participantsTo
    ) throws APIException{
        List<Meeting> meetings = repo.load();
        if(description != null){
            meetings = meetings.stream().filter(meeting->
                    meeting.getDescription() != null &&
                    Pattern.compile(".*"+description+".*",Pattern.CASE_INSENSITIVE).matcher(meeting.getDescription()).matches()
            ).collect(Collectors.toList());
        }
        if(responsiblePerson != null){
            meetings = meetings.stream().filter(meeting->
                    meeting.getResponsiblePerson().getName().equals(responsiblePerson)
            ).collect(Collectors.toList());
        }
        if(category != null){
            meetings = meetings.stream().filter(meeting->
                    meeting.getCategory().name().equals(category)
            ).collect(Collectors.toList());
        }
        if(type != null){
            meetings = meetings.stream().filter(meeting->
                    meeting.getType().name().equals(type)
            ).collect(Collectors.toList());
        }
        if(dateFrom != null){
            try{
                LocalDateTime parsedTime = LocalDateTime.parse(dateFrom);
                meetings = meetings.stream().filter(meeting->
                        meeting.getStartDate().compareTo(parsedTime) <= 0
                ).collect(Collectors.toList());
            }catch(DateTimeParseException e){
                throw new APIException("Specified start date is invalid",HttpStatus.BAD_REQUEST);
            }
        }
        if(dateTo != null){
            try{
                LocalDateTime parsedTime = LocalDateTime.parse(dateTo);
                meetings = meetings.stream().filter(meeting->
                        meeting.getEndDate().compareTo(parsedTime) >= 0
                ).collect(Collectors.toList());
            }catch(DateTimeParseException e){
                throw new APIException("Specified end date is invalid",HttpStatus.BAD_REQUEST);
            }
        }
        if(participantsFrom != null || participantsTo != null){
            meetings = meetings.stream().filter(meeting->
                    (participantsFrom == null ? true : meeting.getParticipants().size()+1 >= participantsFrom) &&
                    (participantsTo == null ? true : meeting.getParticipants().size()+1 <= participantsTo)
            ).collect(Collectors.toList());
        }
        return meetings;
    }
    public Meeting createNewMeeting(Meeting meeting) throws APIException {
        List<Meeting> meetings = repo.load();
        if(meeting.getName() == null || meeting.getName().isEmpty()) throw new APIException("Please enter a name for this meeting.",HttpStatus.BAD_REQUEST);
        if(meetings.stream().anyMatch(m->m.getName().equals(meeting.getName()))) throw new APIException("A meeting with this name already exists.", HttpStatus.BAD_REQUEST);
        if(meeting.getResponsiblePerson() == null || meeting.getResponsiblePerson().toString().isEmpty()) throw new APIException("Please specify a responsible person.",HttpStatus.BAD_REQUEST);
        if(meeting.getType() == null) throw new APIException("Please specify a type for the meeting.",HttpStatus.BAD_REQUEST);
        if(meeting.getCategory() == null) throw new APIException("Please specify a category for the meeting.",HttpStatus.BAD_REQUEST);
        if(meeting.getStartDate() == null) throw new APIException("Please specify a start date for the meeting.",HttpStatus.BAD_REQUEST);
        if(meeting.getEndDate() == null) throw new APIException("Please specify an end date for the meeting.",HttpStatus.BAD_REQUEST);
        meetings.add(meeting);
        repo.save(meetings);
        return meeting;
    }

    public Meeting deleteMeeting(String name) throws APIException{
        List<Meeting> meetings = repo.load();
        for(Meeting meeting:meetings){
            if(meeting.getName().equals(name)){
                meetings.remove(meeting);
                repo.save(meetings);
                return meeting;
            }
        }
        throw new APIException("A meeting with this name does not exist.",HttpStatus.BAD_REQUEST);
    }

    public Meeting addParticipant(Participant participant) throws APIException{
        if(participant.getName() == null || participant.getName().isEmpty()) throw new APIException("Please specify participant's name.",HttpStatus.BAD_REQUEST);
        if(participant.getMeeting() == null) throw new APIException("Please specify meeting name",HttpStatus.BAD_REQUEST);
        if(participant.getTime() == null) throw new APIException("Please specify attending time for this participant",HttpStatus.BAD_REQUEST);
        List<Meeting> meetings = repo.load();
        Meeting alreadyIn = isAlreadyInMeeting(meetings,participant.getName(),participant.getTime());
        if(alreadyIn != null) throw new APIException("This person is already in a meeting \""+alreadyIn.getName()+"\" between "+alreadyIn.getStartDate()+" and "+ alreadyIn.getEndDate()+".",HttpStatus.BAD_REQUEST);
        for(Meeting meeting:meetings){
            if(meeting.getName().equals(participant.getMeeting())){
                if(meeting.getStartDate().isAfter(participant.getTime()) || meeting.getEndDate().isBefore(participant.getTime()))
                    throw new APIException("Specified time is incorrect. This meeting starts at "+meeting.getStartDate()+" and ends at "+meeting.getEndDate()+".",HttpStatus.BAD_REQUEST);
                if(meeting.getParticipants().stream().anyMatch(p->p.getName().equals(participant.getName())))
                    throw new APIException("This person has already been added to this meeting.",HttpStatus.BAD_REQUEST);
                meeting.getParticipants().add(participant.produceParticipant());
                repo.save(meetings);
                return meeting;
            }
        }
        throw new APIException("A meeting with specified name does not exist.",HttpStatus.BAD_REQUEST);
    }

    public visma.meetings.models.Participant removeParticipant(String meetingName, String participantName) throws APIException{
        List<Meeting> meetings = repo.load();
        for(Meeting meeting:meetings){
            if(meeting.getName().equals(meetingName)){
                for(visma.meetings.models.Participant participant : meeting.getParticipants()){
                    if(participant.getName().equals(participantName)){
                        meeting.getParticipants().remove(participant);
                        repo.save(meetings);
                        return participant;
                    }
                }
                throw new APIException("Specified person does not exist in this meeting.",HttpStatus.BAD_REQUEST);
            }
        }
        throw new APIException("A meeting with specified name does not exist.",HttpStatus.BAD_REQUEST);
    }

    private Meeting isAlreadyInMeeting(List<Meeting> meetings, String name, LocalDateTime time){
        Meeting alreadyIn = meetings.stream().filter(meeting->{
            return
                    meeting.getParticipants().stream().anyMatch(participant -> participant.getName().equals(name)) &&
                    meeting.getStartDate().isBefore(time) &&
                    meeting.getEndDate().isAfter(time);
        }).findFirst().orElse(null);
        return alreadyIn;
    }
}
