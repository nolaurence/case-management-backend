package cn.nolaurene.cms.controller.tc;

import cn.nolaurene.cms.common.vo.BaseWebResult;
import cn.nolaurene.cms.common.vo.tc.OSTreeNode;
import cn.nolaurene.cms.service.OSService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author guofukang.gfk
 * @date 2024/11/1.
 */
@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private OSService osService;

    @GetMapping("/{rootOrgId}/tree")
    public BaseWebResult<OSTreeNode> getOrganizationTree(@PathVariable Long rootOrgId) {
        OSTreeNode tree = osService.getOrganizationTree(rootOrgId);
        if (tree == null) {
            return BaseWebResult.fail("未找到根节点");
        }
        return BaseWebResult.success(tree);
    }
}
