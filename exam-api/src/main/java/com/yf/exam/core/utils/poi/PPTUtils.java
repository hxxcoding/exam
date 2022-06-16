package com.yf.exam.core.utils.poi;

import com.yf.exam.core.exception.ServiceException;
import com.yf.exam.core.utils.Reflections;
import com.yf.exam.modules.qu.dto.PPTSlideDTO;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGradientPaint;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.presentationml.x2006.main.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  PPT判分方法
 * </p>
 *
 * @author Xiaoxiao Hu
 * @date 2022/1/17 19:38
 */
public class PPTUtils {

    private XMLSlideShow xmlSlideShow;

    public PPTUtils(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            xmlSlideShow = new XMLSlideShow(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过方法名和参数获取方法并执行
     * @param methodName
     * @param args
     * @return
     */
    public Object executeMethod(String methodName, Object... args) {
        try {
            Class<?>[] classes = null;
            if (args[0] == null) {
                args = null;
            } else {
                classes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            }
            return Reflections.invokeMethod(this, methodName, classes, args);
        } catch (Exception e) { // 捕获所有异常,发生异常表示获取不到对应答案,判错
            return null;
        }
    }

    /**
     * 分析ppt文件页面
     * @param filePath
     * @return
     */
    public static List<PPTSlideDTO> analyzePPTSlide(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            XMLSlideShow xmlSlideShow = new XMLSlideShow(fis);
            List<XSLFSlide> slides = xmlSlideShow.getSlides();
            List<PPTSlideDTO> res = new ArrayList<>();
            for (int i = 0; i < slides.size(); i++) {
                res.add(new PPTSlideDTO(String.valueOf(i), slides.get(i).getSlideName()));
            }
            return res;
        } catch (FileNotFoundException e) {
            throw new ServiceException("文件不存在！");
        } catch (IOException e) {
            throw new ServiceException("文件解析失败！");
        }
    }

    /**
     * 获取主题/设计模版名称
     * @param pos
     * @return
     */
    public String getThemeName(Integer pos) {
        XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(pos);
        return xslfSlide.getTheme().getName();
    }

    /**
     * 获取该页面的第一个跳转的超链接的动作
     * @param pos
     * @return
     */
    public String getJumpHyperlinkAction(Integer pos){
        List<CTShape> spList = pgetSpList(pos);
        for (CTShape sp : spList) {
            CTHyperlink hyperlink = sp.getNvSpPr().getCNvPr().getHlinkClick();
            if (hyperlink != null) {
                String action = hyperlink.getAction();
                String name = sp.getNvSpPr().getCNvPr().getName();
                return action + "&" + name.substring(0, name.length() - 2);
            }
        }
        return null;
    }

    /**
     * 获取该页面的第一个跳转的超链接所在组件的名称
     * @param pos
     * @return
     */
    public String getJumpHyperlinkComponentName(Integer pos){
        List<CTShape> spList = pgetSpList(pos);
        for (CTShape sp : spList) {
            if (sp.getNvSpPr().getCNvPr().getHlinkClick() != null) {
                String name = sp.getNvSpPr().getCNvPr().getName();
                return name.substring(0, name.length() - 2);
            }
        }
        return null;
    }

    /**
     * 获取动画主序列动作信息
     * 动画样式 只能获取进入/退出动画 需要传一个动画次序的参数 并且每个动画的样式不同内容也不同 看看怎么实现复用
     * `飞入`效果无法获取
     * 可用动画列表为：百叶窗/棋盘/向内溶解/切入/随机线条/形状/劈裂/阶梯状/楔入/轮子/擦除/展开/淡入/旋转/缩放
     *          /中心旋转/浮入/翻转式由远及近/升起/回旋/飞旋/弹跳/曲线向上/浮动/玩具风车/挥鞭式
     * 其中，展开/淡入/旋转/缩放/中心旋转/浮入/翻转式由远及近/升起/回旋/飞旋/曲线向上/浮动/玩具风车/挥鞭式 的格式相同均为均为 fade
     * @param slidePos 幻灯片的位置
     * @param actionPos 动作在幻灯片中的顺序
     * @return 动画样式_动画出现时间
     */
    public String getAnimMainSeqAction(Integer slidePos, Integer actionPos){
        XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(slidePos);
        CTTLCommonTimeNodeData mainSeq = xslfSlide.getXmlObject().getTiming().getTnLst().getParArray(0).getCTn()
                .getChildTnLst().getSeqArray(0).getCTn();
        CTTLTimeNodeParallel timeNodeParallel = mainSeq.getChildTnLst().getParArray(actionPos);// 第actionPos个动画
        CTTimeNodeList childTnLst = timeNodeParallel.getCTn().getChildTnLst().getParArray(0)
                .getCTn().getChildTnLst().getParArray(0).getCTn().getChildTnLst();
        CTTLAnimateEffectBehavior actionBehavior = childTnLst.getAnimEffectArray(0);
        return actionBehavior.getFilter() + "&" + actionBehavior.getTransition();
    }

    public String getAnimMainSeqAction(Integer slidePos) {
        return getAnimMainSeqAction(slidePos, 0);
    }

//    public String getAnimMainSeqAction(String pos) {
//        String[] p = pos.split(",");
//        Integer slidePos = Integer.parseInt(p[0]);
//        Integer actionPos = null;
//        if (p.length >= 2) {
//            actionPos = Integer.parseInt(p[1]);
//        }
//        return getAnimMainSeqAction(slidePos, actionPos != null ? actionPos : 0);
//    }

    /**
     * 获取第pos页ppt进入时的过渡方式, 仅支持获取简单的过渡方式, 高级渡方式待研究过
     * 可用的页面切换方式为：推入/擦除/随即线条/揭开/覆盖/时钟/梳理
     */
    public String getTransitionMode(Integer pos){
        XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(pos);
        CTSlideTransition transition = xslfSlide.getXmlObject().getTransition();
        if (transition == null) return null;
        String s = transition.toString();
        return s.substring(s.indexOf("<p:"), s.lastIndexOf("\n"));
    }

    /**
     * 获取版式名称
     * @param pos
     * @return
     */
    public String getLayoutName(Integer pos) {
        final List<XSLFSlide> slides = xmlSlideShow.getSlides();
        return slides.get(pos).getSlideLayout().getName();
    }

    /**
     * 获取标题(第一个文本框)文字
     * @param pos
     * @return
     */
    public String getTitleText(Integer pos) {
        return pgetSpList(pos).get(0).getTxBody().getPArray(0).getRArray(0).getT(); // 获取标题文字
    }

    /**
     * 获取标题(第一个文本框)中文字体
     * pgetSpList(pos).get(0).getTxBody().getPArray(0).getRArray(0).getRPr().getLatin().getTypeface(); // 获取
     * @param pos
     * @return
     */
    public String getTitleEaFontName(Integer pos) {
        return pgetSpList(pos).get(0).getTxBody().getPArray(0).getRArray(0).getRPr().getEa().getTypeface();
    }

    /**
     * 获取标题(第一个文本框)字体大小
     * pgetSpList(pos).get(0).getTxBody().getPArray(0).getRArray(0).getRPr().getLatin().getTypeface(); // 获取
     * @param pos
     * @return
     */
    public Integer getTitleEaFontSize(Integer pos) {
        return pgetSpList(pos).get(0).getTxBody().getPArray(0).getRArray(0).getRPr().getSz();
    }

    /**
     * 获取背景填充类型 需要获取某个填充类型的设置，需要强制类型转换
     * @return "XSLFGradientPaint"为渐变填充 "XSLFTexturePaint"为图片或纹理填充
     */
    public String getBackgroundFillStyle(Integer pos) {
        final XSLFSlide slide = xmlSlideShow.getSlides().get(pos);
        return slide.getBackground().getFillStyle().getPaint().getClass().getSimpleName();
    }

    /**
     * 获取渐变背景类型
     */
    public String getBackgroundGradientType(Integer pos) {
        final XSLFSlide slide = xmlSlideShow.getSlides().get(pos);
        final XSLFGradientPaint paint = (XSLFGradientPaint) slide.getBackground().getFillStyle().getPaint();
        return paint.getGradientType().toString();
    }

    /**
     * 获取渐变背景角度
     */
    public Double getBackgroundGradientAngle(Integer pos) {
        final XSLFSlide slide = xmlSlideShow.getSlides().get(pos);
        final XSLFGradientPaint paint = (XSLFGradientPaint) slide.getBackground().getFillStyle().getPaint();
        return paint.getGradientAngle();
    }

    /**
     * 获取内容(第二个文本框)文字
     * @param pos
     * @return
     */
    public String getContentText(Integer pos) {
        return pgetSpList(pos).get(1).getTxBody().getPArray(0).getRArray(0).getT(); // 获取标题文字
    }

    private List<CTShape> pgetSpList(Integer pos) {
        final List<XSLFSlide> slides = xmlSlideShow.getSlides();
        return slides.get(pos).getXmlObject().getCSld().getSpTree().getSpList();
    }

}
