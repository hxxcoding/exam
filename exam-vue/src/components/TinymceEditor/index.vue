<template>
  <div>
    <editor id="tinymce" v-model="value" :init="init" />
  </div>
</template>

<script>
// 引入基础文件：
import tinymce from 'tinymce/tinymce'
import Editor from '@tinymce/tinymce-vue'
import 'tinymce/themes/silver'
import 'tinymce/plugins/image'// 插入上传图片插件
import 'tinymce/plugins/link'// 插入链接
import 'tinymce/plugins/code'// 插入代码
import 'tinymce/plugins/preview'// 插入预览
import { uploadFile } from '@/api/file/file'
export default {
  name: 'TinymceEditor',
  components: {
    Editor
  },
  props: {
    value: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      init: {
        language_url: '/tinymce/langs/zh_CN.js', // 语言包的路径
        language: 'zh_CN', // 语言
        skin_url: '/tinymce/skins/ui/oxide', // skin路径
        height: 150, // 编辑器高度
        branding: false, // 是否禁用“Powered by TinyMCE”
        paste_data_images: true, // 允许粘贴图像
        plugins: 'print preview searchreplace autolink directionality visualblocks visualchars fullscreen image link media kityformula-editor template code codesample table charmap hr pagebreak nonbreaking anchor insertdatetime advlist lists wordcount imagetools textpattern help emoticons autosave bdmap indent2em autoresize formatpainter axupimgs',
        toolbar: 'code preview |' +
          ' fullscreen |' +
          ' forecolor backcolor bold italic underline strikethrough link anchor |' +
          ' alignleft aligncenter alignright alignjustify outdent indent |' +
          ' table image link |' +
          ' styleselect formatselect fontselect fontsizeselect |' +
          ' bullist numlist |' +
          ' blockquote subscript superscript removeformat |' +
          ' kityformula-editor charmap emoticons hr pagebreak insertdatetime print |' +
          ' bdmap indent2em lineheight formatpainter axupimgs',
        images_upload_handler: (blobInfo, success, failure) => {
          const formData = new FormData()
          formData.set('file', blobInfo.blob())
          uploadFile(formData).then(res => {
            success(res.data.url)
          }).catch(res => {
            failure('error')
          })
        }
      }
    }
  },
  watch: {
    // 动态传参
    curValue(newValue) {
      this.value = newValue
    },
    // 读取输入框内的数据
    value(newValue) {
      this.$emit('input', newValue)
    }
  },
  mounted() {
    tinymce.init({})
  }
}
</script>
