package restfulWS.auth;

import java.util.List;

import restfulWS.auth.model.AuthModel;
import restfulWS.auth.model.MemberModel;

public interface Auth {
	void create(String action, MemberModel memModel) throws Exception;
	void update(String token, MemberModel memModel) throws Exception;
	void delete(String token) throws Exception;
	MemberModel find(String token) throws Exception;
}
