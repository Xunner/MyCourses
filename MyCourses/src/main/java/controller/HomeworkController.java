package controller;

import enums.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import service.ClassService;
import vo.HomeworkVO;

import java.util.HashMap;
import java.util.Map;

/**
 * 作业功能控制器
 * <br>
 * created on 2019/03/29
 *
 * @author 巽
 **/
@RestController
public class HomeworkController {
	private final ClassService classService;

	@Autowired
	public HomeworkController(ClassService classService) {
		this.classService = classService;
	}

	@RequestMapping(value = "/publishHomework", method = RequestMethod.POST)
	public Map<String, Object> publishHomework(@RequestBody HomeworkVO homeworkVO) {
		System.out.println("publishHomework: " + homeworkVO.toString());
		Map<String, Object> ret = new HashMap<>();
		HomeworkVO returnVO = classService.publishHomework(homeworkVO);
		if (returnVO.id == null) {
			ret.put("result", Result.FAILED);
		} else {
			ret.put("result", Result.SUCCESS);
			ret.put("homework", returnVO);
		}
		return ret;
	}
}
