(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-1765565f"],{"0468":function(t,e,o){"use strict";o.d(e,"c",(function(){return r})),o.d(e,"d",(function(){return i})),o.d(e,"b",(function(){return a})),o.d(e,"a",(function(){return s}));var n=o("b775");function r(t){return Object(n["b"])("/exam/api/repo/detail",t)}function i(t){return Object(n["b"])("/exam/api/repo/save",t)}function a(t){return Object(n["b"])("/exam/api/repo/list",t)}function s(t){return Object(n["b"])("/exam/api/repo/batch-action",t)}},"3e86":function(t,e,o){"use strict";o.r(e);var n=function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",{staticClass:"app-container"},[o("el-form",{ref:"postForm",attrs:{model:t.postForm,rules:t.rules,"label-position":"top","label-width":"100%"}},[o("el-card",[o("el-form-item",{attrs:{label:"题库名称",prop:"title"}},[o("el-input",{model:{value:t.postForm.title,callback:function(e){t.$set(t.postForm,"title",e)},expression:"postForm.title"}})],1),o("el-form-item",{attrs:{label:"题库备注",prop:"remark"}},[o("el-input",{attrs:{type:"textarea"},model:{value:t.postForm.remark,callback:function(e){t.$set(t.postForm,"remark",e)},expression:"postForm.remark"}})],1)],1),o("div",{staticStyle:{"margin-top":"20px"}},[o("el-button",{attrs:{type:"primary"},on:{click:t.submitForm}},[t._v("保存")]),o("el-button",{attrs:{type:"info"},on:{click:t.onCancel}},[t._v("返回")])],1)],1)],1)},r=[],i=o("0468"),a={name:"QuRepoDetail",data:function(){return{postForm:{},loading:!1,rules:{title:[{required:!0,message:"题库名称不能为空！"}]}}},created:function(){var t=this.$route.query.id;"undefined"!==typeof t&&this.fetchData(t)},methods:{handleAdd:function(){this.postForm.answerList.push({isRight:!1,content:"",analysis:""})},fetchData:function(t){var e=this,o={id:t};Object(i["c"])(o).then((function(t){e.postForm=t.data}))},submitForm:function(){var t=this;console.log(JSON.stringify(this.postForm)),this.$refs.postForm.validate((function(e){e&&Object(i["d"])(t.postForm).then((function(){t.$notify({title:"成功",message:"题库保存成功！",type:"success",duration:2e3}),t.$router.push({name:"ListRepo"})}))}))},onCancel:function(){this.$router.push({name:"ListRepo"})}}},s=a,u=o("2877"),c=Object(u["a"])(s,n,r,!1,null,null,null);e["default"]=c.exports}}]);