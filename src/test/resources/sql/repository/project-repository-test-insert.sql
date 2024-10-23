INSERT INTO "user" ("user_id", "file_url", "nickname")
VALUES (1, 'user_image.png', 'test_user');

insert into "project" ("project_id", "title", "file_url", "deadline", "description", "created_at", "important_question", "position", "recruitment", "soft_skill", "tech_stack", "created_id", "favorite_count", "view_count")
values (1, '프로젝트 제목1', 'project_image.png', '2024-09-20', '프로젝트 설명1', '2024-08-10T00:00:00Z', '주 1회 회의', 'frontend', 'OPEN', '시간 관리', 'vue, typescript', 1, 0, 0);

insert into "project" ("project_id", "title", "file_url", "deadline", "description", "created_at", "important_question", "position", "recruitment", "soft_skill", "tech_stack", "created_id", "favorite_count", "view_count")
values (2, '프로젝트 제목2', 'project_image.png', '2024-09-20', '프로젝트 설명2', '2024-08-10T00:00:00Z', '주 1회 회의', 'backend', 'OPEN', '시간 관리', 'java, spring', 1, 1, 0);

insert into "recruit" ("recruit_id", "current_count", "position", "target_count", "project_id")
values (1, 0, 'frontend', 1, 1);

insert into "recruit" ("recruit_id", "current_count", "position", "target_count", "project_id")
values (2, 1, 'backend', 2, 2);

insert into "project_favorite" ("project_id", "user_id") values (1,1);