package my.example.jsf2_cdi_jetty_maven;

import javax.inject.Named;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;


@Named
@SessionScoped
public class Visitor implements Serializable {
  private String name;
  
  @PostConstruct
  public void init() {
    name = "Ivan";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}