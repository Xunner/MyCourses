package vo;

import enums.Result;

import java.util.List;

/**
 * TakeClasses方法 VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class TakeClassesVO {
	public Result result;
	public List<ClassToTakeVO> unselectedClass;
	public List<ClassToTakeVO> selectedClass;

	public TakeClassesVO(Result result, List<ClassToTakeVO> unselectedClass, List<ClassToTakeVO> selectedClass) {
		this.result = result;
		this.unselectedClass = unselectedClass;
		this.selectedClass = selectedClass;
	}
}
