package service;

import enums.Result;
import po.ClassPO;

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

	/**
	 * TODO
	 */
	Result publishClass(Long teacherId, Long courseId, LocalDateTime startTime, LocalDateTime endTime, List<Integer> classOrders);

	Result takeClass(Long studentId, Long classId);

	Result quitClass(Long studentId, Long classId);
}
