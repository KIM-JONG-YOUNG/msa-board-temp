create table if not exists `tb_post` (
	`id` BINARY(16) not null,
    `post_title` varchar(300) not null,
    `post_content` TEXT not null,
    `post_writer_id` BINARY(16) not null,
    `post_views` integer not null,
    `created_date_time` datetime not null,
    `updated_date_time` datetime not null,
    `state` integer not null,
    primary key (`id`)
);