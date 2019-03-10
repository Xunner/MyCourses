package service;

import enums.Result;
import po.UserPO;

/**
 * 用户服务接口
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
public interface UserService {
	UserPO findById(Long userId);

	UserPO login(String email, String password);

	Result registerStudent(String email, String name, String password, String studentId);

	Result registerTeacher(String email, String name, String password);

	Result activateAccount(String activationCode);

	Result updateUserInfo(Long userId, String name, String studentId);
}
