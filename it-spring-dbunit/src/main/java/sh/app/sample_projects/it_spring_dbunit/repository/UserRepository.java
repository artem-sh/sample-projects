package sh.app.sample_projects.it_spring_dbunit.repository;

import org.springframework.data.repository.CrudRepository;
import sh.app.sample_projects.it_spring_dbunit.entity.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

  List<User> findByLastnameIgnoreCase(String lastname);
}