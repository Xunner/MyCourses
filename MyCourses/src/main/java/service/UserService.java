package service;

import enums.Result;

/**
 * 用户服务接口
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
public interface UserService {
	Result login(String username, String password);

	Result register(String name, String password, String studentId, String email);
}
