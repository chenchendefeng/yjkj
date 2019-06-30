package com.jiayi.platform.security.provider.controller;

import com.jiayi.platform.basic.service.PlaceService;
import com.jiayi.platform.common.util.ThreadPoolUtil;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.entity.Department;
import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.util.JWTUtil;
import com.jiayi.platform.security.core.dto.DepartDir;
import com.jiayi.platform.security.core.service.DepartmentService;
import com.jiayi.platform.security.core.service.UserService;
import com.jiayi.platform.security.core.vo.DepartmentRequest;
import com.jiayi.platform.security.provider.client.IndexClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/departments")
@Slf4j
public class DepartmentController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private IndexClient indexClient;

    @GetMapping(value = "/staff/tree/{departId}")
    @ResponseBody
    public JsonObject<?> departmentStaffTree(@PathVariable Integer departId) {
        return new JsonObject<>(departmentService.getDepartmentStaffTree(departId));
    }

    @GetMapping(value = "/staff/tree")
    @ResponseBody
    @ApiOperation(value = "根据登录用户获取其部门及子部门人员目录")
    public JsonObject<?> departmentStaffTree() {
        String token = request.getHeader("Authorization");
        Long userId = Long.valueOf(JWTUtil.getUserId(token));
        User user = userService.getUserInfo(userId);
        List<DepartDir> list = null;
        if (user.getDepartmentId() != null)
            list = departmentService.getDepartmentStaffTree(user.getDepartmentId().getId());
        return new JsonObject<>(list);
    }

    @GetMapping(value = "/tree/{departId}")
    @ResponseBody
    public JsonObject<?> departmentTree(@PathVariable Integer departId) {
        return new JsonObject<>(departmentService.getDepartmentTree(departId));
    }

    @GetMapping(value = "/tree")
    @ApiOperation(value = "根据登录用户获取其部门及子部门目录")
    public JsonObject<?> departmentTree() {
        String token = request.getHeader("Authorization");
        Long userId = Long.valueOf(JWTUtil.getUserId(token));
        User user = userService.getUserInfo(userId);
        List<DepartDir> list = null;
        if (user.getDepartmentId() != null) {
            int departmentId = 0;
            if (userId != 1L) departmentId = user.getDepartmentId().getId();
            list = departmentService.getDepartmentTree(departmentId);
        }

        return new JsonObject<>(list);
    }

    @PostMapping
    public JsonObject<?> add(@RequestBody @Valid DepartmentRequest request) {
        Department department = departmentService.add(request.toEntity());
        ThreadPoolUtil.getInstance().submit(()->flushDepartment());
        return new JsonObject<>(department);
    }

    @PutMapping("/{id}")
    public JsonObject<?> modify(@PathVariable Integer id, @RequestBody DepartmentRequest request) {
        Department department = departmentService.modify(id, request);
        ThreadPoolUtil.getInstance().submit(()->flushDepartment());
        return new JsonObject<>(department);
    }

    @DeleteMapping(value = "/{id}")
    public JsonObject<?> delete(@PathVariable Integer id) {
        departmentService.delete(id, placeService.isHavePlace(id));
        ThreadPoolUtil.getInstance().submit(()->flushDepartment());
        return new JsonObject<>("");
    }

    private void flushDepartment(){
        try {
            JsonObject jsonObject = indexClient.flushDepartment();
            log.info("flush department " + jsonObject.getMessage());
        } catch (Exception e) {
            log.error("flush department error", e);
        }
    }
}
