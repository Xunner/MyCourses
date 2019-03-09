package service.impl;

import dao.ClassDao;
import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import po.ClassPO;
import service.ClassService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 班次服务实现类
 * <br>
 * created on 2019/03/09
 *
 * @author 巽
 **/
@Service
@Transactional
public class ClassServiceImpl implements ClassService {
	private final ClassDao classDao;
	private final List<ClassPO> classesToReviewed = Collections.synchronizedList(new ArrayList<>());

	@Autowired
	public ClassServiceImpl(ClassDao classDao) {
		this.classDao = classDao;
	}

	@Override
	public List<ClassPO> findAllByUserId(Long userId) {
		return null;
	}

	@Override
	public Result publishClass(Long teacherId, Long courseId, LocalDateTime startTime, LocalDateTime endTime, List<Integer> classOrders, Integer term) {
		for (Integer classOrder : classOrders) {
			classesToReviewed.add(new ClassPO(startTime, endTime, classOrder, term));
		}
		return Result.SUCCESS;
	}

	@Override
	public Result takeClass(Long studentId, Long classId) {
		return null;
	}

	@Override
	public Result quitClass(Long studentId, Long classId) {
		return null;
	}
}
