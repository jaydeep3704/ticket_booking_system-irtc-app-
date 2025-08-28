package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TrainService {

    private static final String TRAINS_PATH="app/src/main/java/ticket/booking/localDB/trains.json";
    private ObjectMapper objectMapper=new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private static  List<Train> trainList;

    public  TrainService() throws IOException {
         File trains=new File(TRAINS_PATH);
         System.out.println(trains.getAbsolutePath());
         trainList=objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
    }

    public static List<Train> searchTrains(String source, String destination){
                return trainList.stream().filter(
                train -> validTrain(train,source,destination)).collect(Collectors.toList());
    }

    private static boolean validTrain(Train train, String source, String destination) {
        List<String> stations = train.getStations();
        int sourceIndex = stations.indexOf(source);
        int destIndex = stations.indexOf(destination);
        return sourceIndex != -1 && destIndex != -1 && sourceIndex < destIndex;
    }

}
