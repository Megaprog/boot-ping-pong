package pp.controller;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.jmmo.sc.Cassandra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pp.model.User;
import pp.protocol.PingRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class HandlerController {

    @Autowired
    Cassandra cassandra;

    @RequestMapping(value = "/handler", method = RequestMethod.POST)
    public String handler(@RequestBody PingRequest pingRequest, HttpServletResponse response) {
        if (!pingRequest.getType().equals("ping") || pingRequest.getUserId() == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return "";
        }

        final UUID userId;
        try {
            userId = UUID.fromString(pingRequest.getUserId());
        }
        catch (Exception e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return "";
        }

        cassandra.update(new User(userId), QueryBuilder.incr("pings"));

        final User user = cassandra.selectOne(User.class, userId).get();

        return "PONG " + user.getPings();
    }
}
