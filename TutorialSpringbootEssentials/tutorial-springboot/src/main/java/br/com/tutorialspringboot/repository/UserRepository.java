package br.com.tutorialspringboot.repository;

import br.com.tutorialspringboot.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUsername(String username);
}
