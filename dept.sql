/*
 Navicat Premium Data Transfer

 Source Server         : mysql57
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost:3306
 Source Schema         : db01

 Target Server Type    : MySQL
 Target Server Version : 50721
 File Encoding         : 65001

 Date: 06/02/2020 20:35:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dept
-- ----------------------------
DROP TABLE IF EXISTS `dept`;
CREATE TABLE `dept`  (
  `deptno` bigint(20) NOT NULL AUTO_INCREMENT,
  `dname` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `db_source` varchar(60) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`deptno`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dept
-- ----------------------------
INSERT INTO `dept` VALUES (1, '开发部', 'db01');
INSERT INTO `dept` VALUES (2, '人事部', 'db01');
INSERT INTO `dept` VALUES (3, '财务部', 'db01');
INSERT INTO `dept` VALUES (4, '市场部', 'db01');
INSERT INTO `dept` VALUES (5, '运维部', 'db01');
INSERT INTO `dept` VALUES (14, '\"111\"', 'db01');
INSERT INTO `dept` VALUES (15, '徐庆撒比', 'db01');

SET FOREIGN_KEY_CHECKS = 1;
