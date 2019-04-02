package service.impl;

import dao.LogDao;
import dao.StudentDao;
import dao.TeacherDao;
import dao.UserDao;
import enums.OperationType;
import enums.Result;
import enums.StudentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.LogPO;
import po.StudentPO;
import po.TeacherPO;
import po.UserPO;
import service.UserService;
import util.MailUtil;
import vo.UserNumVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
	private final LogDao logDao;
	private final Map<String, TeacherPO> activationTeacher = new ConcurrentHashMap<>();
	private final Map<String, StudentPO> activationStudent = new ConcurrentHashMap<>();
	private final Map<LocalDateTime, String> activationDeadline = new ConcurrentHashMap<>();

	@Autowired
	public UserServiceImpl(UserDao userDao, StudentDao studentDao, TeacherDao teacherDao, LogDao logDao) {
		this.userDao = userDao;
		this.studentDao = studentDao;
		this.teacherDao = teacherDao;
		this.logDao = logDao;
	}

	@Override
	public UserPO findById(Long userId) {
		return userDao.findOne(userId);
	}

	@Override
	public UserPO login(String email, String password) {
		UserPO ret = userDao.findByEmailAndPasswordAndDeletedFalse(email, password);
		if (ret != null) {
			String userType = ret.getClass().getSimpleName();
			if (ret instanceof StudentPO) {
				StudentPO student = (StudentPO) ret;
				userType = student.getStudentType().name();
			}
			logDao.save(new LogPO(LocalDateTime.now(), ret.getId(), ret.getId(), userType, OperationType.LOGIN));
		}
		return ret;
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

	@Override
	public Result updateUserInfo(Long userId, String name, String studentId) {
		UserPO user = userDao.findOne(userId);
		if (user == null) {
			return Result.NOT_EXIST;
		}
		if (user instanceof StudentPO) {
			StudentPO student = (StudentPO) user;
			student.setName(name);
			student.setStudentId(studentId);
			studentDao.save(student);
		} else {
			user.setName(name);
			userDao.save(user);
		}
		return Result.SUCCESS;
	}

	@Override
	public Result deleteAccount(Long userId) {
		UserPO userPO = userDao.findOne(userId);
		if (userPO == null) {
			return Result.NOT_EXIST;
		}
		userPO.setDeleted(true);
		userDao.save(userPO);
		return Result.SUCCESS;
	}

	@Override
	public String getEmailPrefixById(Long userId) {
		String email = userDao.findOne(userId).getEmail();
		return email.substring(0, email.indexOf('@'));
	}

	@Override
	public Map<String, Object> getStatistics(Long adminId) {
		Map<String, Object> ret = new HashMap<>();
		UserPO adminPO = userDao.findOne(adminId);
		if (adminPO == null) {
			ret.put("result", Result.NOT_EXIST);
			return ret;
		}
		List<String> userPieChartOpinion = new ArrayList<>(Arrays.asList("教师", "本科生", "研究生"));
		ret.put("userPieChartOpinion", userPieChartOpinion);
		ret.put("loginCountOpinion", userPieChartOpinion);
		List<UserNumVO> userPieChartSeries = new ArrayList<>();
		userPieChartSeries.add(new UserNumVO("教师", teacherDao.countByDeletedFalse()));
		userPieChartSeries.add(new UserNumVO("本科生", studentDao.countByDeletedFalseAndStudentType(StudentType.UNDERGRADUATE)));
		userPieChartSeries.add(new UserNumVO("研究生", studentDao.countByDeletedFalseAndStudentType(StudentType.GRADUATE)));

		List<String> loginCountX = new ArrayList<>();
		List<List<Integer>> loginCountSeries = new ArrayList<>();
		List<Integer> teacher = new ArrayList<>();
		List<Integer> undergraduate = new ArrayList<>();
		List<Integer> graduate = new ArrayList<>();
		LocalDate now = LocalDate.now();
		for (LocalDate date = now.minusDays(7); date.isBefore(now); date = date.plusDays(1)) {
			loginCountX.add(date.toString());
			LocalDateTime start = now.atTime(0, 0, 0);
			LocalDateTime end = now.atTime(23, 59, 59);
			teacher.add(logDao.countByTimeBetweenAndObjectClassAndOperationType(start, end,
					TeacherPO.class.getSimpleName(), OperationType.LOGIN));
			undergraduate.add(logDao.countByTimeBetweenAndObjectClassAndOperationType(start, end,
					StudentType.UNDERGRADUATE.name(), OperationType.LOGIN));
			graduate.add(logDao.countByTimeBetweenAndObjectClassAndOperationType(start, end,
					StudentType.GRADUATE.name(), OperationType.LOGIN));
		}
		loginCountSeries.add(teacher);
		loginCountSeries.add(undergraduate);
		loginCountSeries.add(graduate);
		ret.put("userPieChartSeries", userPieChartSeries);
		ret.put("loginCountX", loginCountX);
		ret.put("loginCountSeries", loginCountSeries);
		ret.put("result", Result.SUCCESS);
		return ret;
	}

	@Override
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
