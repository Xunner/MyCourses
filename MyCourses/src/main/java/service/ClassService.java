package service;

import enums.Result;
import vo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 选课服务接口
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
public interface ClassService {
	List<ClassProfile> getMyClassProfiles(Long userId);

	Result publishClasses(Long courseId, LocalDateTime startTime, LocalDateTime endTime, Integer classNumber, Integer term, Integer maxNumber);

	Result reviewClass(Long classId, boolean pass);

	Result takeClass(Long studentId, Long classId);

	Result quitClass(Long studentId, Long classId);

	Result publishHomework(Long classId, String name, String description, LocalDateTime deadline, Integer sizeLimit, String typeRestriction);

	ClassInfo getClassInfo(Long userId, Long classId);

	List<ClassStatisticVO> getClassStatistics(Long studentId);

	PostVO addPost(Long userId, String title, String text);

	TakeClassesVO getClassesToTake(Long studentId);

	List<TeacherCourseVO> getTeacherCourses(Long teacherId);

	Map<String, Object> getReview(Long adminId);
}
