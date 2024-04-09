package steparrik.code.crypraidesonwebhooks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crypraidesonwebhooks.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<steparrik.code.crypraidesonwebhooks.entity.User, Integer> {
    Optional<User> findByChatId(String chatId);
}
