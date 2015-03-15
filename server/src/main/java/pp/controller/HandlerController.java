package pp.controller;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import org.jmmo.sc.Cassandra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pp.model.User;
import pp.protocol.PingRequest;
import pp.protocol.PongResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HandlerController {
    private static final Logger log = LoggerFactory.getLogger(HandlerController.class);

    @Autowired
    Cassandra cassandra;

    @RequestMapping(value = "/handler", method = RequestMethod.POST)
    public PongResponse handler(@RequestBody PingRequest pingRequest, HttpServletResponse response) {
        if (!pingRequest.getCommand().equals(PingRequest.COMMAND) || pingRequest.getUserId() == null || pingRequest.getUserId().isEmpty()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        cassandra.update(new User(pingRequest.getUserId()), QueryBuilder.incr("pings"));

        final User user = cassandra.selectOne(User.class, pingRequest.getUserId()).get();

        return new PongResponse(user.getPings());
    }


    @ExceptionHandler(Exception.class)
    public void defaultHandler(HttpServletResponse response, Exception e) throws IOException {
        log.warn("Unexpected exception caught", e);

        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

}
