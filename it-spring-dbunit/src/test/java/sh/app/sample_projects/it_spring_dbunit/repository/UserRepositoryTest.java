package sh.app.sample_projects.it_spring_dbunit.repository;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sh.app.sample_projects.it_spring_dbunit.BaseIntegrationTest;
import sh.app.sample_projects.it_spring_dbunit.entity.User;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@DatabaseSetup("userRepository.dbxml")
public class UserRepositoryTest extends BaseIntegrationTest {
  @Autowired
  UserRepository userRepository;

  @Test
  public void findByLastnameIgnoreCase_noSuchUser() throws Exception {
    assertThat(userRepository.findByLastnameIgnoreCase("Lastname").size(), is(0));
  }

  @Test
  public void findByLastnameIgnoreCase_ok() throws Exception {
    User user = new User();
    user.setFirstname("Firstname1");
    user.setLastname("Lastname1");

    assertThat(userRepository.findByLastnameIgnoreCase("lastname1"), is(equalTo(asList(user))));
  }
}
