package service.impl;

import dao.StudentDao;
import dao.TeacherDao;
import dao.UserDao;
import enums.Result;
import enums.StudentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.StudentPO;
import po.TeacherPO;
import po.UserPO;
import service.UserService;
import util.MailUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	private final Map<String, TeacherPO> activationTeacher = new ConcurrentHashMap<>();
	private final Map<String, StudentPO> activationStudent = new ConcurrentHashMap<>();
	private final Map<LocalDateTime, String> activationDeadline = new ConcurrentHashMap<>();

	@Autowired
	public UserServiceImpl(UserDao userDao, StudentDao studentDao, TeacherDao teacherDao) {
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
	}

	@Override
	public UserPO findById(Long userId) {
		return userDao.findOne(userId);
	}

	@Override
	public UserPO login(String email, String password) {
		return userDao.findByEmailAndPasswordAndDeletedFalse(email, password);
	}

	@Override
	public Result registerStudent(String email, String name, String password, String studentId) {
		if (!email.matches("^[a-zA-Z0-9]+@smail.nju.edu.cn$")) {
			return Result.FAILED;
		}
		if (userDao.existsByEmailAndDeletedFalse(email)) {
			return Result.EXIST;
		}
		StudentType studentType = (email.charAt(0) == 'M' ? StudentType.GRADUATE : StudentType.UNDERGRADUATE);
		String code = createUUID();
		activationStudent.put(code, new StudentPO(email, name, password, false, studentId, studentType));
		activationDeadline.put(LocalDateTime.now().plusMinutes(3), code);
		new Thread(new MailUtil(email, code)).start();
		return Result.SUCCESS;
	}

	@Override
	public Result registerTeacher(String email, String name, String password) {
		if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
			return Result.FAILED;
		}
		if (userDao.existsByEmailAndDeletedFalse(email)) {
			return Result.EXIST;
		}
		String code = createUUID();
		activationTeacher.put(code, new TeacherPO(email, name, password, false));
		activationDeadline.put(LocalDateTime.now().plusMinutes(3), code);
		new Thread(new MailUtil(email, code)).start();
		return Result.SUCCESS;
	}

	@Override
	public Result activateAccount(String activationCode) {
		if (activationStudent.containsKey(activationCode)) {
			studentDao.save(activationStudent.get(activationCode));
			activationStudent.remove(activationCode);
			return Result.SUCCESS;
		} else if (activationTeacher.containsKey(activationCode)) {
			teacherDao.save(activationTeacher.get(activationCode));
			activationTeacher.remove(activationCode);
			return Result.SUCCESS;
		}
		return Result.NOT_EXIST;
	}

	@Scheduled(fixedRate = 60000)   // 60秒执行一次
	public void handlePassedActivation() {
		LocalDateTime now = LocalDateTime.now();
		activationDeadline.forEach((deadline, code) -> {
			if (now.isAfter(deadline)) {
				activationStudent.remove(code);
				activationTeacher.remove(code);
			}
		});
	}

	private static String createUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
