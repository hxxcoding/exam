<template>
  <div class="app-container">

    <h2 class="text-center">{{ paperData.title }}</h2>
    <p class="text-center" style="color: #666">{{ paperData.createTime }} ~ {{ paperData.updateTime }}</p>

    <el-row :gutter="24" style="margin-top: 30px">

      <el-col :span="6" class="text-center">
        考生姓名：{{ paperData.userId_dictText }}
      </el-col>

      <el-col :span="6" class="text-center">
        考生班级：{{ paperData.departId_dictText }}
      </el-col>

      <el-col :span="6" class="text-center">
        考试用时：{{ paperData.userTime }}分钟
      </el-col>

      <el-col :span="6" class="text-center">
        考试得分：{{ paperData.userScore }}
      </el-col>

    </el-row>

    <el-card style="margin-top: 20px">

      <div v-for="item in paperData.quList" :key="item.id" class="qu-content">

        <p>{{ item.sort + 1 }}.<span v-html="item.content" />（分值：{{ item.score }}）</p>
        <div v-if="item.image!=null && item.image!=''">
          <el-image :src="item.image" style="max-width:100%;">
            <div slot="error" class="image-slot">
              <el-link :href="item.image" type="primary" target="_blank" icon="el-icon-files" :underline="true">
                下载附件
              </el-link>
            </div>
          </el-image>
        </div>
        <div v-if="item.quType === 1 || item.quType===3">
          <el-radio-group v-model="radioValues[item.id]">
            <el-radio v-for="an in item.answerList" :key="an.id" :label="an.id">
              {{ an.abc }}.{{ an.content }}
              <div v-if="an.image!=null && an.image!==''" style="clear: both">
                <el-image :src="an.image" style="max-width:100%;">
                  <div slot="error" class="image-slot">
                    <el-link :href="an.image" type="primary" target="_blank" icon="el-icon-files" :underline="true">
                      下载附件
                    </el-link>
                  </div>
                </el-image>
              </div>
            </el-radio>
          </el-radio-group>

          <el-row :gutter="24">

            <el-col :span="12" style="color: #24da70">
              正确答案：{{ radioRights[item.id] }}
            </el-col>

            <el-col v-if="!item.answered" :span="12" style="text-align: right; color: #ff0000;">
              答题结果：未答
            </el-col>

            <el-col v-if="item.answered && !item.isRight" :span="12" style="text-align: right; color: #ff0000;">
              答题结果：{{ myRadio[item.id] }}
            </el-col>

            <el-col v-if="item.answered && item.isRight" :span="12" style="text-align: right; color: #24da70;">
              答题结果：{{ myRadio[item.id] }}
            </el-col>

          </el-row>

        </div>

        <div v-if="item.quType === 4">
          <el-row :gutter="24">
            <el-col :span="12">
              回答：
              <div v-if="item.answer!=null && item.answer!==''">
                <el-image :src="item.answer" style="max-width:100%;">
                  <div slot="error" class="image-slot">
                    <el-link :href="item.answer" type="primary" target="_blank" icon="el-icon-files" :underline="true">
                      {{ item.answer }}
                    </el-link>
                  </div>
                </el-image>
              </div>
            </el-col>
          </el-row>
        </div>

        <div v-if="item.quType === 5">
          <el-row :gutter="24">
            <el-col :span="12">
              回答：
              <el-input v-model="item.answer" size="large" style="width: 50%" disabled />
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="12" style="color: #24da70">
              正确答案：{{ blankValues[item.id] }}
            </el-col>
          </el-row>
        </div>

        <div v-if="item.quType === 2">
          <el-checkbox-group v-model="multiValues[item.id]">
            <el-checkbox v-for="an in item.answerList" :key="an.id" :label="an.id">{{ an.abc }}.{{ an.content }}
              <div v-if="an.image!=null && an.image!==''" style="clear: both">
                <el-image :src="an.image" style="max-width:100%;">
                  <div slot="error" class="image-slot">
                    <el-link :href="an.image" type="primary" target="_blank" icon="el-icon-files" :underline="true">
                      下载附件
                    </el-link>
                  </div>
                </el-image>
              </div>
            </el-checkbox>
          </el-checkbox-group>

          <el-row :gutter="24">

            <el-col :span="12" style="color: #24da70">
              正确答案：{{ multiRights[item.id].join(',') }}
            </el-col>

            <el-col v-if="!item.answered" :span="12" style="text-align: right; color: #ff0000;">
              答题结果：未答
            </el-col>

            <el-col v-if="item.answered && !item.isRight" :span="12" style="text-align: right; color: #ff0000;">
              答题结果：{{ myMulti[item.id].join(',') }}
            </el-col>

            <el-col v-if="item.answered && item.isRight" :span="12" style="text-align: right; color: #24da70;">
              答题结果：{{ myMulti[item.id].join(',') }}
            </el-col>

          </el-row>
        </div>

      </div>

    </el-card>

  </div>
</template>

<script>

import { paperResult } from '@/api/paper/exam'
import { setWaterMark, removeWatermark } from '@/utils/watermark'
import { mapGetters } from 'vuex'

export default {
  name: 'AuctionGoodsDetail',
  data() {
    return {
      // 试卷ID
      paperId: '',
      paperData: {
        quList: []
      },
      radioValues: {},
      multiValues: {},
      blankValues: {},
      radioRights: {},
      multiRights: {},
      myRadio: {},
      myMulti: {}
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'name',
      'realName'
    ])
  },
  created() {
    const id = this.$route.params.id
    if (typeof id !== 'undefined') {
      this.paperId = id
      this.fetchData(id)
    }
  },
  mounted() {
    setWaterMark(this.realName, this.name)
  },
  destroyed() {
    removeWatermark()
  },
  methods: {

    fetchData(id) {
      const params = { id: id }
      paperResult(params).then(response => {
        // 试卷内容
        this.paperData = response.data
        console.log(this.paperData)

        // 填充该题目的答案
        this.paperData.quList.forEach((item) => {
          let radioValue = ''
          let radioRight = ''
          let myRadio = ''
          const multiValue = []
          const multiRight = []
          const myMulti = []
          let blankValue = ''

          item.answerList.forEach((an) => {
            // 用户选定的
            if (an.checked) {
              if (item.quType === 1 || item.quType === 3) {
                radioValue = an.id
                myRadio = an.abc
              } else {
                multiValue.push(an.id)
                myMulti.push(an.abc)
              }
            }

            // 正确答案
            if (an.isRight) {
              if (item.quType === 1 || item.quType === 3) {
                radioRight = an.abc
              } else {
                multiRight.push(an.abc)
              }
            }

            if (item.quType === 5) {
              blankValue = an.content
            }
          })

          this.multiValues[item.id] = multiValue
          this.radioValues[item.id] = radioValue

          this.radioRights[item.id] = radioRight
          this.multiRights[item.id] = multiRight

          this.myRadio[item.id] = myRadio
          this.myMulti[item.id] = myMulti

          this.blankValues[item.id] = blankValue
        })

        console.log(this.multiValues)
        console.log(this.radioValues)
      })
    }
  }
}
</script>

<style scoped>

  .qu-content{

    border-bottom: #eee 1px solid;
    padding-bottom: 10px;

  }

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

</style>

