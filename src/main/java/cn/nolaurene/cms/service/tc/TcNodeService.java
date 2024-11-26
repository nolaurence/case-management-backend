package cn.nolaurene.cms.service.tc;

import cn.nolaurene.cms.common.dto.tc.ChangeAction;
import cn.nolaurene.cms.common.dto.tc.FrontendTcNode;
import cn.nolaurene.cms.common.dto.tc.NodeData;
import cn.nolaurene.cms.dal.enhance.mapper.TestCaseEnhancedMapper;
import cn.nolaurene.cms.dal.entity.CaseTestCaseDO;
import cn.nolaurene.cms.dal.mapper.CaseTestCaseMapper;
import cn.nolaurene.cms.exception.BusinessException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.mybatis.mapper.example.Example;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcNodeService {

    @Resource
    private CaseTestCaseMapper testCaseMapper;

    @Resource
    private TestCaseEnhancedMapper testCaseEnhancedMapper;

    /**
     * 添加节点 add move delete update
     */
    public boolean addNode(ChangeAction changeAction) {
        if (StringUtils.isBlank(changeAction.getAction()) || !changeAction.getAction().equals("add")) {
            return false;
        }
        FrontendTcNode node = changeAction.getNode();
        CaseTestCaseDO nodeToAdd = convertToDataObject(node, null);

        // 捞取父节点
        Example<CaseTestCaseDO> parentExample = new Example<>();
        Example.Criteria<CaseTestCaseDO> parentCriteria = parentExample.createCriteria();
        parentCriteria.andEqualTo(CaseTestCaseDO::getUid, node.getParentUid());
        List<CaseTestCaseDO> parentCaseTestCaseDOS = testCaseMapper.selectByExample(parentExample);
        if (CollectionUtils.isEmpty(parentCaseTestCaseDOS)) {
            throw new BusinessException("父节点不存在，请刷新页面");
        }

        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andEqualTo(CaseTestCaseDO::getUid, nodeToAdd.getUid());

        // 数据库不支持唯一键时，需要先查询是否存在 - -，无语
        List<CaseTestCaseDO> caseTestCaseDOS = testCaseMapper.selectByExample(example);
        if (!caseTestCaseDOS.isEmpty()) {
            // 判断是否删除
            if (caseTestCaseDOS.get(0).getIsDeleted()) {
                nodeToAdd.setIsDeleted(false);
                nodeToAdd.setId(caseTestCaseDOS.get(0).getId());
                int updateResult = testCaseMapper.updateByPrimaryKeySelective(nodeToAdd);
                return updateResult > 0;
            }
            throw new BusinessException("节点已存在");
        }

        // 插入父节点id 并插入
        nodeToAdd.setProjectId(parentCaseTestCaseDOS.get(0).getProjectId());
        nodeToAdd.setParentUid(parentCaseTestCaseDOS.get(0).getUid());
        int insertResult = testCaseMapper.insertSelective(nodeToAdd);

        return insertResult > 0;
    }

    /**
     * 移动节点
     */
    public boolean moveNode(ChangeAction changeAction) {
        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andEqualTo(CaseTestCaseDO::getUid, changeAction.getMoveData().getNodeUid());
        List<CaseTestCaseDO> caseTestCaseDOS = testCaseMapper.selectByExample(example);

        // 拿到要移动的节点
        if (CollectionUtils.isEmpty(caseTestCaseDOS)) {
            throw new BusinessException("移动的节点不存在，请刷新页面");
        }
        CaseTestCaseDO caseTestCaseDO = caseTestCaseDOS.get(0);
        if (caseTestCaseDO.getIsDeleted()) {
            throw new BusinessException("移动的节点不存在，请刷新页面");
        }

        // 拿到移动前后的父节点
        Example<CaseTestCaseDO> prevExample = new Example<>();
        Example.Criteria<CaseTestCaseDO> prevCriteria = prevExample.createCriteria();
        prevCriteria.andEqualTo(CaseTestCaseDO::getUid, changeAction.getMoveData().getFrom());
        List<CaseTestCaseDO> prevCaseTestCaseDOS = testCaseMapper.selectByExample(prevExample);

        Example<CaseTestCaseDO> targetExample = new Example<>();
        Example.Criteria<CaseTestCaseDO> targetCriteria = targetExample.createCriteria();
        prevCriteria.andEqualTo(CaseTestCaseDO::getUid, changeAction.getMoveData().getTo());
        List<CaseTestCaseDO> targetCaseTestCaseDOS = testCaseMapper.selectByExample(targetExample);

        if (CollectionUtils.isEmpty(prevCaseTestCaseDOS) || CollectionUtils.isEmpty(targetCaseTestCaseDOS)) {
            throw new BusinessException("父节点不存在，请刷新页面");
        }
        if (prevCaseTestCaseDOS.get(0).getIsDeleted() || targetCaseTestCaseDOS.get(0).getIsDeleted()) {
            throw new BusinessException("父节点不存在，请刷新页面");
        }
        CaseTestCaseDO targetCaseTestCaseDO = targetCaseTestCaseDOS.get(0);

        caseTestCaseDO.setParentUid(targetCaseTestCaseDO.getUid());
        int updateResult = testCaseMapper.updateByPrimaryKeySelective(caseTestCaseDO);

        return updateResult > 0;
    }

    /**
     * 更新节点
     */
    public boolean updateNode(ChangeAction changeAction) {
        FrontendTcNode node = changeAction.getNode();

        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andEqualTo(CaseTestCaseDO::getUid, node.getData().getUid());
        List<CaseTestCaseDO> caseTestCaseDOS = testCaseMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(caseTestCaseDOS)) {
            throw new BusinessException("更新的节点不存在，请刷新页面");
        }
        if (caseTestCaseDOS.get(0).getIsDeleted()) {
            throw new BusinessException("更新的节点不存在，请刷新页面");
        }

        CaseTestCaseDO caseTestCaseDO = caseTestCaseDOS.get(0);
        // 更新数据
        // 暂时只更新标签和名称
        caseTestCaseDO.setName(node.getData().getText());
        caseTestCaseDO.setTags(JSON.toJSONString(node.getData().getTag()));

        int updateResult = testCaseMapper.updateByPrimaryKeySelective(caseTestCaseDO);
        return updateResult > 0;
    }

    /**
     * 删除节点
     */
    public boolean deleteNode(ChangeAction changeAction) {
        FrontendTcNode node = changeAction.getNode();

        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andEqualTo(CaseTestCaseDO::getUid, node.getData().getUid());
        List<CaseTestCaseDO> caseTestCaseDOS = testCaseMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(caseTestCaseDOS)) {
            throw new BusinessException("更新的节点不存在，请刷新页面");
        }
        if (caseTestCaseDOS.get(0).getIsDeleted()) {
            return true;
        }

        CaseTestCaseDO caseTestCaseDO = caseTestCaseDOS.get(0);
        // 更新数据
        caseTestCaseDO.setIsDeleted(true);

        int updateResult = testCaseMapper.updateByPrimaryKeySelective(caseTestCaseDO);
        return updateResult > 0;
    }

    /**
     * 根据根节点 ID 构建测试用例树
     *
     * @param rootUid 根节点 UID
     * @return 构造的测试用例树结构
     */
    public FrontendTcNode buildTestCaseTree(String rootUid) {
        // 使用rootId捞取根节点
        Example<CaseTestCaseDO> rootExample = new Example<>();
        Example.Criteria<CaseTestCaseDO> rootCriteria = rootExample.createCriteria();
        rootCriteria.andEqualTo(CaseTestCaseDO::getUid, rootUid);
        rootCriteria.andEqualTo(CaseTestCaseDO::getIsDeleted, false);
        List<CaseTestCaseDO> rootNodeList = testCaseMapper.selectByExample(rootExample);

        if (rootNodeList.isEmpty() || !rootNodeList.get(0).getUid().equals(rootUid)) {
            throw new BusinessException("根节点不存在");
        }

        // 根据projectId捞取所有用例
        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andEqualTo(CaseTestCaseDO::getProjectId, rootNodeList.get(0).getProjectId());
        criteria.andEqualTo(CaseTestCaseDO::getIsDeleted, false);

        // 捞取所有用例节点
        List<CaseTestCaseDO> caseList = testCaseMapper.selectByExample(example);

        // 将每个节点按 ID 映射
        Map<String, FrontendTcNode> nodeMap = caseList.stream()
                .map(this::convertToNode)
                .collect(Collectors.toMap(FrontendTcNode::getUid, node -> node));

        // 构造树结构
        FrontendTcNode root = nodeMap.get(rootUid);
        for (FrontendTcNode node : nodeMap.values()) {
            String parentUid = node.getParentUid();
            if (parentUid != null && nodeMap.containsKey(parentUid)) {
                nodeMap.get(parentUid).getChildren().add(node);
            }
        }

        return root; // 返回构造完成的树结构
    }

    public boolean saveTree(FrontendTcNode rootNode) {
        // 创建一个空的 map 来存储所有的 CaseTestCaseDO 实例
        Map<String, CaseTestCaseDO> nodeMap = new HashMap<>();

        // 计算层级关系
        computeHierarchy(rootNode, nodeMap, null, 0);

        // 获取所有的 CaseTestCaseDO 实例
        List<CaseTestCaseDO> toBeSaved = new ArrayList<>(nodeMap.values());

        // 批量插入数据库
        int rowsAffected = testCaseEnhancedMapper.batchInsert(toBeSaved);
        log.info("插入了 {} 行.", rowsAffected);
        return rowsAffected == toBeSaved.size();
    }

    private Map<String, CaseTestCaseDO> computeHierarchy(FrontendTcNode rootNode, Map<String, CaseTestCaseDO> nodeMap, String parentUid, int depth) {
        CaseTestCaseDO caseTestCaseDO = convertToDataObject(rootNode, null);
        caseTestCaseDO.setParentUid(parentUid);
        caseTestCaseDO.setDepth(depth);
        nodeMap.put(caseTestCaseDO.getUid(), caseTestCaseDO);

        if (rootNode.getChildren() != null && !rootNode.getChildren().isEmpty()) {
            for (FrontendTcNode childNode : rootNode.getChildren()) {
                computeHierarchy(childNode, nodeMap, caseTestCaseDO.getUid(), depth + 1);
            }
        }
        return nodeMap;
    }

    public boolean batchAddNode(ChangeAction changeAction) {
        List<FrontendTcNode> nodes = changeAction.getNodeList();
        if (CollectionUtils.isEmpty(nodes)) {
            return false;
        }

        // 捞取父节点，并形成映射
        Set<String> parentUidList = nodes.stream().map(FrontendTcNode::getParentUid).collect(Collectors.toSet());

        List<String> nodeToSaveUidList = nodes
                .stream()
                .map(FrontendTcNode::getData)
                .map(NodeData::getUid)
                .collect(Collectors.toList());

        // 捞取父节点
        Example<CaseTestCaseDO> parentExample = new Example<>();
        Example.Criteria<CaseTestCaseDO> parentCriteria = parentExample.createCriteria();
        parentCriteria.andIn(CaseTestCaseDO::getUid, parentUidList);
        parentCriteria.andEqualTo(CaseTestCaseDO::getIsDeleted, false);
        List<CaseTestCaseDO> parentCaseTestCaseDOS = testCaseMapper.selectByExample(parentExample);

        if (parentCaseTestCaseDOS.size() != parentUidList.size()) {

            // 有部分节点可能是本次存入的，查一下
            Set<String> parentUidSet = new HashSet<>(parentUidList);
            Set<String> nodeToSaveUidSet = new HashSet<>(nodeToSaveUidList);

            for (FrontendTcNode node : nodes) {
                String uid = node.getData().getUid();

                // 库里和本轮都没有，则
                if (!parentUidSet.contains(uid) && !nodeToSaveUidSet.contains(uid)) {
                    throw new BusinessException("有不存在的父节点，请刷新页面");
                }
            }
        }

        // 转换并添加projectid
        List<CaseTestCaseDO> caseTestCaseDOS = nodes.stream().map(node -> convertToDataObject(node, null)).collect(Collectors.toList());
        caseTestCaseDOS.forEach(caseTestCaseDO -> {
            caseTestCaseDO.setProjectId(parentCaseTestCaseDOS.get(0).getProjectId());
        });

        int insertResult = testCaseEnhancedMapper.batchInsert(caseTestCaseDOS);
        return insertResult > 0;
    }

    public boolean batchDeleteNode(ChangeAction changeAction) {
        List<FrontendTcNode> nodes = changeAction.getNodeList();
        if (CollectionUtils.isEmpty(nodes)) {
            return true;
        }

        Set<String> uidList = nodes.stream().map(FrontendTcNode::getData).map(NodeData::getUid).collect(Collectors.toSet());

        Example<CaseTestCaseDO> example = new Example<>();
        Example.Criteria<CaseTestCaseDO> criteria = example.createCriteria();
        criteria.andIn(CaseTestCaseDO::getUid, uidList);

        List<CaseTestCaseDO> caseTestCaseDOS = testCaseMapper.selectByExample(example);

        if (caseTestCaseDOS.size() != uidList.size()) {
            throw new BusinessException("有不存在的节点，请刷新页面");
        }

        caseTestCaseDOS.forEach(caseTestCaseDO -> caseTestCaseDO.setIsDeleted(true));
//        testCaseMapper.up

        int updateResult = testCaseEnhancedMapper.batchUpdate(caseTestCaseDOS);
        return updateResult > 0;
    }

    /**
     * 数据转换，但不拷贝深度和children
     *
     * @param node
     * @param parentIdMap
     * @return
     */
    private CaseTestCaseDO convertToDataObject(FrontendTcNode node, Map<String, Long> parentIdMap) {
        CaseTestCaseDO caseTestCaseDO = new CaseTestCaseDO();
        caseTestCaseDO.setUid(node.getData().getUid());
        caseTestCaseDO.setName(node.getData().getText());
        caseTestCaseDO.setTags(JSON.toJSONString(node.getData().getTag()));

        JSONObject extension = new JSONObject();
        extension.put("generalization", node.getData().getGeneralization());
        extension.put("expand", node.getData().isExpand());
        extension.put("isActive", node.getData().isActive());
        caseTestCaseDO.setExtension(extension.toJSONString());
        caseTestCaseDO.setParentUid(node.getParentUid());

        return caseTestCaseDO;
    }

    /**
     * 将 CaseTestCaseDO 对象转换为 FrontendTcNode 节点
     *
     * @param caseTestCase 测试用例对象
     * @return 转换后的树节点
     */
    private FrontendTcNode convertToNode(CaseTestCaseDO caseTestCase) {
        FrontendTcNode node = new FrontendTcNode();
        node.setId(caseTestCase.getId());               // 设置 id
        node.setUid(caseTestCase.getUid());             // 设置 uid
        node.setParentUid(caseTestCase.getParentUid()); // 设置 parentId

        NodeData data = new NodeData();
        data.setText(caseTestCase.getName());
        data.setUid(caseTestCase.getUid());
        data.setTag(stringIsNull(caseTestCase.getTags()) ? new ArrayList<>() : Arrays.asList(caseTestCase.getTags().split(",")));
        data.setGeneralization(caseTestCase.getBizExtension() != null ? Arrays.asList(caseTestCase.getBizExtension().split(",")) : new ArrayList<>());
        data.setExpand(true);  // 默认展开，按需求设置

        node.setData(data);
        node.setChildren(new ArrayList<>());

        return node;
    }

    private boolean stringIsNull(String tags) {
        if (StringUtils.isEmpty(tags)) {
            return true;
        }
        if (tags.equals("null")) {
            return true;
        }
        if (tags.equals("[]")) {
            return true;
        }
        return false;
    }

}
