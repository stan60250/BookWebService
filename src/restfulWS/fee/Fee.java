package restfulWS.fee;

import java.util.List;

import restfulWS.fee.model.FeeModel;

public interface Fee {
	void create(FeeModel feeModel) throws Exception;
	void update(int id, FeeModel feeModel) throws Exception;
	void delete(int id) throws Exception;
	FeeModel find(int id) throws Exception;
	List<FeeModel> list() throws Exception;
	FeeModel findByName(String name) throws Exception;
}
