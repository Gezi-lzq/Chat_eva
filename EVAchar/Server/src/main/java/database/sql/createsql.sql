create database im_char;
use im_char;

CREATE TABLE `im_user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(9) NOT NULL COMMENT '用户id',
  `sex` int(1) NOT NULL DEFAULT '0' COMMENT '1男2女0未知',
  `nike_name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `phone_number` varchar(11) NOT NULL COMMENT '手机号码',
  `email` varchar(64) NOT NULL COMMENT 'email',
  `avatar` varchar(1024) NOT NULL COMMENT '自定义用户头像',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '在线状态',
  `sign_info` varchar(128) NOT NULL COMMENT '个性签名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='即时通讯-用户';


CREATE TABLE `im_relation_ship` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_Id` int(9) NOT NULL COMMENT '用户A的id',
  `friend_Id` int(9) NOT NULL COMMENT '用户B的id',
  `status` int(1) DEFAULT '0' COMMENT '用户:0-正常, 1-用户A删除,群组:0-正常, 1-被删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='即时通讯-好友关系';


CREATE TABLE `im_message` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `relateId` int(9) NOT NULL COMMENT '用户的关系id',
  `fromId` int(9) NOT NULL COMMENT '发送用户的id',
  `toId` int(9) NOT NULL COMMENT '接收用户的id',
  `content` varchar(4096) NOT NULL COMMENT '消息内容',
  `type` int(2) NOT NULL DEFAULT '1' COMMENT '消息类型',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '0正常 1被删除',
  `message_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `fromId` (`fromId`),
  KEY `toid` (`toId`),
  KEY `relate_id` (`relateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='即时通讯-消息'
