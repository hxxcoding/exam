(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-76c28760"],{"14a5":function(t,e,a){"use strict";a.d(e,"b",(function(){return r})),a.d(e,"a",(function(){return n}));var i=function(t,e){var a="1.23452384164.123412415";null!==document.getElementById(a)&&document.body.removeChild(document.getElementById(a));var i=document.createElement("canvas");i.width=200,i.height=120;var r=i.getContext("2d");r.rotate(-20*Math.PI/180),r.font="16px Vedana",r.fillStyle="#666666",r.textAlign="center",r.textBaseline="Middle",r.fillText(t,i.width/2,i.height),r.fillText(e,i.width/2,i.height+22);var n=document.createElement("div");return n.id=a,n.style.pointerEvents="none",n.style.top="0px",n.style.left="0px",n.style.opacity="0.15",n.style.position="fixed",n.style.zIndex="100000",n.style.width=document.documentElement.clientWidth+"px",n.style.height=document.documentElement.clientHeight+"px",n.style.background="url("+i.toDataURL("image/png")+") left top repeat",document.body.appendChild(n),a},r=function(t,e){var a=i(t,e);null===document.getElementById(a)&&(a=i(t,e))},n=function(){var t="1.23452384164.123412415";null!==document.getElementById(t)&&document.body.removeChild(document.getElementById(t))}},"14f3":function(t,e,a){"use strict";a("c45e")},3200:function(t,e,a){"use strict";a.d(e,"a",(function(){return r})),a.d(e,"d",(function(){return n})),a.d(e,"f",(function(){return s})),a.d(e,"b",(function(){return l})),a.d(e,"c",(function(){return c})),a.d(e,"e",(function(){return o}));var i=a("b775");function r(t){return Object(i["b"])("/exam/api/paper/paper/create-paper",t)}function n(t){return Object(i["b"])("/exam/api/paper/paper/paper-detail",t)}function s(t){return Object(i["b"])("/exam/api/paper/paper/qu-detail",t)}function l(t){return Object(i["b"])("/exam/api/paper/paper/fill-answer",t)}function c(t){return Object(i["b"])("/exam/api/paper/paper/hand-exam",t)}function o(t){return Object(i["b"])("/exam/api/paper/paper/paper-result",t)}},b3de:function(t,e,a){"use strict";a.r(e);var i=function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"app-container"},[a("h2",{staticClass:"text-center"},[t._v(t._s(t.paperData.title))]),a("p",{staticClass:"text-center",staticStyle:{color:"#666"}},[t._v(t._s(t.paperData.createTime))]),a("el-row",{staticStyle:{"margin-top":"50px"},attrs:{gutter:24}},[a("el-col",{staticClass:"text-center",attrs:{span:8}},[t._v(" 考生姓名："+t._s(t.paperData.userId_dictText)+" ")]),a("el-col",{staticClass:"text-center",attrs:{span:8}},[t._v(" 考试用时："+t._s(t.paperData.userTime)+"分钟 ")]),a("el-col",{staticClass:"text-center",attrs:{span:8}},[t._v(" 考试得分："+t._s(t.paperData.userScore)+" ")])],1),a("el-card",{staticStyle:{"margin-top":"20px"}},t._l(t.paperData.quList,(function(e){return a("div",{key:e.id,staticClass:"qu-content"},[a("p",[t._v(t._s(e.sort+1)+"."+t._s(e.content)+"（分值："+t._s(e.score)+"）")]),null!=e.image&&""!=e.image?a("div",[a("el-image",{staticStyle:{"max-width":"100%"},attrs:{src:e.image}},[a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("el-link",{attrs:{href:e.image,type:"primary",target:"_blank",icon:"el-icon-files",underline:!0}},[t._v(" 下载附件 ")])],1)])],1):t._e(),1===e.quType||3===e.quType?a("div",[a("el-radio-group",{model:{value:t.radioValues[e.id],callback:function(a){t.$set(t.radioValues,e.id,a)},expression:"radioValues[item.id]"}},t._l(e.answerList,(function(e){return a("el-radio",{key:e.id,attrs:{label:e.id}},[t._v(" "+t._s(e.abc)+"."+t._s(e.content)+" "),null!=e.image&&""!==e.image?a("div",{staticStyle:{clear:"both"}},[a("el-image",{staticStyle:{"max-width":"100%"},attrs:{src:e.image}},[a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("el-link",{attrs:{href:e.image,type:"primary",target:"_blank",icon:"el-icon-files",underline:!0}},[t._v(" 下载附件 ")])],1)])],1):t._e()])})),1),a("el-row",{attrs:{gutter:24}},[a("el-col",{staticStyle:{color:"#24da70"},attrs:{span:12}},[t._v(" 正确答案："+t._s(t.radioRights[e.id])+" ")]),e.answered?t._e():a("el-col",{staticStyle:{"text-align":"right",color:"#ff0000"},attrs:{span:12}},[t._v(" 答题结果：未答 ")]),e.answered&&!e.isRight?a("el-col",{staticStyle:{"text-align":"right",color:"#ff0000"},attrs:{span:12}},[t._v(" 答题结果："+t._s(t.myRadio[e.id])+" ")]):t._e(),e.answered&&e.isRight?a("el-col",{staticStyle:{"text-align":"right",color:"#24da70"},attrs:{span:12}},[t._v(" 答题结果："+t._s(t.myRadio[e.id])+" ")]):t._e()],1)],1):t._e(),4===e.quType?a("div",[a("el-row",{attrs:{gutter:24}},[a("el-col",{attrs:{span:12}},[t._v(" 回答： "),null!=e.answer&&""!==e.answer?a("div",[a("el-image",{staticStyle:{"max-width":"100%"},attrs:{src:e.answer}},[a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("el-link",{attrs:{href:e.answer,type:"primary",target:"_blank",icon:"el-icon-files",underline:!0}},[t._v(" "+t._s(e.answer)+" ")])],1)])],1):t._e()])],1)],1):t._e(),5===e.quType?a("div",[a("el-row",{attrs:{gutter:24}},[a("el-col",{attrs:{span:12}},[t._v(" 回答： "),a("el-input",{staticStyle:{width:"50%"},attrs:{size:"large",disabled:""},model:{value:e.answer,callback:function(a){t.$set(e,"answer",a)},expression:"item.answer"}})],1)],1),a("el-row",{attrs:{gutter:24}},[a("el-col",{staticStyle:{color:"#24da70"},attrs:{span:12}},[t._v(" 正确答案："+t._s(t.blankValues[e.id])+" ")])],1)],1):t._e(),2===e.quType?a("div",[a("el-checkbox-group",{model:{value:t.multiValues[e.id],callback:function(a){t.$set(t.multiValues,e.id,a)},expression:"multiValues[item.id]"}},t._l(e.answerList,(function(e){return a("el-checkbox",{key:e.id,attrs:{label:e.id}},[t._v(t._s(e.abc)+"."+t._s(e.content)+" "),null!=e.image&&""!==e.image?a("div",{staticStyle:{clear:"both"}},[a("el-image",{staticStyle:{"max-width":"100%"},attrs:{src:e.image}},[a("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[a("el-link",{attrs:{href:e.image,type:"primary",target:"_blank",icon:"el-icon-files",underline:!0}},[t._v(" 下载附件 ")])],1)])],1):t._e()])})),1),a("el-row",{attrs:{gutter:24}},[a("el-col",{staticStyle:{color:"#24da70"},attrs:{span:12}},[t._v(" 正确答案："+t._s(t.multiRights[e.id].join(","))+" ")]),e.answered?t._e():a("el-col",{staticStyle:{"text-align":"right",color:"#ff0000"},attrs:{span:12}},[t._v(" 答题结果：未答 ")]),e.answered&&!e.isRight?a("el-col",{staticStyle:{"text-align":"right",color:"#ff0000"},attrs:{span:12}},[t._v(" 答题结果："+t._s(t.myMulti[e.id].join(","))+" ")]):t._e(),e.answered&&e.isRight?a("el-col",{staticStyle:{"text-align":"right",color:"#24da70"},attrs:{span:12}},[t._v(" 答题结果："+t._s(t.myMulti[e.id].join(","))+" ")]):t._e()],1)],1):t._e()])})),0)],1)},r=[],n=(a("ac6a"),a("7f7f"),a("db72")),s=a("3200"),l=a("14a5"),c=a("2f62"),o={name:"AuctionGoodsDetail",data:function(){return{paperId:"",paperData:{quList:[]},radioValues:{},multiValues:{},blankValues:{},radioRights:{},multiRights:{},myRadio:{},myMulti:{}}},computed:Object(n["a"])({},Object(c["b"])(["sidebar","avatar","device","name","realName"])),created:function(){var t=this.$route.params.id;"undefined"!==typeof t&&(this.paperId=t,this.fetchData(t))},mounted:function(){Object(l["b"])(this.realName,this.name)},destroyed:function(){Object(l["a"])()},methods:{fetchData:function(t){var e=this,a={id:t};Object(s["e"])(a).then((function(t){e.paperData=t.data,e.paperData.quList.forEach((function(t){var a="",i="",r="",n=[],s=[],l=[],c="";t.answerList.forEach((function(e){e.checked&&(1===t.quType||3===t.quType?(a=e.id,r=e.abc):(n.push(e.id),l.push(e.abc))),e.isRight&&(1===t.quType||3===t.quType?i=e.abc:s.push(e.abc)),5===t.quType&&(c=e.content)})),e.multiValues[t.id]=n,e.radioValues[t.id]=a,e.radioRights[t.id]=i,e.multiRights[t.id]=s,e.myRadio[t.id]=r,e.myMulti[t.id]=l,e.blankValues[t.id]=c})),console.log(e.multiValues),console.log(e.radioValues)}))}}},u=o,d=(a("14f3"),a("2877")),p=Object(d["a"])(u,i,r,!1,null,"7fcc9c4f",null);e["default"]=p.exports},c45e:function(t,e,a){}}]);