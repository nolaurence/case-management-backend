# 测试用例管理平台-be
# test case management platform backend

前端 ( frontend repo ) : https://github.com/nolaurence/case-management-frontend

### 项目介绍
1. 支持xmind类型的测试用例在线编写
2. 测试模块和用例分离
3. 角色登录以及权限相关
4. 用例的权限管理

### Project intro
1. support xmind type test case online edit
2. test module and case separate
3. role login and permission
4. （in design）permission management of case


### 当前的开发进度
* 完成了角色登录，登出，注销相关功能
* 完成了xmind的前端构建，并且增删改查同步后端存储
* 基础的测试项目管理功能
* 测试用例标签管理和打标功能
* 测试用例上传图片功能（developing）

### 计划中的features
* 有限的xmind侧主题定制能力 + 暗黑模式
* 尽可能多支持原生的快捷键
* xmind测试用例严格模式（即用例标题起头，且子节点需要带有前置条件，执行步骤，预期结果三种节点）
* 测试计划功能

### 注意事项
本工程冗余了一些私有工程中的代码，后期会进行剔除处理~~

### Current development progress
* basic login, logout with role manage features
* xmind based test case writing and management with backend persistent storage
* basic test program management features

### Credit

前端工程基于wangling2/mind-map构建：https://github.com/wanglin2/mind-map


### 具体开发的milestone
- [x] 完成用户登录相关功能  
- [x] xmind 前端技术预研  
- [x] xmind 管理的相关功能开发  
- [x] xmind 功能开发
- [x] xmind 后端存储设计 + 开发
- [ ] 角色权限管理功能
- [ ] 测试用例权限管理
- [ ] 图片上传模块
- [ ] 标签管理模块
- [ ] url管理模块
