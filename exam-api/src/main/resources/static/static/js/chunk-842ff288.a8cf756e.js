(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-842ff288"],{"0468":function(e,t,n){"use strict";n.d(t,"c",(function(){return a})),n.d(t,"d",(function(){return o})),n.d(t,"b",(function(){return r})),n.d(t,"a",(function(){return l}));var i=n("b775");function a(e){return Object(i["b"])("/exam/api/repo/detail",e)}function o(e){return Object(i["b"])("/exam/api/repo/save",e)}function r(e){return Object(i["b"])("/exam/api/repo/list",e)}function l(e){return Object(i["b"])("/exam/api/repo/batch-action",e)}},"09f4":function(e,t,n){"use strict";n.d(t,"a",(function(){return r})),Math.easeInOutQuad=function(e,t,n,i){return e/=i/2,e<1?n/2*e*e+t:(e--,-n/2*(e*(e-2)-1)+t)};var i=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(e){window.setTimeout(e,1e3/60)}}();function a(e){document.documentElement.scrollTop=e,document.body.parentNode.scrollTop=e,document.body.scrollTop=e}function o(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function r(e,t,n){var r=o(),l=e-r,u=20,s=0;t="undefined"===typeof t?500:t;var c=function e(){s+=u;var o=Math.easeInOutQuad(s,r,l,t);a(o),s<t?i(e):n&&"function"===typeof n&&n()};c()}},2934:function(e,t,n){"use strict";n.d(t,"c",(function(){return a})),n.d(t,"b",(function(){return o})),n.d(t,"a",(function(){return r}));var i=n("b775");function a(e,t){return Object(i["b"])(e,t)}function o(e,t){return Object(i["b"])(e,{ids:t})}function r(e,t,n){return Object(i["b"])(e,{ids:t,state:n})}},"3e5d":function(e,t,n){"use strict";n.r(t);var i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("data-table",{ref:"pagingTable",attrs:{options:e.options,"list-query":e.listQuery},on:{"multi-actions":e.handleMultiAction}},[n("template",{slot:"filter-content"},[n("el-row",[n("el-col",{attrs:{span:24}},[n("el-select",{staticClass:"filter-item",attrs:{clearable:""},model:{value:e.listQuery.params.quType,callback:function(t){e.$set(e.listQuery.params,"quType",t)},expression:"listQuery.params.quType"}},e._l(e.quTypes,(function(e){return n("el-option",{key:e.value,attrs:{label:e.label,value:e.value}})})),1),n("repo-select",{attrs:{multi:!0},model:{value:e.listQuery.params.repoIds,callback:function(t){e.$set(e.listQuery.params,"repoIds",t)},expression:"listQuery.params.repoIds"}}),n("el-input",{staticClass:"filter-item",staticStyle:{width:"200px"},attrs:{placeholder:"题目内容"},model:{value:e.listQuery.params.content,callback:function(t){e.$set(e.listQuery.params,"content",t)},expression:"listQuery.params.content"}}),n("el-button-group",{staticClass:"filter-item",staticStyle:{float:"right"}},[n("el-button",{directives:[{name:"perm",rawName:"v-perm",value:["qu:import"],expression:"['qu:import']"}],attrs:{size:"mini",icon:"el-icon-upload2"},on:{click:e.showImport}},[e._v("导入")]),n("el-button",{directives:[{name:"perm",rawName:"v-perm",value:["qu:export"],expression:"['qu:export']"}],attrs:{size:"mini",icon:"el-icon-download"},on:{click:e.exportExcel}},[e._v("导出")])],1)],1)],1)],1),n("template",{slot:"data-columns"},[n("el-table-column",{attrs:{label:"题目类型",align:"center",width:"100px"},scopedSlots:e._u([{key:"default",fn:function(t){return[e._v(" "+e._s(e._f("quTypeFilter")(t.row.quType))+" ")]}}])}),n("el-table-column",{attrs:{label:"题目内容","show-overflow-tooltip":""},scopedSlots:e._u([{key:"default",fn:function(t){return[n("router-link",{attrs:{slot:"reference",to:{name:"SaveQu",query:{id:t.row.id}}},slot:"reference"},[e._v(" "+e._s(t.row.content)+" ")])]}}])}),n("el-table-column",{attrs:{label:"创建时间",align:"center",prop:"createTime",width:"180px"}})],1)],2),n("el-dialog",{attrs:{title:e.dialogTitle,visible:e.dialogVisible,width:"30%"},on:{"update:visible":function(t){e.dialogVisible=t}}},[n("el-form",{attrs:{"label-position":"left","label-width":"100px"}},[n("el-form-item",{attrs:{label:"操作题库",prop:"repoIds"}},[n("repo-select",{attrs:{multi:!0},model:{value:e.dialogRepos,callback:function(t){e.dialogRepos=t},expression:"dialogRepos"}})],1),n("el-row",[n("el-button",{attrs:{type:"primary"},on:{click:e.handlerRepoAction}},[e._v("保存")])],1)],1)],1),n("el-dialog",{attrs:{title:"导入试题",visible:e.importVisible,width:"30%"},on:{"update:visible":function(t){e.importVisible=t}}},[n("el-row",[n("el-button",{attrs:{type:"primary"},on:{click:e.chooseFile}},[e._v("上传导入")]),n("el-button",{attrs:{type:"warning"},on:{click:e.downloadTemplate}},[e._v("下载导入模板")]),n("a",{staticStyle:{"margin-left":"10px"},attrs:{href:e.quFormatConversionFileUrl}},[n("el-button",{attrs:{type:"warning"}},[e._v("下载试题转换模板")])],1),n("input",{ref:"upFile",staticClass:"file",staticStyle:{display:"none"},attrs:{name:"file",type:"file"},on:{change:e.doImport}})],1),n("el-alert",{staticStyle:{"margin-top":"20px"},attrs:{title:"导入试题暂仅支持`选择题``判断题``填空题`导入！",type:"warning"}})],1)],1)},a=[],o=n("7845"),r=n("4a02"),l=n("0468"),u=n("f4bc"),s=n("e350"),c={name:"QuList",components:{RepoSelect:r["a"],DataTable:o["a"]},data:function(){return{dialogTitle:"加入题库",dialogVisible:!1,importVisible:!1,dialogRepos:[],dialogQuIds:[],dialogFlag:!1,listQuery:{current:1,size:10,params:{content:"",quType:"",repoIds:[]}},quTypes:[{value:1,label:"单选题"},{value:2,label:"多选题"},{value:3,label:"判断题"},{value:5,label:"填空题"},{value:10,label:"Word操作题"},{value:11,label:"Excel操作题"},{value:12,label:"PPT操作题"}],quFormatConversionFileUrl:"".concat("","/upload/file/static/qu/import/qu-import-template-choice-qu-format-conversion.xlsm"),options:{multi:!0,multiActions:[{value:"delete",label:"删除",hasPerm:Object(s["a"])(["qu:delete"])},{value:"update-repo",label:"修改所属题库..",hasPerm:Object(s["a"])(["repo:batch-action"])},{value:"remove-repo",label:"从..题库移除",hasPerm:Object(s["a"])(["repo:batch-action"])}],listUrl:"/exam/api/qu/qu/paging",deleteUrl:"/exam/api/qu/qu/delete",deleteMsg:"删除题目可能导致已作答试卷无法查看,确认删除吗？",addRoute:"SaveQu",addPerm:["qu:save"]}}},methods:{handleMultiAction:function(e){"update-repo"===e.opt&&(this.dialogTitle="修改所属题库",this.dialogFlag=!1),"remove-repo"===e.opt&&(this.dialogTitle="从题库移除",this.dialogFlag=!0),this.dialogVisible=!0,this.dialogQuIds=e.ids},handlerRepoAction:function(){var e=this,t={repoIds:this.dialogRepos,quIds:this.dialogQuIds,remove:this.dialogFlag};Object(l["a"])(t).then((function(){e.$notify({title:"成功",message:"批量操作成功！",type:"success",duration:2e3}),e.dialogVisible=!1,e.$refs.pagingTable.getList()}))},exportExcel:function(){Object(u["a"])(this.listQuery.params)},downloadTemplate:function(){Object(u["e"])()},showImport:function(){this.importVisible=!0},chooseFile:function(){this.$refs.upFile.dispatchEvent(new MouseEvent("click"))},doImport:function(e){var t=this,n=e.target.files[0];Object(u["d"])(n).then((function(e){0!==e.code?t.$alert(e.data.msg,"导入信息",{dangerouslyUseHTMLString:!0}):(t.$message({message:"成功导入"+e.data+"条数据",type:"success"}),t.importVisible=!1,t.$refs.pagingTable.getList())}))}}},p=c,d=(n("5760"),n("2877")),f=Object(d["a"])(p,i,a,!1,null,null,null);t["default"]=f.exports},5760:function(e,t,n){"use strict";n("ae66")},7514:function(e,t,n){"use strict";var i=n("5ca1"),a=n("0a49")(5),o="find",r=!0;o in[]&&Array(1)[o]((function(){r=!1})),i(i.P+i.F*r,"Array",{find:function(e){return a(this,e,arguments.length>1?arguments[1]:void 0)}}),n("9c6c")(o)},ae66:function(e,t,n){},e350:function(e,t,n){"use strict";n.d(t,"a",(function(){return a}));n("6762"),n("2fdb");var i=n("4360");function a(e){if(e&&e instanceof Array&&e.length>0){var t=i["a"].getters&&i["a"].getters.perms,n=e,a=t.some((function(e){return n.includes(e)}));return!!a}return!1}},f4bc:function(e,t,n){"use strict";n.d(t,"b",(function(){return a})),n.d(t,"h",(function(){return o})),n.d(t,"i",(function(){return r})),n.d(t,"f",(function(){return l})),n.d(t,"c",(function(){return u})),n.d(t,"g",(function(){return s})),n.d(t,"a",(function(){return c})),n.d(t,"e",(function(){return p})),n.d(t,"d",(function(){return d}));var i=n("b775");function a(e){return Object(i["b"])("/exam/api/qu/qu/detail",{id:e})}function o(e){return Object(i["b"])("/exam/api/qu/qu/save",e)}function r(e){return Object(i["b"])("/exam/api/qu/qu/office/analyse/word",{url:e})}function l(e){return Object(i["b"])("/exam/api/qu/qu/office/analyse/ppt",{url:e})}function u(e){return Object(i["b"])("/exam/api/qu/qu/office/method",{quType:e})}function s(e,t,n){return Object(i["b"])("/exam/api/qu/qu/office/read-format",{url:e,pos:t,method:n})}function c(e){return Object(i["a"])("/exam/api/qu/qu/export",e,"导出题目-"+(new Date).getTime()+".xlsx")}function p(){return Object(i["a"])("/exam/api/qu/qu/import/template",{},"qu-import-template.xlsx")}function d(e){return Object(i["c"])("/exam/api/qu/qu/import",e)}}}]);