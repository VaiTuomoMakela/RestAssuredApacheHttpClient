package httpclient.restassured.example.problemexample;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** */
@RestController
public class UrlController {

  @RequestMapping(value = "/idecho/{id}")
  public String echoId(@PathVariable("id") String id, HttpServletRequest req) {
    String resp;

    resp = "      path variable id: " + id + "\n" +
        "      req.getPathInfo(): " + req.getPathInfo() + "\n" +
        "req.getPathTranslated(): " + req.getPathTranslated() + "\n" +
        "    req.getRequestURI(): " + req.getRequestURI() + "\n" +
        "   req.getContextPath(): " + req.getContextPath() + "\n";
    System.out.println("Request: " + resp);

    return id;
  }
}
