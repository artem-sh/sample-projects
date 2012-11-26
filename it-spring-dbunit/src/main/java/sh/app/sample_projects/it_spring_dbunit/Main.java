package sh.app.sample_projects.it_spring_dbunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sh.app.sample_projects.it_spring_dbunit.entity.User;
import sh.app.sample_projects.it_spring_dbunit.repository.UserRepository;

public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationConfiguration.xml");
    UserRepository repository = context.getBean(UserRepository.class);

    User user = new User();
    user.setFirstname("Bon");
    user.setLastname("Jovy");
    repository.save(user);

    log.info(repository.findAll().toString());
  }
}