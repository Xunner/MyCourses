package po;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * （老师发布的）作业
 * <br>
 * created on 2019/03/08
 *
 * @author 巽
 **/
@Data
@Entity
@Table(name = "homework")
public class HomeworkPO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "class_id")
	private Long classId;

	private String name;

	private String description;

	private LocalDateTime deadline;

	/** 提交文件大小上限(MB) */
	@Column(name = "size_limit")
	private Integer sizeLimit;

	/** 提交文件类型限制，格式形如：""(无限制), "txt", "txt,doc" */
	@Column(name = "type_restriction")
	private String typeRestriction;
}
