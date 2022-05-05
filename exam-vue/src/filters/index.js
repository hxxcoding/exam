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

export function examType(value) {
  const map = {
    '0': '模拟练习',
    '1': '正式考试'
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
    'isBold': '是否加粗',
    'isItalic': '是否斜体',
    'getPgSzW': '页面宽度',
    'getPgSzH': '页面高度',
    'getPgMarLeft': '左页边距',
    'getPgMarRight': '右页边距',
    'getPgMarTop': '上页边距',
    'getPgMarBottom': '下页边距',
    'getPgMarGutter': '装订线',
    'getPgMarHeader': '页眉距边缘',
    'getPgMarFooter': '页脚距边缘',
    'getColsNum': '分栏数量',
    'isColsLine': '是否分栏',
    'getIndentationFirstLine': '首行缩进',
    'getIndentationHanging': '悬挂缩进',
    'getSpacingBetween': '行间距',
    'getNumFmt': '项目符号',
    'getFontSize': '字号',
    'getFontFamily': '字体',
    'getChineseFontFamily': '默认中文字体',
    'getAsciiFontFamily': '默认英文字体',
    'getUnderlineType': '下划线类型',
    'getPicTextAround': '图片环绕方式'
  }
  return map[value]
}

export function excelMethodFilter(value) {
  const map = {
    'getFunction': '获取函数',
    'getNumFormatCode': '获取数字格式',
    'getNumValue': '获取数字值',
    'getBorderStyleTop': '获取顶部边框样式',
    'getBorderStyleBottom': '获取底部边框样式',
    'getBorderStyleLeft': '获取左边框样式',
    'getBorderStyleRight': '获取右边框样式',
    'getFuncRefRange': '获取函数的影响范围',
    'getMergedRegion': '获取合并的单元格',
    'getRichTextString': '获取单元格的文字',
    'getFontName': '获取字体名称',
    'getFontSize': '获取字体大小',
    'isBold': '是否加粗',
    'getAlignment': '获取对齐方式',
    'getFillBackgroundColor': '获取背景填充颜色',
    'getFillForegroundColor': '获取图案填充颜色',
    'getForegroundPatternType': '获取图案类型',
    'getRowHeight': '获取行高',
    'getColWidth': '获取列宽',
    'getPrintPaperSize': '获取打印纸张类型',
    'getPrintOrientation': '获取打印方向',
    'getPrintHeaderMargin': '获取打印页眉边距',
    'getPrintFooterMargin': '获取打印页脚边距',
    'getPrintTopMargin': '获取打印上页边距',
    'getPrintBottomMargin': '获取打印下页边距',
    'getPrintLeftMargin': '获取打印左页边距',
    'getPrintRightMargin': '获取打印右页边距',
    'isPrintHorizontallyCenter': '打印页面是否水平居中',
    'isPrintVerticallyCenter': '打印页面是否垂直居中',
    'isPrintGridLines': '是否打印网格线',
    'isPrintHeadings': '是否打印行列号',
    'getHeaderContent': '获取页眉内容和格式',
    'getFooterContent': '获取页脚内容和格式',
    'getConditionalFormatting': '获取条件格式',
    'getChartTitle': '获取图表标题',
    'getChartCatTitle': '获取cat坐标标题',
    'getChartValTitle': '获取val坐标标题',
    'getChartDataRef': '获取图表的数据范围',
    'isChartShowVal': '是否图表旁显示值',
    'getChartMajorUnit': '获取图表坐标的数据间隔大小',
    'getSortStateRef': '获取排序影响的范围',
    'getSortConditionRef': '获取排序条件',
    'getAutoFilterRef': '获取自动筛选的范围',
    'getFilterVal': '获取筛选条件'
  }
  return map[value]
}

export function pptMethodFilter(value) {
  const map = {
    'getThemeName': '获取主题',
    'getJumpHyperlink': '获取跳转超链接',
    'getAnimMainSeqAction': '获取主序列动画',
    'getTransitionMode': '获取过渡方式'
  }
  return map[value]
}
