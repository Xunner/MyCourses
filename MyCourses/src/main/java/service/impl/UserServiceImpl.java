package service.impl;

import dao.StudentDao;
import dao.TeacherDao;
import dao.UserDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.StudentPO;
import po.TeacherPO;
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
	private final StudentDao studentDao;
	private final TeacherDao teacherDao;

	@Autowired
	public UserServiceImpl(UserDao userDao, StudentDao studentDao, TeacherDao teacherDao) {
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
	}

	@Override
	public Result login(String email, String password) {
		if (userDao.existsByEmailAndPasswordAndDeletedFalse(email, password)) {
			return Result.SUCCESS;
		}
		return Result.NOT_EXIST;
	}

	@Override
	public Result registerStudent(String email, String name, String password, String studentId) {
		if (userDao.existsByEmailAndDeletedFalse(email)) {
			return Result.EXIST;
		}
		studentDao.save(new StudentPO(email, name, password, false, studentId));
		return Result.SUCCESS;
	}

	@Override
	public Result registerTeacher(String email, String name, String password) {
		if (userDao.existsByEmailAndDeletedFalse(email)) {
			return Result.EXIST;
		}
		teacherDao.save(new TeacherPO(email, name, password, false));
		return Result.SUCCESS;
	}
}
