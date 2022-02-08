<template>
  <div class="app-container">

    <h3>组卷信息</h3>
    <el-card style="margin-top: 20px">

      <div style="float: right; font-weight: bold; color: #ff0000">试卷总分：{{ postForm.totalScore }}分</div>

      <div>

        <el-button class="filter-item" size="small" type="primary" icon="el-icon-plus" @click="handleAdd">
          添加题库
        </el-button>

        <el-table
          :data="repoList"
          :border="false"
          empty-text="请点击上面的`添加题库`进行设置"
          style="width: 100%; margin-top: 15px"
        >
          <el-table-column
            label="题库"
            width="200"
          >
            <template slot-scope="scope">
              <repo-select v-model="scope.row.repoId" :multi="false" @change="repoChange($event, scope.row)" />
            </template>

          </el-table-column>
          <el-table-column
            label="选项"
            align="center"
          >
            <div>题目数量</div>
            <div style="margin-top: 20px">每题分数</div>
          </el-table-column>

          <el-table-column
            label="单选题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.radioCount" :min="0" :max="scope.row.totalRadio" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalRadio + '题'" />
              <el-input-number v-model="scope.row.radioScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="多选题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.multiCount" :min="0" :max="scope.row.totalMulti" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalMulti + '题'" />
              <el-input-number v-model="scope.row.multiScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="判断题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.judgeCount" :min="0" :max="scope.row.totalJudge" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalJudge + '题'" />
              <el-input-number v-model="scope.row.judgeScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="填空题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.blankCount" :min="0" :max="scope.row.totalBlank" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalBlank + '题'" />
              <el-input-number v-model="scope.row.blankScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="Word题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.wordCount" :min="0" :max="scope.row.totalWord" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalWord + '题'" />
              <el-input-number v-model="scope.row.wordScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="Excel题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.excelCount" :min="0" :max="scope.row.totalExcel" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalExcel + '题'" />
              <el-input-number v-model="scope.row.excelScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="PPT题"
            align="center"
          >

            <template slot-scope="scope">
              <el-input-number v-model="scope.row.pptCount" :min="0" :max="scope.row.totalPPT" :controls="false" style="width: 100%" :placeholder="'总' + scope.row.totalPPT + '题'" />
              <el-input-number v-model="scope.row.pptScore" :min="0" :controls="false" style="width: 100%; margin-top: 10px" />
            </template>

          </el-table-column>

          <el-table-column
            label="删除"
            align="center"
            width="80px"
          >
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" circle @click="removeItem(scope.$index)" />
            </template>
          </el-table-column>

        </el-table>

      </div>

    </el-card>

    <h3>考试配置</h3>
    <el-card style="margin-top: 20px">

      <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="120px">

        <el-form-item label="考试名称" prop="title">
          <el-input v-model="postForm.title" />
        </el-form-item>

        <el-form-item label="考试描述" prop="content">
          <el-input v-model="postForm.content" type="textarea" />
        </el-form-item>

        <el-form-item label="总分数" prop="totalScore">
          <el-input-number :value="postForm.totalScore" disabled />
        </el-form-item>

        <el-form-item label="及格分" prop="qualifyScore">
          <el-input-number v-model="postForm.qualifyScore" :max="postForm.totalScore" />
        </el-form-item>

        <el-form-item label="考试时长(分钟)" prop="totalTime">
          <el-input-number v-model="postForm.totalTime" />
        </el-form-item>

        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="postForm.examType" placeholder="请选择考试类型" class="filter-item">
            <el-option
              v-for="item in examTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="postForm.examType !== undefined" label="是否限时">
          <el-checkbox v-model="postForm.timeLimit" :checked="postForm.examType === 1" />
        </el-form-item>

        <el-form-item v-if="postForm.timeLimit" label="考试时间" prop="timeLimit">

          <el-date-picker
            v-model="dateValues"
            format="yyyy-MM-dd HH:mm"
            value-format="yyyy-MM-dd HH:mm"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />

        </el-form-item>

        <div v-if="postForm.examType === 0">
          <el-form-item label="是否限次" prop="tryLimit">
            <el-checkbox v-model="postForm.tryLimit" />
          </el-form-item>

          <el-form-item v-if="postForm.tryLimit" label="限制次数" prop="limitTimes">
            <el-input-number v-model="postForm.limitTimes" :min="1" :max="100" />
          </el-form-item>
        </div>

        <div v-if="postForm.examType === 1">
          <el-form-item label="考试密码" prop="password">
            <el-input v-model="postForm.password" placeholder="请输入密码" style="width: 50%;" show-password />
          </el-form-item>
        </div>

      </el-form>

    </el-card>

    <h3>权限配置</h3>
    <el-card style="margin-top: 20px;">

      <el-radio-group v-model="postForm.openType" style="margin-bottom: 20px">
        <el-radio :label="1" border>完全公开</el-radio>
        <el-radio :label="2" border>部门开放</el-radio>
      </el-radio-group>

      <el-alert
        v-if="postForm.openType===1"
        title="开放的，任何人都可以进行考试！"
        type="warning"
      />

      <div v-if="postForm.openType===2">
        <el-input
          v-model="filterText"
          placeholder="输入关键字进行过滤"
        />

        <el-tree

          ref="tree"
          v-loading="treeLoading"
          empty-text=" "
          :data="treeData"
          default-expand-all
          show-checkbox
          node-key="id"
          :default-checked-keys="postForm.departIds"
          :props="defaultProps"
          :filter-node-method="filterNode"
          @check-change="handleCheckChange"
        />

      </div>

    </el-card>

    <div style="margin-top: 20px">
      <el-button type="primary" @click="handleSave">保存</el-button>
    </div>

  </div>
</template>

<script>
import { fetchDetail, saveData } from '@/api/exam/exam'
import { fetchRepoDetail } from '@/api/qu/repo'
import { fetchTree } from '@/api/sys/depart/depart'
import RepoSelect from '@/components/RepoSelect'

export default {
  name: 'ExamDetail',
  components: { RepoSelect },
  data() {
    return {

      examTypes: [
        {
          value: 0,
          label: '模拟练习'
        },
        {
          value: 1,
          label: '正式考试'
        }
      ],

      step: 1,
      treeData: [],
      defaultProps: {
        label: 'deptName'
      },
      filterText: '',
      treeLoading: false,
      dateValues: [],
      quDialogShow: false,
      quDialogType: 1,
      excludes: [],

      scoreDialog: false,
      scoreBatch: 0,

      // 题库
      repoList: [],

      // 题目列表
      quList: [[], [], [], [], [], [], []],
      quEnable: [false, false, false, false, false, false, false],

      postForm: {
        // 总分数
        totalScore: 0,
        // 题库列表
        repoList: [],
        // 题目列表
        quList: [],
        // 组题方式
        joinType: 1,
        // 开放类型
        openType: 1,
        // 考试班级列表
        departIds: []

      },
      rules: {
        title: [
          { required: true, message: '考试名称不能为空！' }
        ],

        content: [
          { required: true, message: '考试内容不能为空！' }
        ],

        open: [
          { required: true, message: '考试权限不能为空！' }
        ],

        totalScore: [
          { required: true, message: '考试分数不能为空！' }
        ],

        qualifyScore: [
          { required: true, message: '及格分不能为空！' }
        ],

        totalTime: [
          { required: true, message: '考试时长不能为空！' }
        ],

        timeLimit: [
          { required: true, message: '考试时间不能为空！' }
        ],

        limitTimes: [
          { required: true, message: '限制次数不能为空！' }
        ],

        ruleId: [
          { required: true, message: '考试规则不能为空' }
        ],

        examType: [
          { required: true, message: '考试类型不能为空！' }
        ],

        password: [
          { required: true, message: '正式考试的考试密码不能为空！' }
        ]
      }
    }
  },

  watch: {

    filterText(val) {
      this.$refs.tree.filter(val)
    },

    dateValues: {

      handler() {
        this.postForm.startTime = this.dateValues[0]
        this.postForm.endTime = this.dateValues[1]
      }
    },

    // 题库变换
    repoList: {
      handler() {
        const that = this

        that.postForm.totalScore = 0

        this.repoList.forEach(function(item) {
          that.postForm.totalScore += item.radioCount * item.radioScore
          that.postForm.totalScore += item.multiCount * item.multiScore
          that.postForm.totalScore += item.judgeCount * item.judgeScore
          // that.postForm.totalScore += item.saqCount * item.saqScore
          that.postForm.totalScore += item.blankCount * item.blankScore
          that.postForm.totalScore += item.wordCount * item.wordScore
          that.postForm.totalScore += item.excelCount * item.excelScore
          that.postForm.totalScore += item.pptCount * item.pptScore
        })

        // 赋值
        this.postForm.repoList = this.repoList
      },
      deep: true
    }

  },
  created() {
    const id = this.$route.query.id
    if (typeof id !== 'undefined') {
      this.fetchData(id)
    }

    fetchTree({}).then(response => {
      this.treeData = response.data
    })
  },
  methods: {

    handleSave() {
      this.$refs.postForm.validate((valid) => {
        if (!valid) {
          return
        }

        if (this.postForm.totalScore === 0) {
          this.$notify({
            title: '提示信息',
            message: '考试规则设置不正确，请确认！',
            type: 'warning',
            duration: 2000
          })

          return
        }

        if (this.postForm.joinType === 1) {
          for (let i = 0; i < this.postForm.repoList.length; i++) {
            const repo = this.postForm.repoList[i]

            if (!repo.repoId) {
              this.$notify({
                title: '提示信息',
                message: '考试题库选择不正确！',
                type: 'warning',
                duration: 2000
              })

              return
            }

            if ((repo.radioCount > 0 && repo.radioScore === 0) || (repo.radioCount === 0 && repo.radioScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的单选题配置！',
                type: 'warning',
                duration: 2000
              })

              return
            }

            if ((repo.multiCount > 0 && repo.multiScore === 0) || (repo.multiCount === 0 && repo.multiScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的多选题配置！',
                type: 'warning',
                duration: 2000
              })

              return
            }

            if ((repo.judgeCount > 0 && repo.judgeScore === 0) || (repo.judgeCount === 0 && repo.judgeScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的判断题配置！',
                type: 'warning',
                duration: 2000
              })
              return
            }

            if ((repo.blankCount > 0 && repo.blankScore === 0) || (repo.blankCount === 0 && repo.blankScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的填空题配置！',
                type: 'warning',
                duration: 2000
              })
              return
            }

            if ((repo.wordCount > 0 && repo.wordScore === 0) || (repo.wordCount === 0 && repo.wordScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的Word题配置！',
                type: 'warning',
                duration: 2000
              })
              return
            }

            if ((repo.excelCount > 0 && repo.excelScore === 0) || (repo.excelCount === 0 && repo.excelScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的Excel题配置！',
                type: 'warning',
                duration: 2000
              })
              return
            }

            if ((repo.pptCount > 0 && repo.pptScore === 0) || (repo.pptCount === 0 && repo.pptScore > 0)) {
              this.$notify({
                title: '提示信息',
                message: '题库第：[' + (i + 1) + ']项存在无效的PPT题配置！',
                type: 'warning',
                duration: 2000
              })
              return
            }
          }
        }

        this.$confirm('确实要提交保存吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitForm()
        })
      })
    },

    handleCheckChange() {
      const that = this
      // 置空
      this.postForm.departIds = []

      const nodes = this.$refs.tree.getCheckedNodes()
      nodes.forEach(function(item) {
        that.postForm.departIds.push(item.id)
      })
    },

    // 添加子项
    handleAdd() {
      this.repoList.push({ rowId: new Date().getTime(), radioCount: 0, radioScore: 0,
        multiCount: 0, multiScore: 0, judgeCount: 0, judgeScore: 0, blankCount: 0, blankScore: 0,
        wordCount: 0, wordScore: 0, excelCount: 0, excelScore: 0, pptCount: 0, pptScore: 0 })
    },

    removeItem(index) {
      this.repoList.splice(index, 1)
    },

    fetchData(id) {
      const that = this

      fetchDetail(id).then(response => {
        this.postForm = response.data

        if (this.postForm.startTime && this.postForm.endTime) {
          this.dateValues[0] = this.postForm.startTime
          this.dateValues[1] = this.postForm.endTime
        }

        // 按分组填充题目
        if (this.postForm.joinType === 2) {
          this.postForm.quList.forEach(function(item) {
            const index = item.quType - 1
            that.quList[index].push(item)
            that.quEnable[index] = true
          })
        }

        if (this.postForm.joinType === 1) {
          that.repoList = that.postForm.repoList
          that.repoList.forEach(repo => {
            fetchRepoDetail({
              id: repo.repoId
            }).then(response => {
              repo.totalRadio = response.data.radioCount
              repo.totalMulti = response.data.multiCount
              repo.totalJudge = response.data.judgeCount
              repo.totalBlank = response.data.blankCount
              repo.totalWord = response.data.wordCount
              repo.totalExcel = response.data.excelCount
              repo.totalPPT = response.data.pptCount
            })
          })
        }
      })
    },

    submitForm() {
      // 校验和处理数据
      this.postForm.repoList = this.repoList
      // 正式考试的考试默认限制考试次数一次
      if (this.postForm.examType === 1) {
        this.postForm.tryLimit = true
        this.postForm.limitTimes = 1
      }

      saveData(this.postForm).then(() => {
        this.$notify({
          title: '成功',
          message: '考试保存成功！',
          type: 'success',
          duration: 2000
        })

        this.$router.push({ name: 'ListExam' })
      })
    },

    filterNode(value, data) {
      if (!value) return true
      return data.deptName.indexOf(value) !== -1
    },

    repoChange(e, row) {
      if (e != null) {
        row.totalRadio = e.radioCount
        row.totalMulti = e.multiCount
        row.totalJudge = e.judgeCount
        row.totalBlank = e.blankCount
        row.totalWord = e.wordCount
        row.totalExcel = e.excelCount
        row.totalPPT = e.pptCount
      } else {
        row.totalRadio = 0
        row.totalMulti = 0
        row.totalJudge = 0
        row.totalBlank = 0
        row.totalWord = 0
        row.totalExcel = 0
        row.totalPPT = 0
      }
    }

  }
}
</script>

<style scoped>

</style>

