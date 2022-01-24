<template>

  <div>

    <el-alert
      title="当前数据每5秒自动刷新一次,更改查询条件可立即刷新！"
      type="warning"
      style="margin-top: 10px; margin-left: 20px; width: 95%"
    />

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
    >

      <template slot="filter-content">

        <el-input v-model="listQuery.params.userName" style="width: 130px" placeholder="搜索学号" class="filter-item" clearable />
        <el-input v-model="listQuery.params.realName" style="width: 130px" placeholder="搜索姓名" class="filter-item" clearable />
        <depart-tree-select
          v-model="listQuery.params.departId"
          class="el-select filter-item el-select--medium"
          :options="treeData"
          :props="defaultProps"
          placeholder="请选择班级/学院"
        />

        <el-button class="filter-item" type="primary" style="float: right" icon="el-icon-s-promotion" @click="handleOpenDialog">
          发送全员消息
        </el-button>

      </template>

      <template slot="data-columns">

        <el-table-column
          align="center"
          label="学号"
          prop="userName"
        />

        <el-table-column
          align="center"
          label="姓名"
          prop="realName"
        />

        <el-table-column
          align="center"
          label="班级/学院"
          prop="deptName"
        >
          <template slot-scope="scope">
            <div>{{ queryTree(treeData, scope.row.departId) }}</div>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="状态"
        >答题中
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
        >

          <template slot-scope="scope">
            <el-button class="filter-item" type="primary" icon="el-icon-s-promotion" @click="handleOpenDialog(scope.row)">
              发送消息
            </el-button>
          </template>
        </el-table-column>

      </template>
    </data-table>

    <el-dialog title="发送通知" :visible.sync="dialogVisible" width="500px">

      <el-form ref="sendMessage" :model="formData" :rules="rules" label-position="left" label-width="100px">

        <el-form-item label="通知内容" prop="message">
          <el-input
            v-model="formData.message"
            type="textarea"
          />
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSendMsg">发 送</el-button>
      </div>

    </el-dialog>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import { sendMsg } from '@/api/paper/monitor'
import DepartTreeSelect from '@/components/DepartTreeSelect'
import { fetchTree } from '@/api/sys/depart/depart'

export default {
  name: 'Monitor',
  components: { DepartTreeSelect, DataTable },
  data() {
    return {

      treeData: [],
      defaultProps: {
        value: 'id',
        label: 'deptName',
        children: 'children'
      },
      dialogVisible: false,

      listQuery: {
        current: 1,
        size: 10,
        params: {
        }
      },

      formData: {
        avatar: ''
      },

      options: {
        // 列表请求URL
        listUrl: '/exam/api/exam/exam/online-user/paging'
      },

      rules: {
        message: [
          { required: true, message: '消息内容不能为空！' }
        ]
      },

      deptName: ''
    }
  },

  created() {
    fetchTree({}).then(response => {
      this.treeData = response.data
    })
  },

  mounted() {
    // 每5秒自动查询一次数据
    setInterval(() => {
      this.$refs.pagingTable.getList(false)
    }, 5000)
  },

  methods: {

    handleOpenDialog(row = undefined) {
      this.formData = {}
      if (row !== undefined) {
        this.formData.userId = row.id
      }
      this.dialogVisible = true
    },

    departSelected(data) {
      this.formData.departId = data.id
    },
    handleSendMsg() {
      this.$refs.sendMessage.validate((valid) => {
        if (!valid) {
          return
        }
        sendMsg(this.formData.message, this.formData.userId).then(() => {
          this.$notify({
            title: '成功',
            message: '通知发送成功！',
            type: 'success',
            duration: 2000
          })
          this.dialogVisible = false
        })
      })
    },

    // 搜索树状数据中的 ID
    queryTree(tree, id) {
      let stark = []
      stark = stark.concat(tree)
      while (stark.length) {
        const temp = stark.shift()
        if (temp['children']) {
          stark = stark.concat(temp['children'])
        }
        if (temp['id'] === id) {
          return temp['deptName']
        }
      }
      return ''
    }
  }
}
</script>
