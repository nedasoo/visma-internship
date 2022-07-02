package visma.meetings.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;
import visma.meetings.models.Meeting;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MeetingsRepository {
    private final String FILENAME = "meetings.json";
    private final ObjectMapper mapper = createMapper();
    public void save(List<Meeting> meetings){
        try{
            FileWriter writer = new FileWriter(FILENAME);
            mapper.writeValue(writer,meetings);
        }catch(IOException e){
            System.out.println("Could not write to file: "+e.getMessage());
        }
    }
    public List<Meeting> load(){
        try{
            FileReader reader = new FileReader(FILENAME);
            return mapper.readValue(reader,new TypeReference<List<Meeting>>(){});
        }catch(IOException e){
            System.out.println("Could not read from file: "+e.getMessage());
            return new ArrayList<Meeting>();
        }
    }

    private ObjectMapper createMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //for dates
        mapper.registerModule(new JavaTimeModule());

        return mapper;
    }
}
