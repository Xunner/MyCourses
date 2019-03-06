package po;

import lombok.Data;

import javax.persistence.*;

/**
 * 用户
 * <br>
 * created on 2019/03/05
 *
 * @author 巽
 **/
@Data
@Entity
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("U")
@Table(name = "user")
public class UserPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;
	protected String email;
	protected String name;
	protected String password;
	protected boolean deleted;

	public UserPO() {
	}

	public UserPO(String email, String name, String password, boolean deleted) {
		this.email = email;
		this.name = name;
		this.password = password;
		this.deleted = deleted;
	}
}
