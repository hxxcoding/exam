// import parseTime, formatTime and set to filter
export { parseTime, formatTime } from '@/utils'

/**
 * Upper case first char
 * @param {String} string
 */
export function uppercaseFirst(string) {
  return string.charAt(0).toUpperCase() + string.slice(1)
}

/**
 * 通用状态过滤器
 * @param value
 * @returns {*}
 */
export function stateFilter(value) {
  const map = {
    '0': '正常',
    '1': '禁用'
  }
  return map[value]
}

export function quTypeFilter(value) {
  const map = {
    '1': '单选题',
    '2': '多选题',
    '3': '判断题',
    // '4': '操作题',
    '5': '填空题',
    '10': 'Word操作题',
    '11': 'Excel操作题',
    '12': 'PPT操作题'
  }
  return map[value]
}

export function paperStateFilter(value) {
  const map = {
    '0': '考试中',
    '1': '待阅卷',
    '2': '已考完',
    '3': '!已弃考'
  }
  return map[value]
}

export function examOpenType(value) {
  const map = {
    '1': '完全公开',
    '2': '指定部门'
  }
  return map[value]
}

export function examStateFilter(value) {
  const map = {
    '0': '进行中',
    '1': '已禁用',
    '2': '待开始',
    '3': '已结束'
  }
  return map[value]
}

export function wordMethodFilter(value) {
  const map = {
    'isBold': '是否有下划线',
    'isItalic': '是否斜体',
    'getPgSzW': '页面宽度',
    'getPgSzH': '页面高度',
    'getPgMarLeft': '左页边距',
    'getPgMarRight': '右页边距',
    'getPgMarTop': '上页边距',
    'getPgMarBottom': '下页边距',
    'getColsNum': '分栏数量',
    'isColsLine': '是否分栏',
    'getIndentationFirstLine': '首行缩进',
    'getIndentationHanging': '悬挂缩进',
    'getSpacingBetween': '行间距',
    'getNumFmt': '项目符号',
    'getFontSize': '字号',
    'getFontFamily': '字体',
    'getUnderlineType': '下划线类型',
    'getPicTextAround': '图片环绕方式'
  }
  return map[value]
}
