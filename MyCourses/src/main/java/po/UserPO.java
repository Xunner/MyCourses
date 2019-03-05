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
@Table(name = "user")
public class UserPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String password;
	private String studentId;
	private String email;
	private boolean deleted;
}
