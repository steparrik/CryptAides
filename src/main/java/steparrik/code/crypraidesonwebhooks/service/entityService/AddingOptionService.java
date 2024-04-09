package steparrik.code.crypraidesonwebhooks.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;
import steparrik.code.crypraidesonwebhooks.entity.User;
import steparrik.code.crypraidesonwebhooks.repositories.AddingOptionsRepository;


import java.util.Iterator;

@Service
public class AddingOptionService {
    private final AddingOptionsRepository addingOptionsRepository;
    private final UserService userService;

    @Autowired
    public AddingOptionService(AddingOptionsRepository addingOptionsRepository, UserService userService) {
        this.addingOptionsRepository = addingOptionsRepository;
        this.userService = userService;
    }


    public AddingOptions save(AddingOptions addingOptions){
        return addingOptionsRepository.save(addingOptions);
    }

    public void deleteById(int id){
        addingOptionsRepository.deleteById(id);
    }

    public void deleteAdding(long chatId, User user){
        Iterator<AddingOptions> iterator = user.getAddingOptions().iterator();
        while (iterator.hasNext()) {
            AddingOptions adding = iterator.next();
            addingOptionsRepository.deleteById(adding.getId());
            iterator.remove();
        }
        userService.save(user);
    }
}
