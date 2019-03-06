package vo;

import lombok.Data;

/**
 * register方法 VO
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
public class RegisterVO {
	/** 0: Student, 1: Teacher */
	public int type;
	public String email;
	public String name;
	public String password;
	public String studentId;
}
