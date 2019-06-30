package com.jiayi.platform.security.provider.controller;

import com.jiayi.platform.common.exception.AuthException;
import com.jiayi.platform.common.exception.ServiceException;
import com.jiayi.platform.common.vo.ModifyRemark;
import com.jiayi.platform.security.core.dto.JsonObject;
import com.jiayi.platform.security.core.entity.User;
import com.jiayi.platform.security.core.service.UserService;
import com.jiayi.platform.security.core.util.FileUtil;
import com.jiayi.platform.security.core.util.JWTUtil;
import com.jiayi.platform.security.core.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Value("${platform-source.upload-path}")
    private String uploadPath;

    @Value("${platform-source.file-portrait-prefix}")
    private String remotePrefix;

    @GetMapping(value = "/user/logout/{id}")
    @ApiOperation(value = "退出登录")
    @ResponseBody
    public JsonObject<?> logout(@PathVariable long id) {
        userService.logout(id);
        return new JsonObject<>("");
    }

    @GetMapping(value = "/user/getuserinfo/{id}")
    @ApiOperation(value = "获取用户信息")
    public JsonObject<?> getUserInfo(@PathVariable long id) {
        User user = userService.getUserInfo(id);
        return new JsonObject<>(new UserDetailVO(user));
    }

    @PostMapping(value = "/users/save")
    @ApiOperation(value = "保存个人中心信息")
    public JsonObject<?> saveUserInfo(@RequestBody PersonalInfo info) {
        String token = request.getHeader("Authorization");
        Long userId = Long.valueOf(JWTUtil.getUserId(token));
        userService.saveUserInfo(userId, info);
        return new JsonObject<>("");
    }

    @GetMapping(value = "/users/search")
    @ApiOperation(value = "查询当前登录用户部门及子部门的用户列表")
    public JsonObject<?> search(UserSearchVo userSearchVo) {
        String token = request.getHeader("Authorization");
        Long userId = Long.valueOf(JWTUtil.getUserId(token));
        return new JsonObject<>(userService.search(userId, userSearchVo));
    }

    @PostMapping(value = "/users")
    @ApiOperation(value = "添加用户")
    public JsonObject<?> add(@RequestBody @Valid UserRequest userRequest) {
        User user = userService.add(userRequest);
        user.setPassword(null);
        return new JsonObject<>(user);
    }

    @PutMapping(value = "/users/{id}")
    @ApiOperation(value = "修改用户信息")
    public JsonObject<?> modify(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return new JsonObject<>(userService.modify(id, userRequest));
    }

    @DeleteMapping(value = "/users/{id}")
    @ApiOperation(value = "删除用户")
    public JsonObject<?> delete(@PathVariable Long id) {
        if(id == 1L) throw new ServiceException(500,"无法删除Admin用户！");
        else userService.delete(id);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/users/modifypwd")
    @ApiOperation(value = "修改登录用户密码")
    public JsonObject<?> modifypwd(@RequestBody @Valid PasswordRequest passwordRequest) {
        String token = request.getHeader("Authorization");
        Long userId = Long.valueOf(JWTUtil.getUserId(token));
        userService.modifypwd(userId, passwordRequest);
        return new JsonObject<>("");
    }

    @PostMapping(value = "/users/upload")
    @ApiOperation(value = "上传头像，保存生效")
    public JsonObject<?> uploadPortrait(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return new JsonObject<>(upload(file, null));
    }

    @PostMapping(value = "/users/updateportrait/{id}")
    @ApiOperation(value = "修改用户头像，立即生效")
    public JsonObject<?> updatePortrait(@RequestParam("file") MultipartFile file, HttpServletRequest request, @PathVariable Long id) {
        return new JsonObject<>(upload(file, id));
    }

    @PutMapping("/user/updateremark")
    @ApiOperation(value = "修改用户管理备注")
    public JsonObject<?> modifyRemark(@RequestBody ModifyRemark modifyRemark) {
        userService.editDescription(modifyRemark.getRemark(), modifyRemark.getId());
        return new JsonObject<>("");
    }

    private Map<String, String> upload(MultipartFile file, Long id) {
        String fileName = file.getOriginalFilename();
        String normalFileName = FileUtil.generateNormalFileName(fileName);
        try {
            FileUtil.writeFileToPath(file.getBytes(), uploadPath, normalFileName);
        } catch (Exception e) {
            throw new AuthException("上传头像失败", e);
        }
        if (id != null) userService.updatePortrait(uploadPath, normalFileName, id);
        Map<String, String> map = new HashMap<>();
        map.put("portraitUrl", normalFileName);
        map.put("path", remotePrefix);
        return map;
    }
}
