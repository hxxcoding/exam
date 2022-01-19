package com.yf.exam.core.utils.poi;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *
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
     * 获取主题/设计模版名称
     * @param pos
     * @return
     */
    public String getThemeName(int pos) {
        XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(pos);
        return xslfSlide.getTheme().getName();
    }

    /**
     * 只能获取第一个跳转的超链接
     * @param pos
     * @return
     */
    public String getJumpHyperlink(int pos){
        try {
            List<XSLFSlide> slides = xmlSlideShow.getSlides();
            List<CTShape> spList = slides.get(pos).getXmlObject().getCSld().getSpTree().getSpList();
            for (CTShape sp : spList) {
                if (sp.getNvSpPr().getCNvPr().getHlinkClick() != null) {
                    String action = sp.getNvSpPr().getCNvPr().getHlinkClick().getAction();
                    int index = action.indexOf("jump=");
                    if (index != -1){
                        return action.substring(index);
                    }
                }
            }
            return null;
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * 这里的进入动画 需要传一个动画次序的参数 并且每个动画的样式不同内容也不同 看看怎么实现复用
     * @param slidePos 幻灯片的位置
     * @param actionPos 动作在幻灯片中的顺序
     * @return
     */
    public String getMainSeqAction(Integer slidePos, Integer actionPos){
        try {
            XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(slidePos);
            CTTLCommonTimeNodeData mainSeq = xslfSlide.getXmlObject().getTiming().getTnLst().getParArray(0).getCTn()
                    .getChildTnLst().getSeqArray(0).getCTn();
            CTTLTimeNodeParallel timeNodeParallel = mainSeq.getChildTnLst().getParList().get(actionPos);// 第1个动画
            CTTimeNodeList childTnLst = timeNodeParallel.getCTn().getChildTnLst().getParArray(0).getCTn().getChildTnLst().getParArray(0).getCTn().getChildTnLst();
            return childTnLst.getAnimEffectArray(0).getFilter();
        } catch (NullPointerException e){
            return null;
        }
    }

    /**
     * 获取第pos页ppt进入时的过渡方式, 仅支持获取简单的过渡方式, 高级过渡方式待研究
     * @param pos
     * @return
     */
    public String getTransition(int pos){
        try{
            XSLFSlide xslfSlide = xmlSlideShow.getSlides().get(pos);
            CTSlideTransition transition = xslfSlide.getXmlObject().getTransition();
            if (transition == null) return null;
            String s = transition.toString();
            return s.substring(s.indexOf("<p:"), s.lastIndexOf("\n"));
        } catch (NullPointerException e){
            return null;
        }
    }

}
