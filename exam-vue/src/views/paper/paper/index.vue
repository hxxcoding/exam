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
          placeholder="请选择班级/学院"
        />
        <el-select v-model="listQuery.params.state" placeholder="考试状态" class="filter-item" clearable>
          <el-option
            v-for="item in paperStates"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>

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
          label="人员"
          align="center"
          prop="userId_real_name"
        />

        <el-table-column
          label="部门"
          align="center"
          prop="departId_dept_name"
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

      </template>

    </data-table>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import DepartTreeSelect from '@/components/DepartTreeSelect'
import { fetchTree } from '@/api/sys/depart/depart'
import ExamSelect from '@/components/ExamSelect'

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
            value: 'saveToPdf',
            label: '保存为PDF'
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
      if (params.opt === 'saveToPdf') {
        params.ids.forEach(id => {
          const routeData = this.$router.resolve({
            name: 'ExamResult',
            query: {
              id: id,
              isPrint: true
            }
          })
          window.open(routeData.href, '_blank')
        })
      }
    }
  }
}
</script>
