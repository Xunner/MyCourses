package service;

import enums.Result;

/**
 * 课程服务接口
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
public interface CourseService {
	Result createCourse(Long teacherId, String name, Integer grade);

	/**
	 * TODO
	 */
	Result uploadCourseware();

	/**
	 * TODO
	 */
	Result publishHomework(String name, String description);
}
