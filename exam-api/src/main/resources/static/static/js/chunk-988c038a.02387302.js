(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-988c038a"],{"9b73":function(e,t,r){"use strict";r.d(t,"d",(function(){return a})),r.d(t,"c",(function(){return s})),r.d(t,"b",(function(){return u})),r.d(t,"a",(function(){return i})),r.d(t,"e",(function(){return o})),r.d(t,"f",(function(){return c}));var n=r("b775");function a(e){return Object(n["b"])("/exam/api/sys/depart/paging",e)}function s(e){return Object(n["b"])("/exam/api/sys/depart/tree",e)}function u(e){var t={id:e};return Object(n["b"])("/exam/api/sys/depart/detail",t)}function i(e){var t={ids:e};return Object(n["b"])("/exam/api/sys/depart/delete",t)}function o(e){return Object(n["b"])("/exam/api/sys/depart/save",e)}function c(e,t){var r={id:e,sort:t};return Object(n["b"])("/exam/api/sys/depart/sort",r)}},e206:function(e,t,r){"use strict";r.r(t);var n=function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("el-form",[r("el-form-item",{attrs:{label:"用户名"}},[r("el-input",{attrs:{readonly:""},model:{value:e.user.name,callback:function(t){e.$set(e.user,"name","string"===typeof t?t.trim():t)},expression:"user.name"}})],1),r("el-form-item",{attrs:{label:"选课号"}},[r("el-input",{attrs:{readonly:""},model:{value:e.deptName,callback:function(t){e.deptName="string"===typeof t?t.trim():t},expression:"deptName"}})],1),r("el-form-item",{attrs:{label:"密码"}},[r("el-input",{attrs:{type:"password",placeholder:"不修改请留空"},model:{value:e.user.password,callback:function(t){e.$set(e.user,"password","string"===typeof t?t.trim():t)},expression:"user.password"}})],1),r("el-form-item",[r("el-button",{attrs:{type:"primary"},on:{click:e.submit}},[e._v("修改")])],1)],1)},a=[],s=(r("96cf"),r("3b8d")),u=r("2995"),i=r("9b73"),o={props:{user:{type:Object,default:function(){return{password:""}}}},data:function(){return{deptName:""}},mounted:function(){var e=this;Object(i["b"])(this.user.departId).then((function(t){e.deptName=t.data.deptName}))},methods:{logout:function(){var e=Object(s["a"])(regeneratorRuntime.mark((function e(){return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,this.$store.dispatch("user/logout");case 2:this.$router.push("/login?redirect=".concat(this.$route.fullPath));case 3:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}(),submit:function(){var e=Object(s["a"])(regeneratorRuntime.mark((function e(){var t=this;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:Object(u["f"])(this.user).then((function(){t.$notify({title:"成功",message:"用户资料保存成功！！",type:"success",duration:2e3}),t.logout()}));case 1:case"end":return e.stop()}}),e,this)})));function t(){return e.apply(this,arguments)}return t}()}},c=o,p=r("2877"),l=Object(p["a"])(c,n,a,!1,null,null,null);t["default"]=l.exports}}]);