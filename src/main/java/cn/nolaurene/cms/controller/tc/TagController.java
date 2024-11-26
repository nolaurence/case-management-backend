package cn.nolaurene.cms.controller.tc;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@Tag(name = "测试用例标签相关api")
public class TagController {
}
