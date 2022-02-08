<template>
  <div class="app-container">

    <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="150px">

      <el-card>

        <el-form-item label="题目类型 " prop="quType">

          <el-select v-model="postForm.quType" :disabled="quTypeDisabled" class="filter-item" @change="handleTypeChange">
            <el-option
              v-for="item in quTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>

        </el-form-item>

        <el-form-item label="归属题库" prop="repoIds">

          <repo-select v-model="postForm.repoIds" :multi="true" />

        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <tinymce-editor v-model="postForm.content" />
        </el-form-item>

        <el-form-item label="试题附件">
          <file-upload v-model="postForm.image" />
        </el-form-item>

        <el-form-item label="整题解析" prop="oriPrice">
          <el-input v-model="postForm.analysis" type="textarea" :precision="1" :max="999999" />
        </el-form-item>

      </el-card>

      <div v-if="postForm.quType <= 3" class="filter-container" style="margin-top: 25px">

        <el-button v-if="postForm.quType !== 3" class="filter-item" type="primary" icon="el-icon-plus" size="small" @click="handleAdd">
          添加
        </el-button>

        <el-table
          :data="postForm.answerList"
          :border="true"
          style="width: 100%;"
        >
          <el-table-column
            label="是否答案"
            width="120"
            align="center"
          >
            <template slot-scope="scope">

              <el-checkbox v-model="scope.row.isRight">答案</el-checkbox>

            </template>

          </el-table-column>

          <el-table-column
            v-if="itemImage"
            label="选项图片"
            width="120px"
            align="center"
          >
            <template slot-scope="scope">

              <file-upload
                v-model="scope.row.image"
              />

            </template>
          </el-table-column>

          <el-table-column
            label="答案内容"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.content" type="textarea" />
            </template>
          </el-table-column>

          <el-table-column
            label="答案解析"
          >
            <template slot-scope="scope">
              <el-input v-model="scope.row.analysis" type="textarea" />
            </template>
          </el-table-column>

          <el-table-column
            label="操作"
            align="center"
            width="100px"
          >
            <template slot-scope="scope">
              <el-button type="danger" icon="el-icon-delete" circle @click="removeItem(scope.$index)" />
            </template>
          </el-table-column>

        </el-table>

      </div>

      <div v-if="postForm.quType === 5" class="filter-container" style="margin-top: 25px">
        <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="150px">
          <el-card>
            <el-form-item label="填空题答案" prop="answer">
              <el-tag
                v-for="tag in blankAnswerTags"
                :key="tag"
                closable
                :disable-transitions="false"
                @close="handleTagClose(tag)"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputTagVisible"
                ref="saveTagInput"
                v-model="inputTagValue"
                class="input-new-tag"
                size="small"
                @keyup.enter.native="handleInputTagConfirm"
                @blur="handleInputTagConfirm"
              />
              <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 更多答案</el-button>
            </el-form-item>
          </el-card>
        </el-form>
      </div>

      <div v-if="postForm.quType === 10" class="filter-container" style="margin-top: 25px">
        <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="150px">
          <el-alert
            title="如无解析按钮,请检查附件格式是否正确！"
            type="error"
            style="margin-bottom: 10px"
          />
          <el-card>
            <el-form-item label="答案附件">
              <file-upload v-model="postForm.answer" list-type="file" accept=".docx" />
            </el-form-item>
            <el-form-item
              v-if="postForm.answer != null && postForm.answer.endsWith('.docx')"
              label="操作"
              prop="image"
            >
              <el-button class="filter-item" size="small" type="primary" @click="wordParagraphsAnalyze">
                解析答案附件
              </el-button>
              <el-button v-if="wordParagraphs.length !== 0" class="filter-item" size="small" type="primary" icon="el-icon-plus" @click="handleOfficeAnswerAdd">
                添加判分点
              </el-button>
            </el-form-item>

            <el-table
              v-if="wordParagraphs.length !== 0"
              :data="postForm.officeAnswerList"
              :border="false"
              empty-text="请点击上面的`解析`进行设置"
              style="width: 100%; margin-top: 15px"
            >
              <el-table-column label="选择段落" align="center">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.pos" placeholder="请选择">
                    <el-option
                      v-for="item in wordParagraphs"
                      :key="item.pos"
                      style="width: 500px;"
                      :label="item.paragraph"
                      :value="item.pos"
                    />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="选择方法" align="center">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.method"
                  >
                    <el-option
                      v-for="item in methods"
                      :key="item"
                      :label="item | wordMethodFilter"
                      :value="item"
                    />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="提取格式" align="center" width="100">
                <template slot-scope="scope">
                  <el-button size="small" @click="readFormat(scope.row)">
                    提取格式
                  </el-button>
                </template>
              </el-table-column>

              <el-table-column label="设置答案" align="center">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" disabled />
                </template>
              </el-table-column>

              <el-table-column label="设置得分" align="center">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.score" :min="0" style="width: 150px" />
                </template>
              </el-table-column>

              <el-table-column label="删除" align="center" width="60">
                <template slot-scope="scope">
                  <el-button type="danger" icon="el-icon-delete" circle @click="removeOfficeAnswerItem(scope.$index)" />
                </template>
              </el-table-column>
            </el-table>

          </el-card>
        </el-form>
      </div>

      <div v-if="postForm.quType === 11" class="filter-container" style="margin-top: 25px">
        <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="150px">
          <el-card>
            <el-form-item label="答案附件">
              <file-upload v-model="postForm.answer" list-type="file" accept=".xlsx" />
            </el-form-item>
            <el-form-item
              v-if="postForm.answer != null && postForm.answer.endsWith('.xlsx')"
              label="操作"
              prop="image"
            >
              <el-button class="filter-item" size="small" type="primary" icon="el-icon-plus" @click="handleOfficeAnswerAdd">
                添加判分点
              </el-button>
            </el-form-item>

            <el-table
              :data="postForm.officeAnswerList"
              :border="false"
              empty-text="请点击上方`添加判分点`进行答案"
              style="width: 100%; margin-top: 15px"
            >
              <el-table-column label="目标单元格" align="center">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.pos" placeholder="例:`D9`" clearable @blur="handleCellInputChange(scope.row)" />
                </template>
              </el-table-column>

              <el-table-column label="选择方法" align="center">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.method"
                  >
                    <el-option
                      v-for="item in methods"
                      :key="item"
                      :label="item | excelMethodFilter"
                      :value="item"
                    />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="提取格式" align="center" width="100">
                <template slot-scope="scope">
                  <el-button size="small" :disabled="scope.row.pos.length < 2" @click="readFormat(scope.row)">
                    提取格式
                  </el-button>
                </template>
              </el-table-column>

              <el-table-column label="设置答案" align="center">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" disabled />
                </template>
              </el-table-column>

              <el-table-column label="设置得分" align="center">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.score" :min="0" style="width: 150px" />
                </template>
              </el-table-column>

              <el-table-column label="删除" align="center" width="60">
                <template slot-scope="scope">
                  <el-button type="danger" icon="el-icon-delete" circle @click="removeOfficeAnswerItem(scope.$index)" />
                </template>
              </el-table-column>
            </el-table>

          </el-card>
        </el-form>
      </div>

      <div v-if="postForm.quType === 12" class="filter-container" style="margin-top: 25px">
        <el-form ref="postForm" :model="postForm" :rules="rules" label-position="left" label-width="150px">
          <el-alert
            title="如无解析按钮,请检查附件格式是否正确！"
            type="error"
            style="margin-bottom: 10px"
          />
          <el-card>
            <el-form-item label="答案附件">
              <file-upload v-model="postForm.answer" list-type="file" accept=".pptx" />
            </el-form-item>
            <el-form-item
              v-if="postForm.answer != null && postForm.answer.endsWith('.pptx')"
              label="操作"
              prop="image"
            >
              <el-button class="filter-item" size="small" type="primary" @click="pptSlidesAnalyze">
                解析答案附件
              </el-button>
              <el-button v-if="pptSlides.length !== 0" class="filter-item" size="small" type="primary" icon="el-icon-plus" @click="handleOfficeAnswerAdd">
                添加判分点
              </el-button>
            </el-form-item>

            <el-table
              v-if="pptSlides.length !== 0"
              :data="postForm.officeAnswerList"
              :border="false"
              empty-text="请点击上面的`解析`进行设置"
              style="width: 100%; margin-top: 15px"
            >
              <el-table-column label="选择段落" align="center">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.pos" placeholder="请选择">
                    <el-option
                      v-for="item in pptSlides"
                      :key="item.pos"
                      :label="item.slideName"
                      :value="item.pos"
                    />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="选择方法" align="center">
                <template slot-scope="scope">
                  <el-select
                    v-model="scope.row.method"
                  >
                    <el-option
                      v-for="item in methods"
                      :key="item"
                      :label="item | pptMethodFilter"
                      :value="item"
                    />
                  </el-select>
                </template>
              </el-table-column>

              <el-table-column label="提取格式" align="center" width="100">
                <template slot-scope="scope">
                  <el-button size="small" @click="readFormat(scope.row)">
                    提取格式
                  </el-button>
                </template>
              </el-table-column>

              <el-table-column label="设置答案" align="center">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.answer" disabled />
                </template>
              </el-table-column>

              <el-table-column label="设置得分" align="center">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.score" :min="0" style="width: 150px" />
                </template>
              </el-table-column>

              <el-table-column label="删除" align="center" width="60">
                <template slot-scope="scope">
                  <el-button type="danger" icon="el-icon-delete" circle @click="removeOfficeAnswerItem(scope.$index)" />
                </template>
              </el-table-column>
            </el-table>

          </el-card>
        </el-form>
      </div>

      <div style="margin-top: 20px">
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button type="info" @click="onCancel">返回</el-button>
      </div>

    </el-form>

  </div>
</template>

<script>
import { fetchDetail, saveData, wordParagraphsAnalyze, pptSlidesAnalyze, fetchQuOfficeMethods, readFormat } from '@/api/qu/qu'
import RepoSelect from '@/components/RepoSelect'
import FileUpload from '@/components/FileUpload'
import TinymceEditor from '@/components/TinymceEditor'

export default {
  name: 'QuDetail',
  components: { FileUpload, RepoSelect, TinymceEditor },
  data() {
    return {

      quTypeDisabled: false,
      itemImage: true,

      methods: [],

      wordParagraphs: [],

      pptSlides: [],

      // levels: [
      //   { value: 1, label: '普通' },
      //   { value: 2, label: '较难' }
      // ],

      quTypes: [
        {
          value: 1,
          label: '单选题'
        }, {
          value: 2,
          label: '多选题'
        },
        {
          value: 3,
          label: '判断题'
        },
        // {
        //   value: 4,
        //   label: '操作题'
        // },
        {
          value: 5,
          label: '填空题'
        },
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

      postForm: {
        repoIds: [],
        tagList: [],
        answerList: [],
        officeAnswerList: []
      },

      blankAnswerTags: [],

      inputTagVisible: false,

      inputTagValue: '',

      rules: {
        content: [
          { required: true, message: '题目内容不能为空！' }
        ],

        quType: [
          { required: true, message: '题目类型不能为空！' }
        ],

        // level: [
        //   { required: true, message: '必须选择难度等级！' }
        // ],

        repoIds: [
          { required: true, message: '至少要选择一个题库！' }
        ]
      }
    }
  },
  created() {
    const id = this.$route.query.id
    if (typeof id !== 'undefined') {
      this.quTypeDisabled = true
      this.fetchData(id)
    }
  },
  methods: {

    handleTypeChange(v) {
      this.postForm.answerList = []
      if (v === 5) { // 填空题
        this.postForm.answerList.push({ isRight: true, content: '', analysis: '填空题答案内容请使用英文半角符号\';\'分割。例如 1;2;3;' })
      }

      if (v === 3) {
        this.postForm.answerList.push({ isRight: true, content: '正确', analysis: '' })
        this.postForm.answerList.push({ isRight: false, content: '错误', analysis: '' })
      }

      if (v === 1 || v === 2) {
        this.postForm.answerList.push({ isRight: false, content: '', analysis: '' })
        this.postForm.answerList.push({ isRight: false, content: '', analysis: '' })
        this.postForm.answerList.push({ isRight: false, content: '', analysis: '' })
        this.postForm.answerList.push({ isRight: false, content: '', analysis: '' })
      }
    },

    // 添加子项
    handleAdd() {
      this.postForm.answerList.push({ isRight: false, content: '', analysis: '' })
    },

    removeItem(index) {
      this.postForm.answerList.splice(index, 1)
    },

    fetchData(id) {
      fetchDetail(id).then(response => {
        this.postForm = response.data
        if (this.postForm.quType >= 10) {
          this.fetchQuOfficeMethods()
        }
        if (this.postForm.quType === 10 && this.postForm.id !== null && this.postForm.id !== '' &&
          this.postForm.answer !== null && this.postForm.answer !== '') {
          this.wordParagraphsAnalyze()
        }
        if (this.postForm.quType === 12 && this.postForm.id !== null && this.postForm.id !== '' &&
          this.postForm.answer !== null && this.postForm.answer !== '') {
          this.pptSlidesAnalyze()
        }
        if (this.postForm.quType === 5 && this.postForm.answer !== null) {
          this.blankAnswerTags = this.postForm.answer.split(';')
        }
      })
    },
    submitForm() {
      let rightCount = 0

      this.postForm.answerList.forEach(function(item) {
        if (item.isRight) {
          rightCount += 1
        }
      })

      if (this.postForm.quType === 1) {
        if (rightCount !== 1) {
          this.$message({
            message: '单选题答案只能有一个',
            type: 'warning'
          })

          return
        }
      }

      if (this.postForm.quType === 2) {
        if (rightCount < 2) {
          this.$message({
            message: '多选题至少要有两个正确答案！',
            type: 'warning'
          })

          return
        }
      }

      if (this.postForm.quType === 3) {
        if (rightCount !== 1) {
          this.$message({
            message: '判断题只能有一个正确项！',
            type: 'warning'
          })

          return
        }
      }

      if (this.postForm.quType === 5) {
        if (this.blankAnswerTags.length === 0) {
          this.$message({
            message: '填空题不能没有正确项！',
            type: 'warning'
          })

          return
        } else {
          this.postForm.answer = ''
          this.blankAnswerTags.forEach(tag => {
            this.postForm.answer += tag + ';'
          })
          this.postForm.answer = this.postForm.answer.substring(0, this.postForm.answer.length - 1)
        }
      }

      this.$refs.postForm.validate((valid) => {
        if (!valid) {
          return
        }

        saveData(this.postForm).then(response => {
          this.postForm = response.data
          this.$notify({
            title: '成功',
            message: '试题保存成功！',
            type: 'success',
            duration: 2000
          })

          this.$router.push({ name: 'ListQu' })
        })
      })
    },
    onCancel() {
      this.$router.push({ name: 'ListQu' })
    },

    wordParagraphsAnalyze() {
      wordParagraphsAnalyze(this.postForm.answer)
        .then(response => {
          this.wordParagraphs = response.data
          this.wordParagraphs[this.wordParagraphs.length] = {
            paragraph: '全文格式',
            pos: undefined
          }
          this.fetchQuOfficeMethods()
          this.$notify({
            title: '成功',
            message: '解析文件成功！',
            type: 'success',
            duration: 2000
          })
        })
    },

    pptSlidesAnalyze() {
      pptSlidesAnalyze(this.postForm.answer)
        .then(response => {
          this.pptSlides = response.data
          this.fetchQuOfficeMethods()
          this.$notify({
            title: '成功',
            message: '解析文件成功！',
            type: 'success',
            duration: 2000
          })
        })
    },

    fetchQuOfficeMethods() {
      fetchQuOfficeMethods(this.postForm.quType).then(response => {
        this.methods = response.data
      })
    },

    readFormat(row) {
      readFormat(this.postForm.answer, row.pos, row.method)
        .then(response => {
          row.answer = response.data
          this.$notify({
            title: '成功',
            message: '读取格式为:' + response.data,
            type: 'success',
            duration: 2000
          })
        })
    },

    handleOfficeAnswerAdd() {
      this.postForm.officeAnswerList.push({
        pos: this.postForm.quType === 10 ? undefined : '',
        method: '',
        answer: '',
        score: 0
      })
    },

    removeOfficeAnswerItem(index) {
      this.postForm.officeAnswerList.splice(index, 1)
    },

    handleTagClose(tag) {
      this.blankAnswerTags.splice(this.blankAnswerTags.indexOf(tag), 1)
    },

    showInput() {
      this.inputTagVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },

    handleInputTagConfirm() {
      if (this.inputTagValue) {
        this.blankAnswerTags.push(this.inputTagValue)
      }
      this.inputTagVisible = false
      this.inputTagValue = ''
    },

    handleCellInputChange(row) {
      const pattern = /^[A-Z]+[0-9]+$/g
      if (pattern.exec(row.pos) === null) {
        if (/^[a-z]+[0-9]+$/g.exec(row.pos)) {
          row.pos = row.pos.toUpperCase()
        } else {
          row.pos = ''
        }
      }
    }

  }
}
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>

