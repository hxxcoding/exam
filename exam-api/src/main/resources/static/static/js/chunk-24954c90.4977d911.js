(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-24954c90"],{"0468":function(t,e,o){"use strict";o.d(e,"b",(function(){return a})),o.d(e,"d",(function(){return n})),o.d(e,"c",(function(){return i})),o.d(e,"a",(function(){return l}));var r=o("b775");function a(t){return Object(r["b"])("/exam/api/repo/detail",t)}function n(t){return Object(r["b"])("/exam/api/repo/save",t)}function i(t){return Object(r["b"])("/exam/api/repo/list",t)}function l(t){return Object(r["b"])("/exam/api/repo/batch-action",t)}},"4ee14":function(t,e,o){"use strict";o.r(e);var r=function(){var t=this,e=t.$createElement,o=t._self._c||e;return o("div",{staticClass:"app-container"},[o("h3",[t._v("组卷信息")]),o("el-card",{staticStyle:{"margin-top":"20px"}},[o("div",{staticStyle:{float:"right","font-weight":"bold",color:"#ff0000"}},[t._v("试卷总分："+t._s(t.postForm.totalScore)+"分")]),o("div",[o("el-button",{staticClass:"filter-item",attrs:{size:"small",type:"primary",icon:"el-icon-plus"},on:{click:t.handleAdd}},[t._v(" 添加题库 ")]),o("el-table",{staticStyle:{width:"100%","margin-top":"15px"},attrs:{data:t.repoList,border:!1,"empty-text":"请点击上面的`添加题库`进行设置"}},[o("el-table-column",{attrs:{label:"题库",width:"200"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("repo-select",{attrs:{multi:!1},on:{change:function(o){return t.repoChange(o,e.row)}},model:{value:e.row.repoId,callback:function(o){t.$set(e.row,"repoId",o)},expression:"scope.row.repoId"}})]}}])}),o("el-table-column",{attrs:{label:"单选数量",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,max:e.row.totalRadio,controls:!1,placeholder:e.row.totalRadio},model:{value:e.row.radioCount,callback:function(o){t.$set(e.row,"radioCount",o)},expression:"scope.row.radioCount"}})]}}])}),o("el-table-column",{attrs:{label:"单选分数",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,controls:!1},model:{value:e.row.radioScore,callback:function(o){t.$set(e.row,"radioScore",o)},expression:"scope.row.radioScore"}})]}}])}),o("el-table-column",{attrs:{label:"多选数量",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,max:e.row.totalMulti,controls:!1,placeholder:e.row.totalMulti},model:{value:e.row.multiCount,callback:function(o){t.$set(e.row,"multiCount",o)},expression:"scope.row.multiCount"}})]}}])}),o("el-table-column",{attrs:{label:"多选分数",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,controls:!1},model:{value:e.row.multiScore,callback:function(o){t.$set(e.row,"multiScore",o)},expression:"scope.row.multiScore"}})]}}])}),o("el-table-column",{attrs:{label:"判断数量",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,max:e.row.totalJudge,controls:!1,placeholder:e.row.totalJudge},model:{value:e.row.judgeCount,callback:function(o){t.$set(e.row,"judgeCount",o)},expression:"scope.row.judgeCount"}})]}}])}),o("el-table-column",{attrs:{label:"判断分数",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,controls:!1},model:{value:e.row.judgeScore,callback:function(o){t.$set(e.row,"judgeScore",o)},expression:"scope.row.judgeScore"}})]}}])}),o("el-table-column",{attrs:{label:"填空数量",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,max:e.row.totalBlank,controls:!1,placeholder:e.row.totalBlank},model:{value:e.row.blankCount,callback:function(o){t.$set(e.row,"blankCount",o)},expression:"scope.row.blankCount"}})]}}])}),o("el-table-column",{attrs:{label:"填空分数",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,controls:!1},model:{value:e.row.blankScore,callback:function(o){t.$set(e.row,"blankScore",o)},expression:"scope.row.blankScore"}})]}}])}),o("el-table-column",{attrs:{label:"操作数量",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,max:e.row.totalSaq,controls:!1,placeholder:e.row.totalSaq},model:{value:e.row.saqCount,callback:function(o){t.$set(e.row,"saqCount",o)},expression:"scope.row.saqCount"}})]}}])}),o("el-table-column",{attrs:{label:"操作分数",align:"center"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-input-number",{staticStyle:{width:"100%"},attrs:{min:0,controls:!1},model:{value:e.row.saqScore,callback:function(o){t.$set(e.row,"saqScore",o)},expression:"scope.row.saqScore"}})]}}])}),o("el-table-column",{attrs:{label:"删除",align:"center",width:"80px"},scopedSlots:t._u([{key:"default",fn:function(e){return[o("el-button",{attrs:{type:"danger",icon:"el-icon-delete",circle:""},on:{click:function(o){return t.removeItem(e.$index)}}})]}}])})],1)],1)]),o("h3",[t._v("考试配置")]),o("el-card",{staticStyle:{"margin-top":"20px"}},[o("el-form",{ref:"postForm",attrs:{model:t.postForm,rules:t.rules,"label-position":"left","label-width":"120px"}},[o("el-form-item",{attrs:{label:"考试名称",prop:"title"}},[o("el-input",{model:{value:t.postForm.title,callback:function(e){t.$set(t.postForm,"title",e)},expression:"postForm.title"}})],1),o("el-form-item",{attrs:{label:"考试描述",prop:"content"}},[o("el-input",{attrs:{type:"textarea"},model:{value:t.postForm.content,callback:function(e){t.$set(t.postForm,"content",e)},expression:"postForm.content"}})],1),o("el-form-item",{attrs:{label:"总分数",prop:"totalScore"}},[o("el-input-number",{attrs:{value:t.postForm.totalScore,disabled:""}})],1),o("el-form-item",{attrs:{label:"及格分",prop:"qualifyScore"}},[o("el-input-number",{attrs:{max:t.postForm.totalScore},model:{value:t.postForm.qualifyScore,callback:function(e){t.$set(t.postForm,"qualifyScore",e)},expression:"postForm.qualifyScore"}})],1),o("el-form-item",{attrs:{label:"考试时长(分钟)",prop:"totalTime"}},[o("el-input-number",{model:{value:t.postForm.totalTime,callback:function(e){t.$set(t.postForm,"totalTime",e)},expression:"postForm.totalTime"}})],1),o("el-form-item",{attrs:{label:"是否限时"}},[o("el-checkbox",{model:{value:t.postForm.timeLimit,callback:function(e){t.$set(t.postForm,"timeLimit",e)},expression:"postForm.timeLimit"}})],1),t.postForm.timeLimit?o("el-form-item",{attrs:{label:"考试时间",prop:"timeLimit"}},[o("el-date-picker",{attrs:{format:"yyyy-MM-dd HH:mm","value-format":"yyyy-MM-dd HH:mm",type:"datetimerange","range-separator":"至","start-placeholder":"开始时间","end-placeholder":"结束时间"},model:{value:t.dateValues,callback:function(e){t.dateValues=e},expression:"dateValues"}})],1):t._e(),o("el-form-item",{attrs:{label:"是否限次"}},[o("el-checkbox",{model:{value:t.postForm.tryLimit,callback:function(e){t.$set(t.postForm,"tryLimit",e)},expression:"postForm.tryLimit"}})],1),t.postForm.tryLimit?o("el-form-item",{attrs:{label:"限制次数",prop:"tryLimit"}},[o("el-input-number",{attrs:{min:1,max:100},model:{value:t.postForm.limitTimes,callback:function(e){t.$set(t.postForm,"limitTimes",e)},expression:"postForm.limitTimes"}})],1):t._e()],1)],1),o("h3",[t._v("权限配置")]),o("el-card",{staticStyle:{"margin-top":"20px"}},[o("el-radio-group",{staticStyle:{"margin-bottom":"20px"},model:{value:t.postForm.openType,callback:function(e){t.$set(t.postForm,"openType",e)},expression:"postForm.openType"}},[o("el-radio",{attrs:{label:1,border:""}},[t._v("完全公开")]),o("el-radio",{attrs:{label:2,border:""}},[t._v("部门开放")])],1),1===t.postForm.openType?o("el-alert",{attrs:{title:"开放的，任何人都可以进行考试！",type:"warning"}}):t._e(),2===t.postForm.openType?o("div",[o("el-input",{attrs:{placeholder:"输入关键字进行过滤"},model:{value:t.filterText,callback:function(e){t.filterText=e},expression:"filterText"}}),o("el-tree",{directives:[{name:"loading",rawName:"v-loading",value:t.treeLoading,expression:"treeLoading"}],ref:"tree",attrs:{"empty-text":" ",data:t.treeData,"default-expand-all":"","show-checkbox":"","node-key":"id","default-checked-keys":t.postForm.departIds,props:t.defaultProps,"filter-node-method":t.filterNode},on:{"check-change":t.handleCheckChange}})],1):t._e()],1),o("div",{staticStyle:{"margin-top":"20px"}},[o("el-button",{attrs:{type:"primary"},on:{click:t.handleSave}},[t._v("保存")])],1)],1)},a=[],n=(o("ac6a"),o("955d")),i=o("9b73"),l=o("4a02"),s={name:"ExamDetail",components:{RepoSelect:l["a"]},data:function(){return{step:1,treeData:[],defaultProps:{label:"deptName"},levels:[{value:0,label:"不限"},{value:1,label:"普通"},{value:2,label:"较难"}],filterText:"",treeLoading:!1,dateValues:[],quDialogShow:!1,quDialogType:1,excludes:[],scoreDialog:!1,scoreBatch:0,repoList:[],quList:[[],[],[],[],[]],quEnable:[!1,!1,!1,!1,!1],postForm:{totalScore:0,repoList:[],quList:[],joinType:1,openType:1,departIds:[]},rules:{title:[{required:!0,message:"考试名称不能为空！"}],content:[{required:!0,message:"考试内容不能为空！"}],open:[{required:!0,message:"考试权限不能为空！"}],totalScore:[{required:!0,message:"考试分数不能为空！"}],qualifyScore:[{required:!0,message:"及格分不能为空！"}],totalTime:[{required:!0,message:"考试时长不能为空！"}],timeLimit:[{required:!0,message:"考试时间不能为空！"}],tryLimit:[{required:!0,message:"次数限制不能为空！"}],ruleId:[{required:!0,message:"考试规则不能为空"}],password:[{required:!0,message:"考试口令不能为空！"}]}}},watch:{filterText:function(t){this.$refs.tree.filter(t)},dateValues:{handler:function(){this.postForm.startTime=this.dateValues[0],this.postForm.endTime=this.dateValues[1]}},repoList:{handler:function(){var t=this;t.postForm.totalScore=0,this.repoList.forEach((function(e){t.postForm.totalScore+=e.radioCount*e.radioScore,t.postForm.totalScore+=e.multiCount*e.multiScore,t.postForm.totalScore+=e.judgeCount*e.judgeScore,t.postForm.totalScore+=e.saqCount*e.saqScore,t.postForm.totalScore+=e.blankCount*e.blankScore})),this.postForm.repoList=this.repoList},deep:!0}},created:function(){var t=this,e=this.$route.params.id;"undefined"!==typeof e&&this.fetchData(e),Object(i["c"])({}).then((function(e){t.treeData=e.data}))},methods:{handleSave:function(){var t=this;this.$refs.postForm.validate((function(e){if(e)if(0!==t.postForm.totalScore){if(1===t.postForm.joinType)for(var o=0;o<t.postForm.repoList.length;o++){var r=t.postForm.repoList[o];if(!r.repoId)return void t.$notify({title:"提示信息",message:"考试题库选择不正确！",type:"warning",duration:2e3});if(r.radioCount>0&&0===r.radioScore||0===r.radioCount&&r.radioScore>0)return void t.$notify({title:"提示信息",message:"题库第：["+(o+1)+"]项存在无效的单选题配置！",type:"warning",duration:2e3});if(r.multiCount>0&&0===r.multiScore||0===r.multiCount&&r.multiScore>0)return void t.$notify({title:"提示信息",message:"题库第：["+(o+1)+"]项存在无效的多选题配置！",type:"warning",duration:2e3});if(r.judgeCount>0&&0===r.judgeScore||0===r.judgeCount&&r.judgeScore>0)return void t.$notify({title:"提示信息",message:"题库第：["+(o+1)+"]项存在无效的判断题配置！",type:"warning",duration:2e3});if(r.saqCount>0&&0===r.saqScore||0===r.saqCount&&r.saqScore>0)return void t.$notify({title:"提示信息",message:"题库第：["+(o+1)+"]项存在无效的操作题配置！",type:"warning",duration:2e3});if(r.blankCount>0&&0===r.blankScore||0===r.blankCount&&r.blankScore>0)return void t.$notify({title:"提示信息",message:"题库第：["+(o+1)+"]项存在无效的填空题配置！",type:"warning",duration:2e3})}t.$confirm("确实要提交保存吗？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then((function(){t.submitForm()}))}else t.$notify({title:"提示信息",message:"考试规则设置不正确，请确认！",type:"warning",duration:2e3})}))},handleCheckChange:function(){var t=this;this.postForm.departIds=[];var e=this.$refs.tree.getCheckedNodes();e.forEach((function(e){t.postForm.departIds.push(e.id)}))},handleAdd:function(){this.repoList.push({rowId:(new Date).getTime(),radioCount:0,radioScore:0,multiCount:0,multiScore:0,judgeCount:0,judgeScore:0,saqCount:0,saqScore:0,blankCount:0,blankScore:0})},removeItem:function(t){this.repoList.splice(t,1)},fetchData:function(t){var e=this,o=this;Object(n["a"])(t).then((function(t){e.postForm=t.data,e.postForm.startTime&&e.postForm.endTime&&(e.dateValues[0]=e.postForm.startTime,e.dateValues[1]=e.postForm.endTime),2===e.postForm.joinType&&e.postForm.quList.forEach((function(t){var e=t.quType-1;o.quList[e].push(t),o.quEnable[e]=!0})),1===e.postForm.joinType&&(o.repoList=o.postForm.repoList)}))},submitForm:function(){var t=this;this.postForm.repoList=this.repoList,Object(n["c"])(this.postForm).then((function(){t.$notify({title:"成功",message:"考试保存成功！",type:"success",duration:2e3}),t.$router.push({name:"ListExam"})}))},filterNode:function(t,e){return!t||-1!==e.deptName.indexOf(t)},repoChange:function(t,e){null!=t?(e.totalRadio=t.radioCount,e.totalMulti=t.multiCount,e.totalJudge=t.judgeCount,e.totalBlank=t.blankCount,e.totalSaq=t.saqCount):(e.totalRadio=0,e.totalMulti=0,e.totalJudge=0,e.totalBlank=0,e.totalSaq=0)}}},u=s,c=o("2877"),p=Object(c["a"])(u,r,a,!1,null,"505b27b4",null);e["default"]=p.exports},7514:function(t,e,o){"use strict";var r=o("5ca1"),a=o("0a49")(5),n="find",i=!0;n in[]&&Array(1)[n]((function(){i=!1})),r(r.P+r.F*i,"Array",{find:function(t){return a(this,t,arguments.length>1?arguments[1]:void 0)}}),o("9c6c")(n)},"955d":function(t,e,o){"use strict";o.d(e,"a",(function(){return a})),o.d(e,"c",(function(){return n})),o.d(e,"b",(function(){return i}));var r=o("b775");function a(t){return Object(r["b"])("/exam/api/exam/exam/detail",{id:t})}function n(t){return Object(r["b"])("/exam/api/exam/exam/save",t)}function i(){return Object(r["b"])("/exam/api/exam/exam/paging",{current:1,size:100})}},"9b73":function(t,e,o){"use strict";o.d(e,"d",(function(){return a})),o.d(e,"c",(function(){return n})),o.d(e,"b",(function(){return i})),o.d(e,"a",(function(){return l})),o.d(e,"e",(function(){return s})),o.d(e,"f",(function(){return u}));var r=o("b775");function a(t){return Object(r["b"])("/exam/api/sys/depart/paging",t)}function n(t){return Object(r["b"])("/exam/api/sys/depart/tree",t)}function i(t){var e={id:t};return Object(r["b"])("/exam/api/sys/depart/detail",e)}function l(t){var e={ids:t};return Object(r["b"])("/exam/api/sys/depart/delete",e)}function s(t){return Object(r["b"])("/exam/api/sys/depart/save",t)}function u(t,e){var o={id:t,sort:e};return Object(r["b"])("/exam/api/sys/depart/sort",o)}}}]);