package restfulWS.message;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("restfulWS.AlohaMessage")
@RequestMapping("/webservice/message")
public class AlohaMessage implements Message {
	
	// http://localhost:8080/13_MemberWS/spring/webservice/message/doAloha
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/message/doAloha
	@RequestMapping(value = "/doAloha", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody String doMessage(@RequestBody String name) {
		String result;
		result = "Aloha, " + name;
		return result;
	}
}
