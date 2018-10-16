/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50637
 Source Host           : localhost:3306
 Source Schema         : easy_data

 Target Server Type    : MySQL
 Target Server Version : 50637
 File Encoding         : 65001

 Date: 16/10/2018 09:10:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'UUID',
  `url` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '资源[url]',
  `roleid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '介绍说明',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('373b1ce8acb74fc1a8d35fa9826a859c', 'user_url', '52643de213b349ef803e83d5e68c13c2', NULL);
INSERT INTO `t_permission` VALUES ('51f4576fa233494085c0e1e686396011', 'admin_url', '1d639bc32a4849498f134556e6f680b7', '普通管理员');
INSERT INTO `t_permission` VALUES ('96ff018d640b475cb8a39e56e3621431', 'super_url', 'a71afeec9a7d41b38ab52a8cf261a74c', '超级管理员');
INSERT INTO `t_permission` VALUES ('e6ff01896ytb475cb7139e56e362tbvc', 'admin_url', 'a71afeec9a7d41b38ab52a8cf261a74c', '超管访问普管');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'UUID',
  `role` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1d639bc32a4849498f134556e6f680b7', '管理员', '可以查看普通用户和部分数据');
INSERT INTO `t_role` VALUES ('52643de213b349ef803e83d5e68c13c2', '普通用户', '仅能查看属于自己的内容');
INSERT INTO `t_role` VALUES ('a71afeec9a7d41b38ab52a8cf261a74c', '超级管理员', '超级管理员用户，可以查询所有数据');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'uuid',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `age` int(4) NULL DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('578944c8efee49b8a2bfdf57d54b2a9b', 'admin', '000000', '管理员', 16);
INSERT INTO `t_user` VALUES ('752695abfc1d451981bbf5dae60663f8', 'user', '000000', '普通用户', 22);
INSERT INTO `t_user` VALUES ('f1bb55afaf6448d9956865b93dd54c3c', 'superadmin', '000000', '超级管理员', 18);

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'UUID',
  `userid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
  `roleid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('6a571ad7c4fc4fbba4b6fbb640d45fc4', 'f1bb55afaf6448d9956865b93dd54c3c', 'a71afeec9a7d41b38ab52a8cf261a74c');
INSERT INTO `t_user_role` VALUES ('839c827a76144eca97a2f204c11deb4d', '752695abfc1d451981bbf5dae60663f8', '52643de213b349ef803e83d5e68c13c2');
INSERT INTO `t_user_role` VALUES ('f0de4a2485e1420abfcea7eb20d9a54b', '578944c8efee49b8a2bfdf57d54b2a9b', '1d639bc32a4849498f134556e6f680b7');

-- ----------------------------
-- Table structure for wx_access_token
-- ----------------------------
DROP TABLE IF EXISTS `wx_access_token`;
CREATE TABLE `wx_access_token`  (
  `uuid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'uuid',
  `access_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'access_token',
  `expires_in` int(6) NOT NULL COMMENT 'expires_in',
  `type` varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'T' COMMENT 'WXMP\\WXAPP',
  `state` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'T' COMMENT 'T\\F',
  `sj` datetime(0) NOT NULL COMMENT '时间',
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
