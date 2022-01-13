<template>

  <div>

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
    >
      <template slot="data-columns">

        <el-table-column
          label="角色ID"
          align="center"
          prop="id"
        />

        <el-table-column
          label="角色名称"
          align="center"
          prop="roleName"
        />

        <el-table-column align="center" label="操作">
          <template slot-scope="scope">
            <el-button v-if="scope.row.id !== 'sa'" type="text" icon="el-icon-unlock" @click="handleUpdate(scope.row)">菜单授权</el-button>
          </template>
        </el-table-column>

      </template>

    </data-table>

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog title="修改角色" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event)">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event)">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect($event)">父子联动</el-checkbox>
          <el-tree
            ref="menu"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中,请稍候"
            :props="defaultProps"
          />
        </el-form-item>
      </el-form>

      <el-alert
        title="`角色管理`,`菜单管理`为系统高危权限,请谨慎操作！"
        type="error"
      />

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import { listMenuTreeByRole } from '@/api/sys/menu/menu'
import { updateRoleMenu } from '@/api/sys/role/role'

export default {
  name: 'SysRoleList',
  components: { DataTable },
  data() {
    return {

      listQuery: {
        current: 1,
        size: 10,
        params: {
        }
      },
      open: false,
      form: {},
      // 菜单列表
      menuOptions: [],

      openDataScope: false,
      menuExpand: false,
      menuNodeAll: false,
      deptExpand: true,
      deptNodeAll: false,

      options: {
        // 列表请求URL
        listUrl: '/exam/api/sys/role/paging',
        // 启用禁用
        stateUrl: '/sys/user/state'
      }
    }
  },

  methods: {

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.open = true
      this.form.roleName = row.roleName
      this.form.id = row.id
      const roleMenu = this.listMenuTreeByRole(row.id)
      this.$nextTick(() => {
        roleMenu.then(res => {
          const checkedKeys = res.checkedKeys
          checkedKeys.forEach((v) => {
            this.$nextTick(() => {
              this.$refs.menu.setChecked(v, true, false)
            })
          })
        })
      })
    },

    /** 根据角色ID查询菜单树结构 */
    listMenuTreeByRole(roleId) {
      return listMenuTreeByRole(roleId).then(response => {
        this.menuOptions = response.data.tree
        return response.data
      })
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id !== undefined) {
            this.form.menuIds = this.getMenuAllCheckedKeys()
            updateRoleMenu(this.form).then(response => {
              this.$notify({
                title: '成功',
                message: '修改成功！',
                type: 'success',
                duration: 2000
              })
              this.open = false
            })
          }
        }
      })
    },

    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      const checkedKeys = this.$refs.menu.getCheckedKeys()
      // 半选中的菜单节点
      const halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys()
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys)
      return checkedKeys
    },

    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value) {
      const treeList = this.menuOptions
      for (let i = 0; i < treeList.length; i++) {
        this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value) {
      this.$refs.menu.setCheckedNodes(value ? this.menuOptions : [])
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value) {
      this.form.menuCheckStrictly = !!value
    },
    // 表单重置
    reset() {
      if (this.$refs.menu !== undefined) {
        this.$refs.menu.setCheckedKeys([])
      }
      this.menuExpand = false
      this.menuNodeAll = false
      this.deptExpand = true
      this.deptNodeAll = false
      this.form = {
        roleId: undefined,
        roleName: undefined,
        roleKey: undefined,
        roleSort: 0,
        status: '0',
        menuIds: [],
        deptIds: [],
        menuCheckStrictly: true,
        deptCheckStrictly: true,
        remark: undefined
      }
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    }
  }
}
</script>
