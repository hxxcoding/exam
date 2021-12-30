(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-637f026d"],{"0468":function(e,t,s){"use strict";s.d(t,"b",(function(){return a})),s.d(t,"d",(function(){return o})),s.d(t,"c",(function(){return i})),s.d(t,"a",(function(){return r}));var n=s("b775");function a(e){return Object(n["b"])("/exam/api/repo/detail",e)}function o(e){return Object(n["b"])("/exam/api/repo/save",e)}function i(e){return Object(n["b"])("/exam/api/repo/list",e)}function r(e){return Object(n["b"])("/exam/api/repo/batch-action",e)}},7514:function(e,t,s){"use strict";var n=s("5ca1"),a=s("0a49")(5),o="find",i=!0;o in[]&&Array(1)[o]((function(){i=!1})),n(n.P+n.F*i,"Array",{find:function(e){return a(this,e,arguments.length>1?arguments[1]:void 0)}}),s("9c6c")(o)},d5b7:function(e,t,s){"use strict";s.r(t);var n=function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"app-container"},[s("el-form",{ref:"postForm",attrs:{model:e.postForm,rules:e.rules,"label-position":"left","label-width":"150px"}},[s("el-card",[s("el-form-item",{attrs:{label:"题目类型 ",prop:"quType"}},[s("el-select",{staticClass:"filter-item",attrs:{disabled:e.quTypeDisabled},on:{change:e.handleTypeChange},model:{value:e.postForm.quType,callback:function(t){e.$set(e.postForm,"quType",t)},expression:"postForm.quType"}},e._l(e.quTypes,(function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})})),1)],1),s("el-form-item",{attrs:{label:"难度等级 ",prop:"level"}},[s("el-select",{staticClass:"filter-item",model:{value:e.postForm.level,callback:function(t){e.$set(e.postForm,"level",t)},expression:"postForm.level"}},e._l(e.levels,(function(e){return s("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})})),1)],1),s("el-form-item",{attrs:{label:"归属题库",prop:"repoIds"}},[s("repo-select",{attrs:{multi:!0},model:{value:e.postForm.repoIds,callback:function(t){e.$set(e.postForm,"repoIds",t)},expression:"postForm.repoIds"}})],1),s("el-form-item",{attrs:{label:"题目内容",prop:"content"}},[s("el-input",{attrs:{type:"textarea"},model:{value:e.postForm.content,callback:function(t){e.$set(e.postForm,"content",t)},expression:"postForm.content"}})],1),s("el-form-item",{attrs:{label:"试题图片(附件)"}},[s("file-upload",{model:{value:e.postForm.image,callback:function(t){e.$set(e.postForm,"image",t)},expression:"postForm.image"}})],1),s("el-form-item",{attrs:{label:"整题解析",prop:"oriPrice"}},[s("el-input",{attrs:{type:"textarea",precision:1,max:999999},model:{value:e.postForm.analysis,callback:function(t){e.$set(e.postForm,"analysis",t)},expression:"postForm.analysis"}})],1)],1),4!==e.postForm.quType?s("div",{staticClass:"filter-container",staticStyle:{"margin-top":"25px"}},[s("el-button",{staticClass:"filter-item",attrs:{type:"primary",icon:"el-icon-plus",size:"small",plain:""},on:{click:e.handleAdd}},[e._v(" 添加 ")]),s("el-table",{staticStyle:{width:"100%"},attrs:{data:e.postForm.answerList,border:!0}},[s("el-table-column",{attrs:{label:"是否答案",width:"120",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("el-checkbox",{model:{value:t.row.isRight,callback:function(s){e.$set(t.row,"isRight",s)},expression:"scope.row.isRight"}},[e._v("答案")])]}}],null,!1,1650073960)}),e.itemImage?s("el-table-column",{attrs:{label:"选项图片",width:"120px",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("file-upload",{model:{value:t.row.image,callback:function(s){e.$set(t.row,"image",s)},expression:"scope.row.image"}})]}}],null,!1,2486182644)}):e._e(),s("el-table-column",{attrs:{label:"答案内容"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("el-input",{attrs:{type:"textarea"},model:{value:t.row.content,callback:function(s){e.$set(t.row,"content",s)},expression:"scope.row.content"}})]}}],null,!1,924406712)}),s("el-table-column",{attrs:{label:"答案解析"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("el-input",{attrs:{type:"textarea"},model:{value:t.row.analysis,callback:function(s){e.$set(t.row,"analysis",s)},expression:"scope.row.analysis"}})]}}],null,!1,3792987939)}),s("el-table-column",{attrs:{label:"操作",align:"center",width:"100px"},scopedSlots:e._u([{key:"default",fn:function(t){return[s("el-button",{attrs:{type:"danger",icon:"el-icon-delete",circle:""},on:{click:function(s){return e.removeItem(t.$index)}}})]}}],null,!1,1518468532)})],1)],1):e._e(),s("div",{staticStyle:{"margin-top":"20px"}},[s("el-button",{attrs:{type:"primary"},on:{click:e.submitForm}},[e._v("保存")]),s("el-button",{attrs:{type:"info"},on:{click:e.onCancel}},[e._v("返回")])],1)],1)],1)},a=[],o=(s("ac6a"),s("f4bc")),i=s("4a02"),r=s("2a75"),l={name:"QuDetail",components:{FileUpload:r["a"],RepoSelect:i["a"]},data:function(){return{quTypeDisabled:!1,itemImage:!0,levels:[{value:1,label:"普通"},{value:2,label:"较难"}],quTypes:[{value:1,label:"单选题"},{value:2,label:"多选题"},{value:3,label:"判断题"},{value:4,label:"操作题"},{value:5,label:"填空题"}],postForm:{repoIds:[],tagList:[],answerList:[]},rules:{content:[{required:!0,message:"题目内容不能为空！"}],quType:[{required:!0,message:"题目类型不能为空！"}],level:[{required:!0,message:"必须选择难度等级！"}],repoIds:[{required:!0,message:"至少要选择一个题库！"}]}}},created:function(){var e=this.$route.params.id;"undefined"!==typeof e&&(this.quTypeDisabled=!0,this.fetchData(e))},methods:{handleTypeChange:function(e){this.postForm.answerList=[],5===e&&this.postForm.answerList.push({isRight:!0,content:"",analysis:"填空题答案内容请使用英文半角符号';'分割。例如 1;2;3;"}),3===e&&(this.postForm.answerList.push({isRight:!0,content:"正确",analysis:""}),this.postForm.answerList.push({isRight:!1,content:"错误",analysis:""})),1!==e&&2!==e||(this.postForm.answerList.push({isRight:!1,content:"",analysis:""}),this.postForm.answerList.push({isRight:!1,content:"",analysis:""}),this.postForm.answerList.push({isRight:!1,content:"",analysis:""}),this.postForm.answerList.push({isRight:!1,content:"",analysis:""}))},handleAdd:function(){this.postForm.answerList.push({isRight:!1,content:"",analysis:""})},removeItem:function(e){this.postForm.answerList.splice(e,1)},fetchData:function(e){var t=this;Object(o["b"])(e).then((function(e){t.postForm=e.data}))},submitForm:function(){var e=this,t=0;this.postForm.answerList.forEach((function(e){e.isRight&&(t+=1)})),1!==this.postForm.quType||1===t?2===this.postForm.quType&&t<2?this.$message({message:"多选题至少要有两个正确答案！",type:"warning"}):3!==this.postForm.quType||1===t?5!==this.postForm.quType||1===t?this.$refs.postForm.validate((function(t){t&&Object(o["e"])(e.postForm).then((function(t){e.postForm=t.data,e.$notify({title:"成功",message:"试题保存成功！",type:"success",duration:2e3}),e.$router.push({name:"ListQu"})}))})):this.$message({message:"填空题请务增加选项框！",type:"warning"}):this.$message({message:"判断题只能有一个正确项！",type:"warning"}):this.$message({message:"单选题答案只能有一个",type:"warning"})},onCancel:function(){this.$router.push({name:"ListQu"})}}},u=l,c=s("2877"),p=Object(c["a"])(u,n,a,!1,null,"350ddb8c",null);t["default"]=p.exports},f4bc:function(e,t,s){"use strict";s.d(t,"b",(function(){return a})),s.d(t,"e",(function(){return o})),s.d(t,"a",(function(){return i})),s.d(t,"d",(function(){return r})),s.d(t,"c",(function(){return l}));var n=s("b775");function a(e){return Object(n["b"])("/exam/api/qu/qu/detail",{id:e})}function o(e){return Object(n["b"])("/exam/api/qu/qu/save",e)}function i(e){return Object(n["a"])("/exam/api/qu/qu/export",e,"导出的数据.xlsx")}function r(){return Object(n["a"])("/exam/api/qu/qu/import/template",{},"qu-import-template.xlsx")}function l(e){return Object(n["c"])("/exam/api/qu/qu/import",e)}}}]);