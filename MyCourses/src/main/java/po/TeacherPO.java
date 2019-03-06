package po;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 教师
 * <br>
 * created on 2019/03/06
 *
 * @author 巽
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("T")
public class TeacherPO extends UserPO {
	public TeacherPO() {
	}

	public TeacherPO(String email, String name, String password, boolean deleted) {
		super(email, name, password, deleted);
	}
}
