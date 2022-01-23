<template>

  <div class="app-container">

    <el-row :gutter="24">

      <el-col :span="24">
        <el-card style="margin-bottom: 10px">

          距离考试结束还有：<exam-timer v-model="paperData.leftSeconds" @timeout="doHandler()" />
          <el-button style="float: right; margin-top: -10px" type="warning" icon="el-icon-plus" :loading="loading" @click="handHandExam()">
            {{ handleText }}
          </el-button>

        </el-card>
      </el-col>

      <el-col :span="6" :xs="24" style="margin-bottom: 10px">

        <el-card>

          <p class="card-title">答题卡</p>
          <el-row :gutter="24" class="card-line" style="padding-left: 10px">
            <el-tag type="info">未作答</el-tag>
            <el-tag type="success">已作答</el-tag>
          </el-row>

          <div v-if="paperData.radioList!==undefined && paperData.radioList.length > 0">
            <p class="card-title">单选题</p>
            <el-row :gutter="24" class="card-line">
              <el-tag v-for="item in paperData.radioList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)"> {{ item.sort+1 }}</el-tag>
            </el-row>
          </div>

          <div v-if="paperData.multiList!==undefined && paperData.multiList.length > 0">
            <p class="card-title">多选题</p>
            <el-row :gutter="24" class="card-line">
              <el-tag v-for="item in paperData.multiList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
            </el-row>
          </div>

          <div v-if="paperData.judgeList!==undefined && paperData.judgeList.length > 0">
            <p class="card-title">判断题</p>
            <el-row :gutter="24" class="card-line">
              <el-tag v-for="item in paperData.judgeList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
            </el-row>
          </div>

          <div v-if="paperData.blankList!==undefined && paperData.blankList.length > 0">
            <p class="card-title">填空题</p>
            <el-row :gutter="24" class="card-line">
              <el-tag v-for="item in paperData.blankList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
            </el-row>
          </div>

          <div v-if="paperData.wordList!==undefined && paperData.wordList.length > 0">
            <p class="card-title">操作题</p>
            <el-row :gutter="24" class="card-line">
              <el-tag v-for="item in paperData.wordList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
              <el-tag v-for="item in paperData.excelList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
              <el-tag v-for="item in paperData.pptList" :key="item.id" :type="cardItemClass(item.answered, item.quId)" @click="handSave(item)">{{ item.sort+1 }}</el-tag>
            </el-row>
          </div>

        </el-card>

      </el-col>

      <el-col :span="18" :xs="24">

        <el-card class="qu-content">
          <p v-if="quData.content">{{ quData.sort + 1 }}.<span v-html="quData.content" /></p>
          <div v-if="quData.image!=null && quData.image!==''">
            <el-link :href="quData.image" type="primary" target="_blank" icon="el-icon-files" :underline="true" style="margin-bottom: 10px">
              点击下载附件
            </el-link>
          </div>
          <div v-if="quData.quType === 1 || quData.quType===3">
            <el-radio-group v-model="radioValue">
              <el-radio v-for="item in quData.answerList" :key="item.id" :label="item.id">{{ item.abc }}.{{ item.content }}
                <div v-if="item.image!=null && item.image!==''" style="clear: both">
                  <el-image :src="item.image" style="max-width:100%;">
                    <div slot="error" class="image-slot">
                      <el-link :href="item.image" type="primary" target="_blank" icon="el-icon-files" :underline="true">
                        点击下载附件
                      </el-link>
                    </div>
                  </el-image>
                </div>
              </el-radio>
            </el-radio-group>
          </div>

          <div v-if="quData.quType === 2">
            <el-checkbox-group v-model="multiValue">
              <el-checkbox v-for="item in quData.answerList" :key="item.id" :label="item.id">{{ item.abc }}.{{ item.content }}
                <div v-if="item.image!=null && item.image!==''" style="clear: both">
                  <el-image :src="item.image" style="max-width:100%;">
                    <div slot="error" class="image-slot">
                      <el-link :href="item.image" type="success" target="_blank" icon="el-icon-files" :underline="true">
                        点击下载附件
                      </el-link>
                    </div>
                  </el-image>
                </div>
              </el-checkbox>
            </el-checkbox-group>

          </div>

          <div v-if="quData.quType === 5">
            <el-input v-model="answer" size="large" placeholder="请输入答案" style="width: 50%" clearable />
          </div>

          <div v-if="quData.quType >= 10">
            <file-upload
              v-model="answer"
              list-type="office-file"
              tips="请仔细读题,根据题目要求上传.xlsx/.docx/.pptx文件"
              accept=".xlsx, .docx, .pptx"
            />
          </div>

        </el-card>

        <div style="margin-top: 20px">
          <el-button v-if="showPrevious" type="primary" icon="el-icon-back" @click="handPrevious()">
            上一题
          </el-button>

          <el-button v-if="showNext" type="warning" icon="el-icon-right" @click="handNext()">
            下一题
          </el-button>

        </div>

      </el-col>

    </el-row>
  </div>

</template>

<script>
import { paperDetail, quDetail, handExam, fillAnswer } from '@/api/paper/exam'
import { Loading } from 'element-ui'
import FileUpload from '@/components/FileUpload'
import ExamTimer from '@/components/ExamTimer'
import { setWaterMark, removeWatermark } from '@/utils/watermark'
import { mapGetters } from 'vuex'

export default {
  name: 'ExamProcess',
  components: { FileUpload, ExamTimer },
  data() {
    return {
      // 全屏/不全屏
      isFullscreen: false,
      showPrevious: false,
      showNext: true,
      loading: false,
      handleText: '交卷',
      pageLoading: false,

      fullUrl: '',
      timer: null,
      websocket: null,

      // 试卷ID
      paperId: '',
      // 当前答题卡
      cardItem: {},
      allItem: [],
      // 当前题目内容
      quData: {
        answerList: []
      },
      // 试卷信息
      paperData: {
        leftSeconds: 99999,
        radioList: [],
        multiList: [],
        judgeList: [],
        blankList: [],
        // saqList: []
        wordList: [],
        excelList: [],
        pptList: []
      },
      // 单选选定值
      radioValue: '',
      // 多选选定值
      multiValue: [],
      // 操作题答案（文件路径）
      answer: '',
      // 已答ID
      answeredIds: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'userId',
      'name',
      'realName'
    ])
  },
  mounted() {
    this.initWebSocket()
    setWaterMark(this.realName, this.name)
    window.addEventListener('beforeunload', this.beforeUnloadHandler, false)
  },
  beforeDestroy() {
    this.$nextTick(() => {
      document.oncontextmenu = new Function()
      document.onselectstart = new Function()
    })
  },
  destroyed() {
    removeWatermark()
    window.removeEventListener('beforeunload', this.beforeUnloadHandler, false)
  },
  // 回退时无法弹窗
  created() {
    const id = this.$route.params.id
    if (typeof id !== 'undefined' && id !== null) {
      this.paperId = id
      this.fetchData(id)
    }
    history.pushState(null, null, document.URL)
    window.addEventListener('popstate', function() {
      history.pushState(null, null, document.URL)
    })
    this.$nextTick(() => {
      document.oncontextmenu = new Function('event.returnValue=false')
      document.onselectstart = new Function('event.returnValue=false')
    })
  },

  methods: {

    beforeUnloadHandler(e) {
      e = e || window.event
      e.returnValue = true // 此处返回任意字符串，不返回null即可，不能修改默认提示内容
    },

    // 答题卡样式
    cardItemClass(answered, quId) {
      if (quId === this.cardItem.quId) {
        return 'warning'
      }

      if (answered) {
        return 'success'
      }

      if (!answered) {
        return 'info'
      }
    },

    /**
     * 统计有多少题没答的
     * @returns {number}
     */
    countNotAnswered() {
      let notAnswered = 0

      this.paperData.radioList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      this.paperData.multiList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      this.paperData.judgeList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      // this.paperData.saqList.forEach(function(item) {
      //   if (!item.answered) {
      //     notAnswered += 1
      //   }
      // })

      this.paperData.blankList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      this.paperData.wordList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      this.paperData.excelList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      this.paperData.pptList.forEach(function(item) {
        if (!item.answered) {
          notAnswered += 1
        }
      })

      return notAnswered
    },

    /**
     * 下一题
     */
    handNext() {
      const index = this.cardItem.sort + 1
      this.handSave(this.allItem[index])
    },

    /**
     * 上一题
     */
    handPrevious() {
      const index = this.cardItem.sort - 1
      this.handSave(this.allItem[index])
    },

    doHandler() {
      this.handleText = '正在交卷，请等待...'
      this.loading = true

      const params = { id: this.paperId }
      handExam(params).then(() => {
        this.$message({
          message: '试卷提交成功！',
          type: 'success'
        })
        this.$router.push({ name: 'ExamOnline' })
      })
    },

    // 交卷操作
    handHandExam() {
      const that = this

      // 交卷保存答案
      this.handSave(this.cardItem, function() {
        const notAnswered = that.countNotAnswered()

        let msg = '确认要交卷吗？'

        if (notAnswered > 0) {
          msg = '您还有' + notAnswered + '题未作答，确认要交卷吗?'
        }

        that.$confirm(msg, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          that.doHandler()
        }).catch(() => {
          that.$message({
            type: 'info',
            message: '交卷已取消，您可以继续作答！'
          })
        })
      })
    },

    // 保存答案
    handSave(item, callback) {
      if (item.id === this.allItem[0].id) {
        this.showPrevious = false
      } else {
        this.showPrevious = true
      }

      // 最后一个索引
      const last = this.allItem.length - 1

      if (item.id === this.allItem[last].id) {
        this.showNext = false
      } else {
        this.showNext = true
      }

      const answers = this.multiValue
      if (this.radioValue !== '') {
        answers.push(this.radioValue)
      }

      const params = {
        quType: this.cardItem.quType,
        paperId: this.paperId,
        quId: this.cardItem.quId,
        answers: answers,
        answer: (this.cardItem.quType >= 10 || this.cardItem.quType === 5) ? this.answer : ''
      }
      fillAnswer(params).then(() => {
        // 必须选择一个值
        if (answers.length > 0 || params.answer !== '') {
          // 加入已答列表
          this.cardItem.answered = true
        } else {
          this.cardItem.answered = false
        }

        // 最后一个动作，交卷
        if (callback) {
          callback()
        }

        // 查找详情
        this.fetchQuData(item)
      })
    },

    // 试卷详情
    fetchQuData(item) {
      // 打开
      const loading = Loading.service({
        text: '拼命加载中',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      // 获得详情
      this.cardItem = item

      // 查找下个详情
      const params = { paperId: this.paperId, quId: item.quId }
      quDetail(params).then(response => {
        this.quData = response.data
        this.radioValue = ''
        this.multiValue = []

        // 填充该题目的答案
        this.quData.answerList.forEach((item) => {
          if ((this.quData.quType === 1 || this.quData.quType === 3) && item.checked) {
            this.radioValue = item.id
          }

          if (this.quData.quType === 2 && item.checked) {
            this.multiValue.push(item.id)
          }
        })
        if (this.quData.quType >= 10 || this.quData.quType === 5) { // 操作题 或 填空题
          this.answer = this.quData.answer
        }

        // 关闭详情
        loading.close()
      })
    },

    // 试卷详情
    fetchData(id) {
      const params = { id: id }
      paperDetail(params).then(response => {
        // 试卷内容
        this.paperData = response.data

        // 获得第一题内容
        if (this.paperData.radioList) {
          this.cardItem = this.paperData.radioList[0]
        } else if (this.paperData.multiList) {
          this.cardItem = this.paperData.multiList[0]
        } else if (this.paperData.judgeList) {
          this.cardItem = this.paperData.judgeList[0]
        } else if (this.paperData.blankList) {
          this.cardItem = this.paperData.blankList[0]
        } else if (this.paperData.wordList) {
          this.cardItem = this.paperData.wordList[0]
        } else if (this.paperData.excelList) {
          this.cardItem = this.paperData.excelList[0]
        } else if (this.paperData.pptList) {
          this.cardItem = this.paperData.pptList[0]
        }
        // else if (this.paperData.saqList) {
        //   this.cardItem = this.paperData.saqList[0]
        // }

        const that = this

        this.paperData.radioList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.multiList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.judgeList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.blankList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.wordList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.excelList.forEach(function(item) {
          that.allItem.push(item)
        })

        this.paperData.pptList.forEach(function(item) {
          that.allItem.push(item)
        })

        // this.paperData.saqList.forEach(function(item) {
        //   that.allItem.push(item)
        // })

        // 当前选定
        this.fetchQuData(this.cardItem)
      })
    },

    initWebSocket() {
      const api = `${process.env.VUE_APP_BASE_API}`
      const url = '/api/socket/paper/' + this.userId
      if (api === null || api === '') {
        this.fullUrl = ''.concat(location.protocol === 'https:' ? 'wss' : 'ws', '://').concat(location.host).concat(this.url)
      } else {
        // 同接口替换
        this.fullUrl = api.replace('https://', 'wss://').replace('http://', 'ws://')
        this.fullUrl = ''.concat(this.fullUrl).concat(url)
      } // 清理
      console.log(this.fullUrl)
      this.clear() // 连接socket

      this.connect()
    },
    connect() {
      console.log('++++connect', this.fullUrl)
      this.websocket = new WebSocket(this.fullUrl)
      this.websocket.onopen = this.onOpen
      this.websocket.onerror = this.onError
      this.websocket.onclose = this.onClose
      this.websocket.onmessage = this.onMessage
    },
    onOpen() {
      // 维持心跳
      this.beat()
    },
    onError(e) {
      console.log('socket error', e)
    },
    onClose(e) {
      console.log('socket error', e)
    },
    // 收到消息
    onMessage(e) {
      var data = e.data
      if (data !== 'pong') {
        this.$notify({
          title: '通知',
          message: data,
          type: 'warning',
          duration: 0
        })
      }
    },
    // 销毁资源
    clear() {
      // 断开上一个连接
      if (this.websocket != null && this.websocket.readyState === 1) {
        this.websocket.close()
      } // 取消任务
      if (this.timer != null) {
        clearInterval(this.timer)
      }
    },

    // 定时发送心跳
    beat() {
      const _this = this

      // 清理定时器
      if (this.timer != null) {
        clearInterval(this.timer)
      } // 10秒联系一次

      this.timer = setInterval(() => {
        // 发送消息
        _this.ping()
      }, 10000) // 首次触发

      this.ping()
    },

    ping() {
      if (this.websocket.readyState === 1) {
        // 首次触发
        this.websocket.send('ping')
      } else {
        // 只要错误就重新连接
        this.connect()
      }
    }

  }
}
</script>

<style scoped>

  .qu-content div{
    line-height: 30px;
  }

  .el-checkbox-group label,.el-radio-group label{
    width: 100%;
  }

  .card-title{
    background: #eee;
    line-height: 35px;
    text-align: center;
    font-size: 14px;
  }
  .card-line{
    padding-left: 10px
  }
  .card-line span {
    cursor: pointer;
    margin: 2px;
  }

  /deep/
  .el-radio, .el-checkbox{
    padding: 9px 20px 9px 10px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    margin-bottom: 10px;
  }

  .is-checked{
    border: #409eff 1px solid;
  }

  .el-radio img, .el-checkbox img{
    max-width: 200px;
    max-height: 200px;
    border: #dcdfe6 1px dotted;
  }

  /deep/
  .el-checkbox__inner {
    display: none;
  }

  /deep/
  .el-radio__inner{
    display: none;
  }

  /deep/
  .el-checkbox__label{
    line-height: 30px;
  }

  /deep/
  .el-radio__label{
    line-height: 30px;
  }

</style>

