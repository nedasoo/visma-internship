package visma.meetings;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import visma.meetings.models.Meeting;

import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MeetingsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final String meetingName = "java-meet";

    @Test
    @Order(1)
    void insertNewMeeting() throws Exception{
        this.mockMvc.perform(
                post("/meetings").content("{\"name\":\""+meetingName+"\",\"description\":\"A casual Java meeting\",\"responsiblePerson\":\"Vardenis Pavardenis\",\"type\":\"Live\",\"category\":\"Short\",\"startDate\":\"2022-07-04 12:00:00\",\"endDate\":\"2022-07-04 14:00:00\"}").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(meetingName));
    }

    @Test
    @Order(2)
    void deleteMeeting() throws Exception{
        this.mockMvc.perform(delete("/meetings/"+meetingName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(meetingName));
    }

    @Test
    @Order(3)
    void createAnotherMeeting() throws Exception{
        this.insertNewMeeting();
    }

    @Test
    @Order(4)
    void addPersonToMeeting() throws Exception{
        this.mockMvc.perform(post("/participants").contentType(MediaType.APPLICATION_JSON).content("{\"meeting\":\""+meetingName+"\",\"name\":\"Jonas Petraitis\",\"time\":\"2022-07-04T13:30:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants[0].name").value("Jonas Petraitis"));
    }
    @Test
    @Order(5)
    void removePersonFromMeeting() throws Exception{
        this.mockMvc.perform(delete("/meetings/"+meetingName+"/participants/Jonas Petraitis"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void removeNonExistentAttendee() throws Exception{
        this.mockMvc.perform(delete("/meetings/"+meetingName+"/participants/Firstname Lastname"))
                .andExpect(status().is4xxClientError());
    }

    void listMeetings(MultiValueMap<String,String> filters, ResultMatcher expectedResult) throws Exception{
        this.mockMvc.perform(get("/meetings").queryParams(filters))
                .andExpect(status().isOk())
                .andExpect(expectedResult);
    }

    @Test
    @Order(7)
    void listAllMeetingsNoFilters() throws Exception{
        MultiValueMap<String,String> filters = new LinkedMultiValueMap<>();
        this.listMeetings(filters,jsonPath("$").isNotEmpty());
    }

    @Test
    @Order(8)
    void listMeetingsWithDescriptionFilter() throws Exception{
        MultiValueMap<String,String> filters = new LinkedMultiValueMap<>();
        filters.add("description","CASUAL");
        this.listMeetings(filters,jsonPath("$").isNotEmpty());
    }

    @Test
    @Order(9)
    void listMeetingsWithDateFilter() throws Exception{
        MultiValueMap<String,String> filters = new LinkedMultiValueMap<>();
        filters.add("dateFrom","2022-09-05T10:00:00");
        filters.add("dateTo","2022-10-15T23:59:59");
        this.listMeetings(filters,jsonPath("$").isEmpty());
    }

    @Test
    @Order(10)
    void listMeetingsWithCategoryFilter() throws Exception{
        MultiValueMap<String,String> filters = new LinkedMultiValueMap<>();
        filters.add("category","Short");
        this.listMeetings(filters,jsonPath("$").isNotEmpty());
    }

    @AfterAll
    void cleanup() throws Exception{
        System.out.printf("Cleaning up");
        this.deleteMeeting();
    }




}
