(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-4caa441b"],{"09f4":function(e,t,n){"use strict";n.d(t,"a",(function(){return u})),Math.easeInOutQuad=function(e,t,n,o){return e/=o/2,e<1?n/2*e*e+t:(e--,-n/2*(e*(e-2)-1)+t)};var o=function(){return window.requestAnimationFrame||window.webkitRequestAnimationFrame||window.mozRequestAnimationFrame||function(e){window.setTimeout(e,1e3/60)}}();function r(e){document.documentElement.scrollTop=e,document.body.parentNode.scrollTop=e,document.body.scrollTop=e}function i(){return document.documentElement.scrollTop||document.body.parentNode.scrollTop||document.body.scrollTop}function u(e,t,n){var u=i(),s=e-u,a=20,c=0;t="undefined"===typeof t?500:t;var l=function e(){c+=a;var i=Math.easeInOutQuad(c,u,s,t);r(i),c<t?o(e):n&&"function"===typeof n&&n()};l()}},2934:function(e,t,n){"use strict";n.d(t,"c",(function(){return r})),n.d(t,"b",(function(){return i})),n.d(t,"a",(function(){return u}));var o=n("b775");function r(e,t){return Object(o["b"])(e,t)}function i(e,t){return Object(o["b"])(e,{ids:t})}function u(e,t,n){return Object(o["b"])(e,{ids:t,state:n})}},6414:function(e,t,n){"use strict";n.d(t,"b",(function(){return r})),n.d(t,"d",(function(){return i})),n.d(t,"a",(function(){return u})),n.d(t,"c",(function(){return s})),n.d(t,"e",(function(){return a}));var o=n("b775");function r(){return Object(o["b"])("/exam/api/sys/menu/list",{})}function i(e){return Object(o["b"])("/exam/api/sys/menu/save",e)}function u(e){return Object(o["b"])("/exam/api/sys/menu/delete",{id:e})}function s(e){return Object(o["b"])("/exam/api/sys/menu/list-tree-by-role",{id:e})}function a(e,t){var n={id:e,sort:t};return Object(o["b"])("/exam/api/sys/menu/sort",n)}},"7d61":function(e,t,n){"use strict";n.d(t,"a",(function(){return r})),n.d(t,"b",(function(){return i}));var o=n("b775");function r(){return Object(o["b"])("/exam/api/sys/role/list",{})}function i(e,t){return Object(o["b"])("/exam/api/sys/role/menu/update",{id:e,menuIds:t})}},cdfb:function(e,t,n){"use strict";n.r(t);var o=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("data-table",{ref:"pagingTable",attrs:{options:e.options,"list-query":e.listQuery}},[n("template",{slot:"data-columns"},[n("el-table-column",{attrs:{label:"角色ID",align:"center",prop:"id"}}),n("el-table-column",{attrs:{label:"角色名称",align:"center",prop:"roleName"}}),n("el-table-column",{attrs:{align:"center",label:"操作"},scopedSlots:e._u([{key:"default",fn:function(t){return["sa"!==t.row.id?n("el-button",{directives:[{name:"perm",rawName:"v-perm",value:["sys:menu:list-tree-by-role"],expression:"['sys:menu:list-tree-by-role']"}],attrs:{type:"text",icon:"el-icon-unlock"},on:{click:function(n){return e.handleUpdate(t.row)}}},[e._v("菜单授权")]):e._e()]}}])})],1)],2),n("el-dialog",{attrs:{title:"修改角色",visible:e.open,width:"500px","append-to-body":""},on:{"update:visible":function(t){e.open=t}}},[n("el-form",{ref:"form",attrs:{model:e.form,rules:e.rules,"label-width":"100px"}},[n("el-form-item",{attrs:{label:"角色名称",prop:"roleName"}},[n("el-input",{attrs:{placeholder:"请输入角色名称"},model:{value:e.form.roleName,callback:function(t){e.$set(e.form,"roleName",t)},expression:"form.roleName"}})],1),n("el-form-item",{attrs:{label:"菜单权限"}},[n("el-checkbox",{on:{change:function(t){return e.handleCheckedTreeExpand(t)}},model:{value:e.menuExpand,callback:function(t){e.menuExpand=t},expression:"menuExpand"}},[e._v("展开/折叠")]),n("el-checkbox",{on:{change:function(t){return e.handleCheckedTreeNodeAll(t)}},model:{value:e.menuNodeAll,callback:function(t){e.menuNodeAll=t},expression:"menuNodeAll"}},[e._v("全选/全不选")]),n("el-checkbox",{on:{change:function(t){return e.handleCheckedTreeConnect(t)}},model:{value:e.form.menuCheckStrictly,callback:function(t){e.$set(e.form,"menuCheckStrictly",t)},expression:"form.menuCheckStrictly"}},[e._v("父子联动")]),n("el-tree",{ref:"menu",staticClass:"tree-border",attrs:{data:e.menuOptions,"show-checkbox":"","node-key":"id","check-strictly":!e.form.menuCheckStrictly,"empty-text":"加载中,请稍候",props:e.defaultProps}})],1)],1),n("el-alert",{attrs:{title:"`角色管理`,`菜单管理`为系统高危权限,请谨慎操作！",type:"error"}}),n("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[n("el-button",{directives:[{name:"perm",rawName:"v-perm",value:["sys:role:menu:update"],expression:"['sys:role:menu:update']"}],attrs:{type:"primary"},on:{click:e.submitForm}},[e._v("确 定")]),n("el-button",{on:{click:e.cancel}},[e._v("取 消")])],1)],1)],1)},r=[],i=(n("ac6a"),n("7845")),u=n("6414"),s=n("7d61"),a={name:"SysRoleList",components:{DataTable:i["a"]},data:function(){return{listQuery:{current:1,size:10,params:{}},open:!1,form:{},menuOptions:[],openDataScope:!1,menuExpand:!1,menuNodeAll:!1,deptExpand:!0,deptNodeAll:!1,options:{listUrl:"/exam/api/sys/role/paging",stateUrl:"/sys/user/state"}}},methods:{handleUpdate:function(e){var t=this;this.reset(),this.open=!0,this.form.roleName=e.roleName,this.form.id=e.id;var n=this.listMenuTreeByRole(e.id);this.$nextTick((function(){n.then((function(e){var n=e.checkedKeys;n.forEach((function(e){t.$nextTick((function(){t.$refs.menu.setChecked(e,!0,!1)}))}))}))}))},listMenuTreeByRole:function(e){var t=this;return Object(u["c"])(e).then((function(e){return t.menuOptions=e.data.tree,e.data}))},submitForm:function(){var e=this;this.$refs["form"].validate((function(t){t&&void 0!==e.form.id&&(e.form.menuIds=e.getMenuAllCheckedKeys(),Object(s["b"])(e.form.id,e.form.menuIds).then((function(t){e.$notify({title:"成功",message:"修改成功！",type:"success",duration:2e3}),e.open=!1})))}))},getMenuAllCheckedKeys:function(){var e=this.$refs.menu.getCheckedKeys(),t=this.$refs.menu.getHalfCheckedKeys();return e.unshift.apply(e,t),e},handleCheckedTreeExpand:function(e){for(var t=this.menuOptions,n=0;n<t.length;n++)this.$refs.menu.store.nodesMap[t[n].id].expanded=e},handleCheckedTreeNodeAll:function(e){this.$refs.menu.setCheckedNodes(e?this.menuOptions:[])},handleCheckedTreeConnect:function(e){this.form.menuCheckStrictly=!!e},reset:function(){void 0!==this.$refs.menu&&this.$refs.menu.setCheckedKeys([]),this.menuExpand=!1,this.menuNodeAll=!1,this.deptExpand=!0,this.deptNodeAll=!1,this.form={roleId:void 0,roleName:void 0,roleKey:void 0,roleSort:0,status:"0",menuIds:[],deptIds:[],menuCheckStrictly:!0,deptCheckStrictly:!0,remark:void 0}},cancel:function(){this.open=!1,this.reset()}}},c=a,l=n("2877"),d=Object(l["a"])(c,o,r,!1,null,null,null);t["default"]=d.exports}}]);