<template>

  <div>

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
      @multi-actions="handleMultiAction"
    >

      <template slot="filter-content">

        <el-input v-model="listQuery.params.userName" style="width: 200px" placeholder="搜索学号" class="filter-item" clearable />
        <el-input v-model="listQuery.params.realName" style="width: 200px" placeholder="搜索姓名" class="filter-item" clearable />
        <depart-tree-select
          v-model="listQuery.params.departId"
          class="el-select filter-item el-select--medium"
          :options="treeData"
          :props="defaultProps"
          placeholder="请选择班级/学院"
          width="200px"
        />

        <el-button class="filter-item" type="primary" icon="el-icon-plus" @click="handleAdd">
          添加
        </el-button>

        <el-button-group class="filter-item" style="float:  right">
          <el-button size="mini" icon="el-icon-download" @click="downloadTemplate">下载导入模版</el-button>
          <el-button size="mini" icon="el-icon-upload2" @click="chooseFile">导入</el-button>
          <input ref="upFile" class="file" name="file" type="file" style="display: none" @change="doImport">
        </el-button-group>

      </template>

      <template slot="data-columns">

        <el-table-column
          type="selection"
          width="55"
        />

        <el-table-column
          align="center"
          label="用户名"
        >
          <template slot-scope="scope">
            <a @click="handleUpdate(scope.row)">{{ scope.row.userName }}</a>
          </template>

        </el-table-column>

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
          label="角色"
          prop="roleIds"
        />

        <el-table-column
          align="center"
          label="创建时间"
          prop="createTime"
        />

        <el-table-column
          align="center"
          label="状态"
        >

          <template slot-scope="scope">
            {{ scope.row.state | stateFilter }}
          </template>
        </el-table-column>

      </template>
    </data-table>

    <el-dialog title="添加用户" :visible.sync="dialogVisible" width="500px">

      <el-form :model="formData" label-position="left" label-width="100px">

        <el-form-item label="用户名">
          <el-input v-model="formData.userName" />
        </el-form-item>

        <el-form-item label="姓名">
          <el-input v-model="formData.realName" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="formData.password" placeholder="不修改请留空" type="password" />
        </el-form-item>

        <el-form-item label="部门">
          <depart-tree-select v-model="formData.departId" :options="treeData" :props="defaultProps" />
        </el-form-item>

        <el-form-item label="角色">
          <meet-role v-model="formData.roles" />
        </el-form-item>

        <!--        <el-form-item label="头像" prop="avatar">-->

        <!--          <single-upload-->
        <!--            v-model="formData.avatar"-->
        <!--          />-->
        <!--        </el-form-item>-->

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </div>

    </el-dialog>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import MeetRole from '@/components/MeetRole'
import { saveData } from '@/api/sys/user/user'
import DepartTreeSelect from '@/components/DepartTreeSelect'
import { fetchTree } from '@/api/sys/depart/depart'
import { importTemplate, importExcel } from '@/api/sys/user/user'

export default {
  name: 'SysUserList',
  components: { DepartTreeSelect, DataTable, MeetRole },
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
        listUrl: '/exam/api/sys/user/paging',
        // 启用禁用
        stateUrl: '/exam/api/sys/user/state',
        deleteUrl: '/exam/api/sys/user/delete',
        // 批量操作列表
        multiActions: [
          {
            value: 'enable',
            label: '启用'
          }, {
            value: 'disable',
            label: '禁用'
          },
          {
            value: 'delete',
            label: '删除'
          }
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

  methods: {

    handleUploadSuccess(response) {
      // 上传图片赋值
      this.formData.avatar = response.data.url
    },

    handleAdd() {
      this.formData = {}
      this.dialogVisible = true
    },

    handleUpdate(row) {
      this.dialogVisible = true
      this.formData = row
      this.formData.roles = row.roleIds.split(',')
      this.formData.password = null
    },

    departSelected(data) {
      this.formData.departId = data.id
    },
    handleSave() {
      saveData(this.formData).then(() => {
        this.$message({
          type: 'success',
          message: '用户修改成功!'
        })
        this.dialogVisible = false
        this.$refs.pagingTable.getList()
      })
    },

    // 批量操作监听
    handleMultiAction(obj) {
      if (obj.opt === 'cancel') {
        this.handleCancelOrder(obj.ids)
      }
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
    },

    downloadTemplate() {
      importTemplate()
    },

    chooseFile: function() {
      this.$refs.upFile.dispatchEvent(new MouseEvent('click'))
    },

    doImport(e) {
      const file = e.target.files[0]

      importExcel(file).then(res => {
        if (res.code !== 0) {
          this.$alert(res.data.msg, '导入信息', {
            dangerouslyUseHTMLString: true
          })
        } else {
          this.$message({
            message: '成功导入' + res.data + '条数据',
            type: 'success'
          })
          this.importVisible = false
          this.$refs.pagingTable.getList()
        }
      })
    }
  }
}
</script>
