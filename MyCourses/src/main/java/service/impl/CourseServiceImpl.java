package service.impl;

import dao.CourseDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.CoursePO;
import service.CourseService;
import vo.CourseProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 课程服务
 * <br>
 * created on 2019/03/09
 *
 * @author 巽
 **/
@Service
@Transactional
public class CourseServiceImpl implements CourseService {
	private final CourseDao courseDao;
	private final List<CoursePO> coursesToReviewed = Collections.synchronizedList(new ArrayList<>());

	@Autowired
	public CourseServiceImpl(CourseDao courseDao) {
		this.courseDao = courseDao;
	}

	@Override
	public Result createCourse(Long teacherId, String name, Integer grade) {
		coursesToReviewed.add(new CoursePO(teacherId, name, grade));
		return Result.SUCCESS;
	}

	/**
	 * TODO
	 */
	@Override
	public Result uploadCourseware() {
		return null;
	}

	@Override
	public List<CourseProfile> getCourseProfilesToTake(Long studentId) {
		// TODO
		return null;
	}
}
