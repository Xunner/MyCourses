<template>
  <el-container>
    <el-header>
      <admin-nav :name="name"></admin-nav>
    </el-header>
    <el-main>
      <div id="userPieChart" style="width: 95%;height: 400px"></div>
      <div id="loginCount" style="margin-top: 40px;width: 95%;height: 400px"></div>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'statistics',
  beforeCreate () {
    /* HTTP请求 */
    this.$http.get('/MyCourses/statistics', {'params': {'adminId': this.$cookies.get('userId')}}).then((res) => {
      if (res.data.result === 'SUCCESS') {
        this.userPieChartOpinion = res.data.userPieChartOpinion
        this.userPieChartSeries = res.data.userPieChartSeries
        this.loginCountX = res.data.loginCountX
        this.loginCountOpinion = res.data.loginCountOpinion
        this.loginCountSeries = res.data.loginCountSeries
      } else {
        this.$message.error('网络错误，请刷新或稍后再试')
      }
      this.userPieChart.setOption({
        title: {text: 'MyCourses 用户统计', subtext: '用户种类比例图', x: 'center'},
        tooltip: {trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)'},
        legend: {orient: 'vertical', x: 'left', data: this.userPieChartOpinion},
        toolbox: {
          show: true,
          feature: {
            mark: {show: true},
            dataView: {show: true, readOnly: false},
            magicType: {
              show: true,
              type: ['pie', 'funnel'],
              option: {funnel: {x: '25%', width: '50%', funnelAlign: 'left'}}
            },
            saveAsImage: {show: true},
            restore: {show: true}
          }
        },
        calculable: true,
        series: [{name: '用户数量', type: 'pie', radius: '55%', center: ['50%', '60%'], data: this.userPieChartSeries}
        ]
      })
      this.loginCount.setOption({
        title: {text: 'MyCourses 每日登陆次数统计', subtext: '日活', x: 'center'},
        tooltip: {trigger: 'axis'},
        legend: {orient: 'vertical', x: 'left', data: this.loginCountOpinion},
        toolbox: {
          show: true,
          feature: {
            mark: {show: true},
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
            saveAsImage: {show: true},
            restore: {show: true}
          }
        },
        calculable: true,
        xAxis: [{type: 'category', boundaryGap: false, data: this.loginCountX}],
        yAxis: [{type: 'value'}],
        series: [{
          name: '教师',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: this.loginCountSeries[0]
        }, {
          name: '研究生',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: this.loginCountSeries[1]
        }, {
          name: '本科生',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: this.loginCountSeries[2]
        }]
      })
    }, () => {
      this.$message.error('网络错误，请刷新或稍后再试')
      this.userPieChart.setOption(this.userPieChartOption)
      this.loginCount.setOption(this.loginCountOption)
    })
  },
  mounted () {
    if (this.$cookies.isKey('userId')) {
      this.$nextTick(() => {
        this.userPieChart = this.$echarts.init(document.getElementById('userPieChart'))
        this.loginCount = this.$echarts.init(document.getElementById('loginCount'))
      })
    } else {
      /* 如果cookie不存在，则跳转到登录页 */
      this.$router.push('/login')
    }
  },
  data () {
    return {
      name: this.$cookies.get('email'),
      userPieChartOpinion: ['教师', '本科生', '研究生'],
      userPieChartSeries: [
        {value: 1024, name: '教师'},
        {value: 4096, name: '本科生'},
        {value: 2048, name: '研究生'}
      ],
      loginCountX: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      loginCountOpinion: ['教师', '本科生', '研究生'],
      loginCountSeries: [
        [120, 132, 101, 134, 90, 230, 210],
        [220, 182, 191, 234, 290, 330, 310],
        [150, 232, 201, 154, 190, 330, 410]
      ],
      userPieChartOption: {
        title: {text: 'MyCourses 用户统计', subtext: '用户种类比例图', x: 'center'},
        tooltip: {trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)'},
        legend: {orient: 'vertical', x: 'left', data: ['教师', '本科生', '研究生']},
        toolbox: {
          show: true,
          feature: {
            mark: {show: true},
            dataView: {show: true, readOnly: false},
            magicType: {
              show: true,
              type: ['pie', 'funnel'],
              option: {funnel: {x: '25%', width: '50%', funnelAlign: 'left'}}
            },
            saveAsImage: {show: true},
            restore: {show: true}
          }
        },
        calculable: true,
        series: [{
          name: '用户数量',
          type: 'pie',
          radius: '55%',
          center: ['50%', '60%'],
          data: [{value: 1024, name: '教师'}, {value: 4096, name: '本科生'}, {value: 2048, name: '研究生'}]
        }
        ]
      },
      loginCountOption: {
        title: {text: 'MyCourses 每日登陆次数统计', subtext: '日活', x: 'center'},
        tooltip: {trigger: 'axis'},
        legend: {orient: 'vertical', x: 'left', data: this.loginCountOpinion},
        toolbox: {
          show: true,
          feature: {
            mark: {show: true},
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
            saveAsImage: {show: true},
            restore: {show: true}
          }
        },
        calculable: true,
        xAxis: [{type: 'category', boundaryGap: false, data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']}],
        yAxis: [{type: 'value'}],
        series: [{
          name: '教师',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: [120, 132, 101, 134, 90, 230, 210]
        }, {
          name: '研究生',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: [220, 182, 191, 234, 290, 330, 310]
        }, {
          name: '本科生',
          type: 'line',
          stack: '总量',
          itemStyle: {normal: {areaStyle: {type: 'default'}}},
          data: [150, 232, 201, 154, 190, 330, 410]
        }]
      }
    }
  }
}
</script>

<style scoped>

</style>
