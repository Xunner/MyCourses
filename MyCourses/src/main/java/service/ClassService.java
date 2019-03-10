package service;

import enums.Result;
import po.ClassPO;
import vo.ClassInfo;
import vo.ClassProfile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课服务接口
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
public interface ClassService {
	List<ClassProfile> getMyClassProfiles(Long userId);

	Result publishClass(Long teacherId, Long courseId, LocalDateTime startTime, LocalDateTime endTime, List<Integer> classOrders, Integer term);

	Result takeClass(Long studentId, Long classId);

	Result quitClass(Long studentId, Long classId);

	Result publishHomework(Long classId, String name, String description, LocalDateTime deadline, Integer sizeLimit, String typeRestriction);

	ClassInfo getClassInfo(Long userId, Long classId);
}
