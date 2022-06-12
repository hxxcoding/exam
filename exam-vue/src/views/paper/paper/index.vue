<template>

  <div>

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
      @multi-actions="handleMultiAction"
    >
      <template slot="filter-content" style="display: flex; align-items: flex-start">

        <exam-select v-model="listQuery.params.examId" class="filter-item" />

        <depart-tree-select
          v-model="listQuery.params.departId"
          class="el-select filter-item el-select--medium "
          :options="treeData"
          :props="defaultProps"
          width="200px"
          placeholder="请选择学期/教师/选课号"
        />
        <el-select v-model="listQuery.params.state" placeholder="考试状态" class="filter-item" clearable>
          <el-option
            v-for="item in paperStates"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

        <el-input v-model="listQuery.params.userName" placeholder="搜索学号" style="width: 200px;" class="filter-item" clearable />

        <el-button-group class="filter-item" style="float:  right">
          <el-button v-perm="['paper:export']" size="mini" icon="el-icon-download" @click="exportExcel">导出成绩</el-button>
        </el-button-group>

      </template>

      <template slot="data-columns">

        <el-table-column
          label="考试名称"
          align="center"
          prop="title"
        >

          <template slot-scope="scope">
            <router-link :to="{ name: 'ExamResult', query: {id : scope.row.id}}">
              {{ scope.row.title }}
            </router-link>

          </template>

        </el-table-column>

        <el-table-column
          label="姓名"
          align="center"
          prop="userId_real_name"
        />

        <el-table-column
          label="选课号"
          align="center"
          prop="departId_dept_name"
        />

        <el-table-column
          label="学号"
          align="center"
          prop="userId_user_name"
        />

        <el-table-column
          label="考场座位号"
          align="center"
          prop="seat"
        />

        <el-table-column
          label="考试时长(min)"
          align="center"
          prop="totalTime"
        >

          <template slot-scope="scope">
            {{ scope.row.userTime }} / {{ scope.row.totalTime }}
          </template>

        </el-table-column>

        <el-table-column
          label="考试得分"
          align="center"
        >

          <template slot-scope="scope">
            {{ scope.row.userScore }} / {{ scope.row.totalScore }}
          </template>

        </el-table-column>

        <el-table-column
          label="考试时间"
          align="center"
          prop="createTime"
          width="180px"
        />

        <el-table-column
          label="考试结果"
          align="center"
        >

          <template slot-scope="scope">
            <span v-if="scope.row.state===1">待阅卷</span>
            <span v-else-if="scope.row.state===0">待交卷</span>
            <span v-else>

              <span v-if="scope.row.userScore >= scope.row.qualifyScore" style="color:#00ff00">合格</span>
              <span v-else style="color: #ff0000">不合格</span>
            </span>
          </template>

        </el-table-column>

        <el-table-column
          label="考试状态"
          align="center"
        >

          <template slot-scope="scope">
            {{ scope.row.state | paperStateFilter }}
          </template>

        </el-table-column>

        <el-table-column
          label="操作"
          align="center"
          width="120px"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.state === 0"
              v-perm="['paper:hand-exam-by-force']"
              type="primary"
              size="mini"
              @click="handHandExamByForce(scope.row.id)"
            >
              强制交卷
            </el-button>
            <el-button
              v-if="scope.row.state === 2 && Date.now() < new Date(scope.row.limitTime)"
              v-perm="['paper:back-exam']"
              type="warning"
              size="mini"
              @click="handBackExam(scope.row.id)"
            >
              退回试卷
            </el-button>
          </template>

        </el-table-column>

      </template>

    </data-table>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import DepartTreeSelect from '@/components/DepartTreeSelect'
import { fetchTree } from '@/api/sys/depart/depart'
import { exportExcel, exportPaper } from '@/api/paper/paper'
import { handExamByForce, backExam } from '@/api/paper/exam'
import ExamSelect from '@/components/ExamSelect'
import { Loading } from 'element-ui'

export default {
  components: { ExamSelect, DepartTreeSelect, DataTable },

  data() {
    return {

      dialogVisible: false,

      captureList: [],

      paperStates: [
        { value: 0, label: '考试中' },
        { value: 1, label: '待阅卷' },
        { value: 2, label: '已考完' },
        { value: 3, label: '!已弃考' }
      ],
      treeData: [],
      defaultProps: {
        value: 'id',
        label: 'deptName',
        children: 'children'
      },

      listQuery: {
        current: 1,
        size: 10,
        params: {
          examId: ''
        }
      },

      options: {

        // 可批量操作
        multi: true,
        multiActions: [
          {
            value: 'exportPaper',
            label: '批量导出试卷'
          }
        ],
        // 列表请求URL
        listUrl: '/exam/api/paper/paper/paging'
      }
    }
  },

  created() {
    const examId = this.$route.query.examId

    if (typeof examId !== 'undefined') {
      this.listQuery.params.examId = examId
    }

    fetchTree({}).then(response => {
      this.treeData = response.data
    })
  },
  methods: {
    handleMultiAction(params) {
      const loading = Loading.service({
        text: '正在生成试卷并压缩,请耐心等待……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      if (params.opt === 'exportPaper') {
        exportPaper(params.ids).then(response => {
          const url = response.data
          window.open(url)
          loading.close()
        })
      }
    },

    handHandExamByForce(paperId) {
      const msg = '强制交卷后学生无法继续作答,确认强制交卷吗？'
      const data = {
        id: paperId
      }
      this.$confirm(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        handExamByForce(data).then(() => {
          this.$message({
            message: '强制交卷成功！',
            type: 'success'
          })
          this.$refs.pagingTable.getList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '强制交卷已取消，学生可继续作答！'
        })
      })
    },

    handBackExam(paperId) {
      const msg = '退回交卷后学生可以继续作答,确认退回吗？'
      const data = {
        id: paperId
      }
      this.$confirm(msg, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        backExam(data).then(() => {
          this.$message({
            message: '退回交卷成功！',
            type: 'success'
          })
          this.$refs.pagingTable.getList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '退回操作已取消！'
        })
      })
    },

    exportExcel() {
      exportExcel(this.listQuery.params)
    }
  }
}
</script>
