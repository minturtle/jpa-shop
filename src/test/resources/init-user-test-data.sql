INSERT INTO `users` (
    `version`,
    `created_at`,
    `modified_at`,
    `user_id`,
    `address`,
    `detailed_address`,
    `email`,
    `google_uid`,
    `kakao_uid`,
    `name`,
    `password`,
    `profile_image_url`,
    `user_uid`,
    `username`
) VALUES (
             1,
             '2024-03-31 12:00:00.000000',
             '2024-03-31 12:00:00.000000',
             1,
             '서울시 강남구',
             '역삼동 123-45',
             'user@example.com',
             NULL,
             NULL,
             '홍길동',
             'Jf2rTvFrXb8QTZfoz3szoVM0jZIS2xrXmVdBL05IL5t77TYgFT/4b/DAqqxd2+6lK/jdxkjWF3sc0Nm5VwRgSulvUEuuR774o2C5z08FjVdgvBgUWrmI6tPdPK7YMAWOPjRXet/qL5rjgjYGo16fOpDZAEStdsK9G9dhg3iJ5Jtoh2Cngq3uo6t2Souc0jt5i2D1qolHVG+bTQmIbgWSgKFBq+5yWm0bHGaCeFJMpAaN8izjjlIl6cVYkeKKlEdI',
             'http://example.com/profiles/hong.png',
             'user-001',
             'honggildong'
         ), (
             1,
             '2024-03-31 12:00:00.000000',
             '2024-03-31 12:00:00.000000',
             2,
             '경상북도 구미시',
             '대학로 1',
             'user2@email.com',
             NULL,
             '123214214',
             '김철수',
             NULL,
             'http://example.com/profiles/kim.png',
             NULL,
             'user-002',
             NULL
         ),(
            1,
            '2024-03-31 12:00:00.000000',
            '2024-03-31 12:00:00.000000',
            3,
            '대구광역시',
            '달서구 123',
            'user3@email.com',
            '123214214',
            NULL,
            '김영희',
            NULL,
            'http://example.com/profiles/young.png',
            NULL,
            'user-003',
            NULL
        );

INSERT INTO accounts (account_id, account_uid, name, user_id, balance, version)
VALUES (1, 'account-001', '내 계좌', 1, 100000, 1);

INSERT INTO accounts (account_id,account_uid, name, user_id, balance, version)
VALUES (2, 'account-002', '내 계좌2', 1, 500, 1);