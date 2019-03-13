package service.impl;

import dao.CourseDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.CoursePO;
import service.CourseService;

import java.util.*;

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
	private final Map<Long, CoursePO> coursesToReviewed = Collections.synchronizedMap(new HashMap<>());
	private Long id = 0L;
	private final Boolean lock = true;

	@Autowired
	public CourseServiceImpl(CourseDao courseDao) {
		this.courseDao = courseDao;
	}

	@Override
	public Result createCourse(Long teacherId, String name, Integer grade) {
		synchronized (lock) {
			coursesToReviewed.put(++id, new CoursePO(teacherId, name, grade));
		}
		return Result.SUCCESS;
	}

	@Override
	public Result reviewCourse(Long courseId, boolean pass) {
		Iterator<Map.Entry<Long, CoursePO>> entries = coursesToReviewed.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Long, CoursePO> entry = entries.next();
			if (courseId.equals(entry.getKey())) {
				if (pass) {
					courseDao.save(entry.getValue());
				}
				entries.remove();
				System.out.println("完成开课在线审核：" + courseId);
				return Result.SUCCESS;
			}
		}
		return Result.NOT_EXIST;
	}

	/**
	 * TODO
	 */
	@Override
	public Result uploadCourseware() {
		return null;
	}
}
