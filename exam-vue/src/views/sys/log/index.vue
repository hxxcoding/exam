<template>

  <div>

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
    >

      <template slot="filter-content">
        <el-input v-model="listQuery.params.title" clearable placeholder="搜索日志" style="width: 200px;" class="filter-item" />
        <el-input v-model="listQuery.params.userName" clearable placeholder="操作人" style="width: 200px;" class="filter-item" />
      </template>

      <template slot="data-columns">

        <el-table-column
          label="日志类型"
          prop="title"
          align="center"
        />

        <el-table-column
          label="操作人"
          prop="userName"
          align="center"
        />

        <el-table-column
          label="IP"
          prop="ip"
          align="center"
        />

        <el-table-column
          label="操作时间"
          prop="createTime"
          align="center"
        />

        <el-table-column
          label="操作"
          align="center"
        >
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-search" @click="displayLogData(scope.row.data)">查看明细</el-button>
          </template>
        </el-table-column>

      </template>

    </data-table>

    <el-dialog
      title="日志明细"
      :visible.sync="dialogVisible"
      width="40%"
    >
      <el-input
        v-model="logData"
        type="textarea"
        :rows="15"
        placeholder="请输入内容"
      />
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>

</template>

<script>
import DataTable from '@/components/DataTable'

export default {
  name: 'SysLogList',
  components: { DataTable },
  filters: {

    // 订单状态
    userState(value) {
      const map = {
        '0': '正常',
        '1': '禁用'
      }
      return map[value]
    }
  },
  data() {
    return {

      dialogVisible: false,
      logData: '',

      listQuery: {
        current: 1,
        size: 10,
        params: {
        }
      },

      options: {
        // 列表请求URL
        listUrl: '/exam/api/sys/log/paging'
      }
    }
  },

  methods: {

    displayLogData(data) {
      this.dialogVisible = true
      this.logData = JSON.stringify(JSON.parse(data), null, 4)
    }

  }
}
</script>
