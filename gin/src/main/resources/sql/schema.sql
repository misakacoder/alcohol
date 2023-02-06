create table `wine`
(
    `id`          bigint(20) primary key auto_increment comment 'id',
    `code_name`   varchar(32) not null comment '代号',
    `person_name` varchar(32) null comment '人名',
    `wine_name`   varchar(32) null comment '酒名',
    `create_at`   datetime    not null default now() comment '创建时间',
    unique index `udx_code_name` (`code_name`) using btree
) engine = innodb default charset = utf8mb4 collate = utf8mb4_bin comment '酒厂';