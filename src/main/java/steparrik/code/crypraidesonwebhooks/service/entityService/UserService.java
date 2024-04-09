package steparrik.code.crypraidesonwebhooks.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User save(User user){
       return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public Optional<User> findByChatId(String chatID){
        return userRepository.findByChatId(chatID);
    }

    public boolean checkUser(String chatId){
        Optional<User> user = userRepository.findByChatId(chatId);
        if(user.isPresent()){
            return true;
        }
        return false;
    }
}
