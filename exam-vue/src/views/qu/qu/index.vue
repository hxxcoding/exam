<template>

  <div>

    <data-table
      ref="pagingTable"
      :options="options"
      :list-query="listQuery"
      @multi-actions="handleMultiAction"
    >
      <template slot="filter-content">

        <el-row>
          <el-col :span="24">

            <el-select v-model="listQuery.params.quType" class="filter-item" clearable>
              <el-option
                v-for="item in quTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>

            <repo-select v-model="listQuery.params.repoIds" :multi="true" />

            <el-input v-model="listQuery.params.content" placeholder="题目内容" style="width: 200px;" class="filter-item" />

            <el-button-group class="filter-item" style="float:  right">
              <el-button v-perm="['qu:import']" size="mini" icon="el-icon-upload2" @click="showImport">导入</el-button>
              <el-button v-perm="['qu:export']" size="mini" icon="el-icon-download" @click="exportExcel">导出</el-button>
            </el-button-group>

          </el-col>
        </el-row>

      </template>

      <template slot="data-columns">

        <el-table-column
          label="题目类型"
          align="center"
          width="100px"
        >
          <template slot-scope="scope">
            {{ scope.row.quType | quTypeFilter() }}
          </template>
        </el-table-column>

        <el-table-column
          label="题目内容"
          show-overflow-tooltip
        >
          <template slot-scope="scope">
            <router-link slot="reference" :to="{ name: 'SaveQu', query:{ id: scope.row.id}}" v-text="scope.row.content.replace(/<[^>]+>|&[^>]+;/g, '')" />
          </template>
        </el-table-column>

        <el-table-column
          label="创建时间"
          align="center"
          prop="createTime"
          width="180px"
        />

      </template>

    </data-table>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="30%"
    >

      <el-form label-position="left" label-width="100px">

        <el-form-item label="操作题库" prop="repoIds">
          <repo-select v-model="dialogRepos" :multi="true" />
        </el-form-item>

        <el-row>
          <el-button type="primary" @click="handlerRepoAction">保存</el-button>
        </el-row>

      </el-form>

    </el-dialog>

    <el-dialog
      title="导入试题"
      :visible.sync="importVisible"
      width="30%"
    >

      <el-row>
        <el-button type="primary" @click="chooseFile">上传导入</el-button>
        <el-button type="warning" @click="downloadTemplate">下载导入模板</el-button>
        <a :href="quFormatConversionFileUrl" style="margin-left: 10px">
          <el-button type="warning">下载试题转换模板</el-button>
        </a>
        <input ref="upFile" class="file" name="file" type="file" style="display: none" @change="doImport">
      </el-row>

      <el-alert
        title="导入试题暂仅支持`选择题``判断题``填空题`导入！"
        type="warning"
        style="margin-top: 20px"
      />

    </el-dialog>

  </div>

</template>

<script>
import DataTable from '@/components/DataTable'
import RepoSelect from '@/components/RepoSelect'
import { batchAction } from '@/api/qu/repo'
import { exportExcel, importExcel, importTemplate } from '@/api/qu/qu'
import { checkPerms } from '@/utils/permission'

export default {
  name: 'QuList',
  components: { RepoSelect, DataTable },
  data() {
    return {

      dialogTitle: '加入题库',
      dialogVisible: false,
      importVisible: false,
      dialogRepos: [],
      dialogQuIds: [],
      dialogFlag: false,

      listQuery: {
        current: 1,
        size: 10,
        params: {
          content: '',
          quType: '',
          repoIds: []
        }
      },

      quTypes: [
        {
          value: 1,
          label: '单选题'
        },
        {
          value: 2,
          label: '多选题'
        },
        {
          value: 3,
          label: '判断题'
        },
        {
          value: 5,
          label: '填空题'
        },
        // {
        //   value: 4,
        //   label: '简答题'
        // }
        {
          value: 10,
          label: 'Word操作题'
        },
        {
          value: 11,
          label: 'Excel操作题'
        },
        {
          value: 12,
          label: 'PPT操作题'
        }
      ],

      quFormatConversionFileUrl: `${process.env.VUE_APP_BASE_API}/upload/file/static/qu/import/qu-import-template-choice-qu-format-conversion.xlsm`,

      options: {

        // 可批量操作
        multi: true,

        // 批量操作列表
        multiActions: [
          {
            value: 'delete',
            label: '删除',
            hasPerm: checkPerms(['qu:delete'])
          },
          {
            value: 'update-repo',
            label: '修改所属题库..',
            hasPerm: checkPerms(['repo:batch-action'])
          },
          {
            value: 'remove-repo',
            label: '从..题库移除',
            hasPerm: checkPerms(['repo:batch-action'])
          }
        ],
        // 列表请求URL
        listUrl: '/exam/api/qu/qu/paging',
        // 删除请求URL
        deleteUrl: '/exam/api/qu/qu/delete',
        deleteMsg: '删除题目可能导致已作答试卷无法查看,确认删除吗？',
        // 添加数据路由
        addRoute: 'SaveQu',
        addPerm: ['qu:save']
      }
    }
  },
  methods: {

    handleMultiAction(obj) {
      if (obj.opt === 'update-repo') {
        this.dialogTitle = '修改所属题库'
        this.dialogFlag = false
      }

      if (obj.opt === 'remove-repo') {
        this.dialogTitle = '从题库移除'
        this.dialogFlag = true
      }

      this.dialogVisible = true
      this.dialogQuIds = obj.ids
    },

    handlerRepoAction() {
      const postForm = { repoIds: this.dialogRepos, quIds: this.dialogQuIds, remove: this.dialogFlag }

      batchAction(postForm).then(() => {
        this.$notify({
          title: '成功',
          message: '批量操作成功！',
          type: 'success',
          duration: 2000
        })

        this.dialogVisible = false
        this.$refs.pagingTable.getList()
      })
    },

    exportExcel() {
      // 导出当前查询的数据
      exportExcel(this.listQuery.params)
    },

    downloadTemplate() {
      importTemplate()
    },

    showImport() {
      this.importVisible = true
    },

    // 只是为了美化一下导入按钮
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

<style lang="scss">
.el-tooltip__popper{max-width:50%}
</style>
