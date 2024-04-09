package steparrik.code.crypraidesonwebhooks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crypraidesonwebhooks.entity.Coin;
import steparrik.code.crypraidesonwebhooks.entity.User;


import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Integer> {
    Optional<Coin> findByName(String name);
    Optional<Coin> findByNameAndUser(String name, User user);

}
