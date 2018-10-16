package com.iiover.common.user.controller;


import com.iiover.common.shiro.service.ShiroService;
import com.iiover.common.user.entity.UserEntity;
import com.iiover.common.user.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/user", method = { RequestMethod.GET, RequestMethod.POST })
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private ShiroService shiroService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    private String main(HttpServletRequest request) {

        System.out.println("===========跳转首页==================");

        return "/user/login.jsp";
    }

    @RequestMapping(value = "/login")
    public String Login(String username, String mm,HttpSession session, Model model){
        String password =mm;
        System.out.println("0000000000000000000000000000000000000000000000000");
        if(username==null){
            model.addAttribute("message", "账号不为空");
            return "/user/login.jsp";
        }


        //主体,当前状态为没有认证的状态“未认证”
        Subject subject = SecurityUtils.getSubject();
        // 登录后存放进shiro token
        UsernamePasswordToken token=new UsernamePasswordToken(username,password);
        UserEntity user;
        //登录方法（认证是否通过）
        //使用subject调用securityManager,安全管理器调用Realm
        try {
            //利用异常操作
            //需要开始调用到Realm中
            System.out.println("========================================");
            System.out.println("1、进入认证方法");
            subject.login(token);
            user = (UserEntity)subject.getPrincipal();
            session.setAttribute("user",subject);
            model.addAttribute("message", "登录完成");
            System.out.println("登录完成");
        } catch (IncorrectCredentialsException e) {
            model.addAttribute("message", "账号密码不正确");
            System.out.println("用户名密码错误!");
            log.info("密码不正确UUUUUUUUU");
            return "user/login.jsp";
        }

        return "/user/main.jsp";
    }

    @RequestMapping("/check")
    public String check(HttpSession session){

        Subject subject=(Subject)session.getAttribute("user");

        UserEntity user=(UserEntity)subject.getPrincipal();
        System.out.println(user.toString());
        return "permission";
    }

    @RequestMapping("/superControl")
    public String superControl(HttpSession session){

        return "/user/superControl.jsp";
    }

    @RequestMapping("/superList")
    public String superList(){

        return "/user/superList.jsp";
    }

    @RequestMapping("/adminControl")
    public String adminControl(){

        return "/user/adminControl.jsp";
    }


    @RequestMapping("/adminList")
    public String adminList(){
        return "/user/adminList.jsp";
    }

    @RequestMapping("/userControl")
    public String userControl(){
        return "/user/userControl.jsp";
    }

    @RequestMapping("/userInfo")
    public String userInfo(){
        return "/user/userInfo.jsp";
    }
    @RequestMapping("/nopermission")
    public String nopermission(){
        return "/user/nopermission.jsp";
    }


}
