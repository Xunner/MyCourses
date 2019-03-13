package vo;

import java.util.List;

/**
 * TakeClasses方法 VO
 * <br>
 * created on 2019/03/14
 *
 * @author 巽
 **/
public class TakeClassesVO {
	public List<ClassToTakeVO> unselectedClass;
	public List<ClassToTakeVO> selectedClass;

	public TakeClassesVO(List<ClassToTakeVO> unselectedClass, List<ClassToTakeVO> selectedClass) {
		this.unselectedClass = unselectedClass;
		this.selectedClass = selectedClass;
	}
}
