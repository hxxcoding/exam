<template>

  <data-table
    ref="pagingTable"
    :options="options"
    :list-query="listQuery"
  >
    <template slot="filter-content">

      <el-select v-model="listQuery.params.openType" class="filter-item" placeholder="开放类型" clearable>
        <el-option
          v-for="item in openTypes"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>

      <el-date-picker
        v-model="listQuery.params.startTime"
        class="filter-item"
        value-format="yyyy-MM-dd HH:mm"
        type="date"
        placeholder="考试开始时间"
      />

      <el-date-picker
        v-model="listQuery.params.endTime"
        class="filter-item"
        value-format="yyyy-MM-dd HH:mm"
        type="date"
        placeholder="考试结束时间"
      />

      <el-input v-model="listQuery.params.title" placeholder="搜索考试名称" style="width: 200px;" class="filter-item" />

    </template>

    <template slot="data-columns">

      <el-table-column
        label="考试名称"
        prop="title"
        align="center"
        show-overflow-tooltip
      />

      <el-table-column
        label="考试类型"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.examType | examType }}
        </template>

      </el-table-column>

      <el-table-column
        label="开放类型"
        align="center"
      >
        <template slot-scope="scope">
          {{ scope.row.openType | examOpenType }}
        </template>

      </el-table-column>

      <el-table-column
        label="考试时间与时长"
        width="265px"
        align="center"
      >

        <template slot-scope="scope">
          <span v-if="scope.row.timeLimit">
            {{ scope.row.startTime }} ~ {{ scope.row.endTime }}
          </span>
          <span v-else>不限开始时间</span>
          (时长{{ scope.row.totalTime }}分钟)
        </template>

      </el-table-column>

      <el-table-column
        label="次数限制"
        align="center"
      >

        <template slot-scope="scope">
          <span v-if="scope.row.tryLimit">
            {{ scope.row.limitTimes }}
          </span>
          <span v-else>不限次</span>
        </template>

      </el-table-column>

      <el-table-column
        label="考试总分"
        prop="totalScore"
        align="center"
      />

      <el-table-column
        label="及格线"
        prop="qualifyScore"
        align="center"
      />

      <el-table-column
        label="状态"
        align="center"
      >

        <template slot-scope="scope">
          {{ scope.row.state | examStateFilter }}
        </template>

      </el-table-column>

      <el-table-column
        label="操作"
        align="center"
        width="220px"
      >
        <template slot-scope="scope">
          <el-button v-perm="['exam:save']" type="primary" size="mini" icon="el-icon-edit" @click="handleUpdateExam(scope.row.id)">修改</el-button>
          <el-button v-perm="['user:exam:paging']" type="warning" size="mini" icon="el-icon-user" @click="handleExamDetail(scope.row.id)">考试人员</el-button>
        </template>
      </el-table-column>

    </template>

  </data-table>

</template>

<script>
import DataTable from '@/components/DataTable'
import { checkPerms } from '@/utils/permission'

export default {
  name: 'ExamList',
  components: { DataTable },
  data() {
    return {

      openTypes: [
        {
          value: 1,
          label: '完全开放'
        },
        {
          value: 2,
          label: '指定部门'
        }
      ],

      listQuery: {
        current: 1,
        size: 10,
        params: {
          title: ''
        }
      },

      options: {
        // 可批量操作
        multi: true,
        // 批量操作列表
        multiActions: [
          {
            value: 'delete',
            label: '删除',
            hasPerm: checkPerms(['exam:delete'])
            // hasPerm: this.hasPerm('exam:delete')
          }, {
            value: 'enable',
            label: '启用',
            hasPerm: checkPerms(['exam:state'])
          },
          {
            value: 'disable',
            label: '禁用',
            hasPerm: checkPerms(['exam:state'])
          }
        ],
        // 列表请求URL
        listUrl: '/exam/api/exam/exam/paging',
        // 删除请求URL
        deleteUrl: '/exam/api/exam/exam/delete',
        // 状态请求URL
        stateUrl: '/exam/api/exam/exam/state',
        addRoute: 'SaveExam',
        addPerm: ['exam:save']
      }
    }
  },
  methods: {

    handleExamDetail(examId) {
      this.$router.push({ name: 'ListExamUser', query: { examId: examId }})
    },

    handleUpdateExam(examId) {
      this.$router.push({ name: 'SaveExam', query: { id: examId }})
    }
  }
}
</script>
