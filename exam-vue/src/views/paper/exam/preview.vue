<template>
  <div class="app-container">

    <el-row :gutter="24">

      <el-col :span="24" style="margin-bottom: 20px">

        <el-alert
          v-if="detailData.examType === 1"
          title="点击`开始考试`后将自动进入考试，请诚信考试！考试途中请勿关闭或退出考试页面！如若关闭请及时联系监考老师！"
          type="error"
          style="margin-bottom: 10px"
        />

        <el-card class="pre-exam">

          <div><strong>考试名称：</strong>{{ detailData.title }}</div>
          <div><strong>考试时长：</strong>{{ detailData.totalTime }}分钟</div>
          <div><strong>考试时段：</strong>
            <span v-if="detailData.timeLimit">{{ detailData.startTime }} ~ {{ detailData.endTime }}</span>
            <span v-else>不限时</span>
          </div>
          <div><strong>次数限制：</strong>
            <span v-if="detailData.tryLimit">{{ detailData.limitTimes }}次</span>
            <span v-else>不限次</span>
          </div>
          <div><strong>试卷总分：</strong>{{ detailData.totalScore }}分</div>
          <div><strong>及格分数：</strong>{{ detailData.qualifyScore }}分</div>
          <div><strong>考试描述：</strong>{{ detailData.content }}</div>
          <div><strong>考试类型：</strong> {{ detailData.examType | examType }}</div>
          <div><strong>开放类型：</strong> {{ detailData.openType | examOpenType }}</div>
          <div>
            <strong>考场座位：</strong>
            <el-input v-model="postForm.seat" placeholder="请输入考场座位号(例:3-021)" style="width: 250px; vertical-align: bottom" clearable @blur="handleSeatInputChange" />
          </div>
          <div v-if="detailData.isStart && detailData.examType === 1">
            <strong>考试密码：</strong>
            <el-input v-model="postForm.password" placeholder="请联系监考老师输入考试密码！" style="width: 250px; vertical-align: bottom" show-password clearable />
          </div>

        </el-card>

      </el-col>

      <el-col :span="24">
        <el-alert
          v-if="detailData.isStart && detailData.examType === 1"
          title="您有未完成的的考试, `继续考试`请联系监考老师输入考试密码！"
          type="warning"
          style="margin-bottom: 10px"
        />
      </el-col>

      <el-col :span="24">

        <el-button
          v-if="!detailData.isStart"
          type="primary"
          icon="el-icon-caret-right"
          :disabled="postForm.seat === ''"
          @click="handleCreate"
        >
          开始考试
        </el-button>

        <el-button
          v-if="detailData.isStart"
          type="warning"
          icon="el-icon-video-pause"
          :disabled="(detailData.examType === 1 && (postForm.password === null || postForm.password === '' || postForm.seat === ''))
            || (detailData.examType === 0 && (postForm.seat === ''))"
          @click="handleCreate"
        >
          继续考试
        </el-button>

        <el-button @click="handleBack">
          返回
        </el-button>

      </el-col>

    </el-row>
  </div>
</template>

<script>
import { Loading } from 'element-ui'
import { fetchPreview } from '@/api/exam/exam'
import { createPaper } from '@/api/paper/exam'

export default {
  name: 'ExamPrepare',
  data() {
    return {
      detailData: {},
      postForm: {
        examId: '',
        password: '',
        seat: ''
      }
    }
  },

  created() {
    this.postForm.examId = this.$route.query.examId
    this.fetchData()
  },

  methods: {

    fetchData() {
      fetchPreview(this.postForm.examId).then(response => {
        this.detailData = response.data
        if (this.detailData.isStart) {
          this.postForm.seat = this.detailData.seat
        }
      })
    },

    handleCreate() {
      const that = this
      if (!this.handleSeatInputChange()) {
        return
      }
      if (this.detailData.examType === 1 && this.detailData.isStart && (this.postForm.password === null || this.postForm.password === '')) {
        this.$message({
          message: '请输入考试密码!',
          type: 'warning'
        })
        return
      }
      // 打开
      const loading = Loading.service({
        text: '正在进入考试...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      createPaper(this.postForm).then(response => {
        if (response.code === 0) {
          this.$message({
            message: '试卷读取成功，即将进入考试！',
            type: 'success'
          })

          setTimeout(function() {
            loading.close()
            that.dialogVisible = false
            that.$router.push({ name: 'StartExam', params: { id: response.data.id }})
          }, 1000)
        }
      }).catch(() => {
        loading.close()
      })
    },

    handleBack() {
      this.$router.push({ name: 'ExamOnline' })
    },

    handleSeatInputChange() {
      const pattern = /^[0-9]-[0-9]{3}$/g
      if (pattern.exec(this.postForm.seat) === null) {
        this.postForm.seat = ''
        this.$message({
          message: '座位号输入格式有误, 请重新输入!',
          type: 'warning'
        })
        return false
      }
      return true
    }

  }
}
</script>

<style scoped>

  .pre-exam div {

    line-height: 42px;
    color: #555555;
  }

</style>

