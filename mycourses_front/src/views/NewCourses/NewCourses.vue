<template>
  <el-container>
    <el-header>
      <teacher-nav :name="name"></teacher-nav>
    </el-header>
    <el-main>
      <!--所有选课-->
      <el-card v-for="(myClass, i) of myClasses" :key="i" class="box-card" v-show="isProfile">
        <div slot="header">
          <el-button type="text" @click="clickClass(myClass.classId)">{{myClass.name}}</el-button>
        </div>
        <div v-for="(homework, i2) in myClass.homework" :key="i2" class="text"><i class="el-icon-edit-outline"></i>
          <span class="item">尚未提交的作业：{{homework.name}}</span>
          <span class="item">&nbsp;&nbsp;&nbsp;&nbsp;截止日期：{{homework.deadline}}</span>
        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'NewCourses',
  data () {
    return {
      name: this.$cookies.get('email')
    }
  }
}
</script>

<style scoped>

</style>
