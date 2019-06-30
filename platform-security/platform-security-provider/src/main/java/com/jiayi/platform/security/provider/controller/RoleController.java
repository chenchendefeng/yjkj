package com.jiayi.platform.security.provider.controller;

import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.service.RoleService;
import com.jiayi.platform.security.core.vo.RoleRequest;
import com.jiayi.platform.security.core.vo.RoleSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping()
    @ResponseBody
    public JsonObject<?> addRole(@RequestBody RoleRequest roleRequest) {
        return new JsonObject<>(roleService.addRole(roleRequest));
    }
    @DeleteMapping("/{roleId}")
    @ResponseBody
    public JsonObject<?> deleteRole(@PathVariable Long roleId) {
        if(roleId == 1L) throw new ServiceException(500,"无法删除超级管理员角色！");
        else roleService.deleteRoleById(roleId);
        return new JsonObject<>("");
    }
    @PutMapping("/{roleId}")
    @ResponseBody
    public JsonObject<?> modifyRole(@PathVariable Long roleId, @RequestBody RoleRequest roleRequest) {
        roleService.modifyRole(roleId, roleRequest);
        return new JsonObject<>("");
    }

    @GetMapping()
    public JsonObject<?> searchRoles(RoleSearchVO roleSearchVO) {
        return new JsonObject<>(roleService.search(roleSearchVO));
    }
}
