package service.impl;

import dao.UserDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.UserService;

/**
 * 用户服务实现类
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
@Service
@Transactional
public class UserServiceImpl implements UserService {
	private final UserDao userDao;

	@Autowired
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public Result login(String username, String password) {
		if (userDao.findAllByNameOrStudentIdOrEmail(username, username, username).isEmpty()) {
			return Result.NOT_EXIST;
		}
		return Result.SUCCESS;
	}

	@Override
	public Result register(String name, String password, String studentId, String email) {
		return null;
	}
}
