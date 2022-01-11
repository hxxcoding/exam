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
        />

        <el-table-column
          label="操作人"
          prop="userName"
        />

        <el-table-column
          label="IP"
          prop="ip"
        />

        <el-table-column
          label="操作时间"
          prop="createTime"
        />

      </template>

    </data-table>
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

      listQuery: {
        current: 1,
        size: 10,
        params: {
        }
      },

      options: {
        // 列表请求URL
        listUrl: '/exam/api/sys/log/paging'
      },

      types: [
        {
          value: '登录系统',
          label: '登录系统'
        }
      ]
    }
  },

  methods: {

    // 批量操作监听
    handleMultiAction(obj) {
      if (obj.opt === 'cancel') {
        this.handleCancelOrder(obj.ids)
      }
    }
  }
}
</script>
