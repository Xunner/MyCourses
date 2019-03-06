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
	Result login(String email, String password);

	Result registerStudent(String email, String name, String password, String studentId);

	Result registerTeacher(String email, String name, String password);
}
