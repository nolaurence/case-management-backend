package cn.nolaurene.cms.common.dto.tc;

import lombok.Data;

import java.util.List;

@Data
public class NodeData {

    private String text;

    private List<String> generalization;

    private List<String> tag;

    private boolean expand;

    private boolean isActive;

    private String uid;

    // 以下字段暂时未用到，先定义
    private String richText;

    private String image;

    private String imageTitle;

    private ImageSize imageSize;

    private String hyperlink;

    private String hyperlinkTitle;

    private String note;

    private String attachmentUrl;

    private String attachmentName;

    private List<String> associativeLineTargets;

    private String associativeLineText;
}
