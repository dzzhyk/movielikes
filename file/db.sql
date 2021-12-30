-- ----------------------------
-- 1、用户表
-- ----------------------------
drop table if exists sys_user;
create table sys_user
(
    user_id     bigint(20)  not null auto_increment comment '用户ID',
    user_name   varchar(30) not null comment '用户账号',
    email       varchar(50)  default '' comment '用户邮箱',
    password    varchar(100) default '' comment '密码',
    status      char(1)      default '0' comment '帐号状态（0正常 1停用）',
    del_flag    char(1)      default '0' comment '删除标志（0代表存在 2代表删除）',
    login_ip    varchar(128) default '' comment '最后登录IP',
    login_date  datetime comment '最后登录时间',
    create_by   varchar(64)  default '' comment '创建者',
    create_time datetime comment '创建时间',
    update_by   varchar(64)  default '' comment '更新者',
    update_time datetime comment '更新时间',
    remark      varchar(500) default null comment '备注',
    primary key (user_id)
) engine = innodb
  auto_increment = 283230 comment = '用户表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user
values (283229, 'admin', 'ry@163.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0',
        '127.0.0.1', sysdate(), 'admin', sysdate(), '', null, '管理员');

-- ----------------------------
-- 2、电影表
-- ----------------------------
drop table if exists sys_movie;
create table sys_movie
(
    movie_id      bigint(20)  not null comment '电影ID',
    title varchar(255) not null comment '电影名称',
#     title_chinese varchar(30) not null comment '中文电影名称',
    genres        varchar(255) default '' comment '电影分类',
#     `release`     varchar(30) comment '上映时间',
#     runtime       int comment '总时长(分钟)',
#     overview      text comment '简介',
#     poster_path   varchar(255) comment '海报uri',
    primary key (movie_id)
) engine = innodb
    comment = '电影表';


-- ----------------------------
-- 3、用户-电影关联表
-- 用户收藏
-- ----------------------------
drop table if exists sys_user_movie;
create table sys_user_movie
(
    user_id  bigint(20) not null comment '用户ID',
    movie_id bigint(20) not null comment '电影ID',
    primary key (user_id, movie_id)
) engine = innodb comment = '用户和电影关联表';