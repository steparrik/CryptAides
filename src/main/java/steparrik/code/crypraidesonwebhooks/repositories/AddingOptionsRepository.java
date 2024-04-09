package steparrik.code.crypraidesonwebhooks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import steparrik.code.crypraidesonwebhooks.entity.AddingOptions;


@Repository
public interface AddingOptionsRepository extends JpaRepository<AddingOptions, Integer> {
    void deleteById(int id);
}
