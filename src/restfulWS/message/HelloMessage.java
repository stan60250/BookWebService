package restfulWS.message;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("restfulWS.HelloMessage")
@RequestMapping("/webservice/message")
public class HelloMessage implements Message {
	
	// http://localhost:8080/13_MemberWS/spring/webservice/message/doHello
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/message/doHello
	@RequestMapping(value = "/doHello", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String doMessage(@RequestBody String name) {
		String result;
		result = "Hello, " + name;
		return result;
	}
}
