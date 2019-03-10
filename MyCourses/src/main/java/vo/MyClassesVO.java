package vo;

import enums.Result;

import java.util.List;

/**
 * getMyClasses方法 VO
 * <br>
 * created on 2019/03/10
 *
 * @author 巽
 **/
public class MyClassesVO {
	public Result result;
	public List<ClassProfile> myClasses;

	public MyClassesVO(Result result, List<ClassProfile> myClasses) {
		this.result = result;
		this.myClasses = myClasses;
	}
}
