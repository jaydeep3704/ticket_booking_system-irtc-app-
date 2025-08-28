package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.utils.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;
    private List<User> userList;
    private static final String USERS_PATH="app/src/main/java/ticket/booking/localDB/users.json";
    private  ObjectMapper objectMapper=new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    public TrainService trainService;

    public UserBookingService (User user1) throws IOException {
        this.user=user1;
        loadUsers();
    }

    public UserBookingService() throws IOException {
        this.trainService=new TrainService();
        loadUsers();
    }

    public void loadUsers() {
        File users=new File(USERS_PATH);
        try {
            this.userList=objectMapper.readValue(users, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            System.out.println("Failed To Load Users !");
        }
    }





    public Boolean loginUser(){
        Optional<User> foundUser=userList.stream().filter(listUser->{
            return listUser.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),listUser.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            Optional<User> foundUser=userList.stream().filter(listUser->{
                return listUser.getName().equals(user1.getName());
            }).findFirst();

            if(foundUser.isPresent()){
                return Boolean.FALSE;
            }
            userList.add(user1);
            saveUserListToFile();
            return  Boolean.TRUE;
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    public void saveUserListToFile() throws IOException {
        File userFile=new File(USERS_PATH);
        objectMapper.writeValue(userFile,userList);
    }

    public void fetchBooking(){
        user.printTickets();
    }

    public Boolean cancelBooking(String ticketId) throws IOException {
        Optional<Ticket> foundTicket=user.getTicketsBooked().stream().filter(ticket -> {
            return ticket.getTicketId().equals(ticketId);
        }).findFirst();

        if(foundTicket.isEmpty()){
            return Boolean.FALSE;
        }
        else{
            user.getTicketsBooked().remove(foundTicket.get());
            saveUserListToFile();
        }
        return Boolean.TRUE;
    }

    public void logout() {
        if (this.user != null) {
            this.user = null;   // Clear the current logged-in user
            System.out.println("User logged out successfully!");
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    public List<Train> getTrains(String source,String destination){

        return trainService.searchTrains(source,destination);
    }

}
