## 一、数据库设计

### ER图与数据库表（表名，各列名及数据类型，外键关系）：

![database diagram](/img/database_diagram.png)

### 说明：

1. user表包含Student、Teacher和Admin三类用户，以TYPE字段作为区分。
2. course表为课程，class表为班次，即每学期每班的开课；class表中有courseId字段但未标记为course表中id的外键，这是为了解耦，避免实体类过深的自动装载；其他表中的teacher_id、student_id等同理。
3. 学生与班次的选课关系在student_class_score表中体现。

## 二、架构设计

### 1、工程的项目结构截图：

后端项目结构：

![后端项目结构](/img/后端项目结构.png)

前端项目结构：

![前端项目结构](/img/前端项目结构.png)

### 2、是否使用框架：

后端使用Spring MVC作为主架构，数据层以Spring Data JPA框架为基础，部分扩展功能引入Hibernate框架，视图层（View层，在Spring MVC中即JSP）因前后端分离未使用。

### 3、前端页面是否使用框架：

使用Vue-cli框架进行开发、webpack框架进行打包、element-ui作为主要使用的组件库。

## 三、类设计

### 1、各包的类：名称及职责；（注：不要使用截图）

1. component包：

   Scheduler: 负责所有定时任务，如60s一次的自动开课检查。

2. controller包

   FileController: 负责接收与文件传递相关的请求，如课件、作业的上传和下载。

   HomeworkController: 负责接收与作业相关的请求，如作业的发布、成绩公布与修改。

   MainController: 负责其他所有请求。

3. dao包：包含与每个实体类一一对应的持久化操作类，数量较多不再一一列出。

4. enums包

   ClassState: 开课状态，包括未开课、开课中和已结课。

   OperationType: 操作类型，如添加、修改和删除。

   PublishMethod: 发布方法，如未发布、完全公开和仅可查看本人。

   Result: 方法执行结果，包括成功、失败、不存在、已存在。

   StudentType: 学生类型，包括本科生和研究生。

5. po包：包含与主要数据库表一一对应的实体类，数量较多不再一一列出。

6. service包

   ClassService: 负责与班次相关的业务。

   CourseService: 负责与课程相关的业务。

   FileService: 负责与文件传输相关的业务。

   MessageService: 负责与消息收发相关的业务。

   UserService: 负责与用户相关的业务。

7. util包

   MailUtil: 负责注册验证邮件的发送。

8. vo包：包含各种与前端交互的纯数据对象，数量较多不再一一列出。

### 2、各前端的页面：名称及功能：（注：不要使用截图）

1. home: 不显示，仅作为登陆后的跳转页使用。
2. login: 登录页面。
3. MyClasses: 学生主页面，展示学生已选的所有课程概况和点击后的详情页面。
4. TakeClasses: 学生选课页面，展示所有可选课程和已选课程。
5.  MyInformation: 学生信息页面，展示学生个人信息、个人统计信息以及消息。
6. TeacherCourses: 教师主页面，展示教师的所有课程和所有班次概况，以及点击后的详情页面。
7. TeacherInfo: 教师信息页面，展示教师个人信息、个人统计信息以及消息。
8. review: 主管主页面，展示所有待审核的课程和班次。
9. statistics: 主管统计信息页面，展示学生、教师统计信息以及MyCourses使用情况。

## 四、其他

### 1、开发环境（数据库，服务器等）：

Win10操作系统、IntelliJ IDEA、WebStorm、MySQL数据库，Tomcat服务器。

### 2、开发心得体会：

1. 敏捷开发，尤其是单人敏捷开发，需要多做TODO标记来提醒自己哪些地方没有做完。
2. 单人开发最好顺着业务流程开发，方便测试，避免遗漏。

### 3、其他需要补充的：

单人开发项目很自由，可以很繁琐，也可以很暴力~。

### 4、运行截图

![login](/img/login.png)

![mycourses](/img/mycourses.png)

![takeclasses](/img/takeclasses.png)

![myinformation](/img/myinformation.png)

![teachercourses](/img/teachercourses.png)

![teacherclass](/img/teacherclass.png)

![teacherinfo](/img/teacherinfo.png)

![review](/img/review.png)

![statistics1](/img/statistics1.png)

![statistics2](/img/statistics2.png)