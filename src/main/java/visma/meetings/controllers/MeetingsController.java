package visma.meetings.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import visma.meetings.dto.Meeting;
import visma.meetings.dto.Participant;
import visma.meetings.exceptions.APIException;
import visma.meetings.services.MeetingsService;

import java.util.List;

@RestController
public class MeetingsController {
    MeetingsService service;
    public MeetingsController(MeetingsService service){
        this.service = service;
    }

    @GetMapping("/meetings")
    public ResponseEntity<List<visma.meetings.models.Meeting>>getAllMeetings(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String responsiblePerson,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer participantsFrom,
            @RequestParam(required = false) Integer participantsTo
    ){
        try{
            List<visma.meetings.models.Meeting> response = service.getAllMeetings(
                    description,
                    responsiblePerson,
                    category,
                    type,
                    dateFrom,
                    dateTo,
                    participantsFrom,
                    participantsTo
            );
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(APIException e) {
            return new ResponseEntity(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity("An error occurred.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/meetings")
    public ResponseEntity<visma.meetings.models.Meeting>createMeeting(@RequestBody Meeting meeting){
        try{
            visma.meetings.models.Meeting response = service.createNewMeeting(meeting.constructMeeting());
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(APIException e) {
            return new ResponseEntity(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity("An error occurred.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: only allow resposible person to delete the meeting (authorization?)
    @DeleteMapping("/meetings/{name}")
    public ResponseEntity<visma.meetings.models.Meeting>deleteMeeting(@PathVariable String name){
        try{
            visma.meetings.models.Meeting response = service.deleteMeeting(name);
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(APIException e) {
            return new ResponseEntity(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity("An error occurred.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/participants")
    public ResponseEntity<visma.meetings.models.Meeting>addParticipant(@RequestBody Participant participant){
        try{
            visma.meetings.models.Meeting response = service.addParticipant(participant);
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(APIException e) {
            return new ResponseEntity(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity("An error occurred.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/meetings/{meeting}/participants/{name}")
    public ResponseEntity<visma.meetings.models.Participant>removeParticipant(
            @PathVariable(name="meeting") String meetingName,
            @PathVariable(name="name") String name){
        try{
            visma.meetings.models.Participant response = service.removeParticipant(meetingName,name);
            return new ResponseEntity(response, HttpStatus.OK);
        }catch(APIException e) {
            return new ResponseEntity(e.getMessage(),e.getStatusCode());
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity("An error occurred.",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
