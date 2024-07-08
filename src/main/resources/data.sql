-- restaurantsテーブル
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (1, 'SAMURAIのパスタ', 'restaurant_01.jpg', 'パスタ', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 2000, 2, '073-0145', '北海道砂川市西五条南X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (2, 'SAMURAIバーガー', 'restaurant_02.jpg', 'ハンバーガー', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 3000, 3, '030-0945', '青森県青森市桜川X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (3, '侍の休憩所', 'restaurant_03.jpg', '喫茶', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 2000, 4, '029-5618', '岩手県和賀郡西和賀町沢内両沢X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (4, 'la samurai', 'restaurant_04.jpg', 'フレンチ', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 2500, 5, '989-0555', '宮城県刈田郡七ヶ宿町滝ノ上X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (5, 'SAMURAI本舗', 'restaurant_05.jpg', '喫茶', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 10000, 6, '018-2661', '秋田県山本郡八峰町八森浜田X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (6, '食事処 SAMURAI', 'restaurant_06.jpg', '和食', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 6000, 2, '999-6708', '山形県酒田市泉興野X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (7, 'SAMURAI飯', 'restaurant_07.jpg', 'フレンチ', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 7000, 3, '969-5147', '福島県会津若松市大戸町芦牧X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (8, '割烹SAMURAI', 'restaurant_08.jpg', 'カフェ', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 8000, 4, '310-0021', '茨城県水戸市南町X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (9, 'SAMURAIレストラン', 'restaurant_09.jpg', '中華', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 9000, 5, '323-1101', '栃木県下都賀郡藤岡町大前X-XX-XX', '012-345-678');
INSERT IGNORE INTO restaurants (id, name, image_name, category, description, price, capacity, postal_code, address, phone_number) VALUES (10, '味自慢SAMURAI', 'restaurant_10.jpg', 'そば・うどん', '最寄り駅から徒歩10分。自然豊かで閑静な場所にあります。長期滞在も可能です。', 10000, 6, '370-0806', '群馬県高崎市上和田町X-XX-XX', '012-345-678');

-- rolesテーブル
INSERT IGNORE INTO roles (id, membership) VALUES (1, 'ROLE_GENERAL');
INSERT IGNORE INTO roles (id, membership) VALUES (2, 'ROLE_PREMIUM');
INSERT IGNORE INTO roles (id, membership) VALUES (3, 'ROLE_ADMIN');

-- usersテーブル
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (1, '侍 太郎', 'サムライ タロウ', '101-0022', '東京都千代田区神田練塀町300番地', '090-1234-5678', 'taro.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, 1);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (2, '侍 花子', 'サムライ ハナコ', '101-0022', '東京都千代田区神田練塀町300番地', '090-1234-5678', 'hanako.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 1, 1);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (3, '侍 義勝', 'サムライ ヨシカツ', '638-0644', '奈良県五條市西吉野町湯川X-XX-XX', '090-1234-5678', 'yoshikatsu.samurai@example.com', '$2a$10$2JNjTwZBwo7fprL2X4sv.OEKqxnVtsVQvuXDkI8xVGix.U3W5B7CO', 3, 1);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (4, '侍 幸美', 'サムライ サチミ', '342-0006', '埼玉県吉川市南広島X-XX-XX', '090-1234-5678', 'sachimi.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (5, '侍 雅', 'サムライ ミヤビ', '527-0209', '滋賀県東近江市佐目町X-XX-XX', '090-1234-5678', 'miyabi.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (6, '侍 正保', 'サムライ マサヤス', '989-1203', '宮城県柴田郡大河原町旭町X-XX-XX', '090-1234-5678', 'masayasu.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (7, '侍 真由美', 'サムライ マユミ', '951-8015', '新潟県新潟市松岡町X-XX-XX', '090-1234-5678', 'mayumi.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (8, '侍 安民', 'サムライ ヤスタミ', '241-0033', '神奈川県横浜市旭区今川町X-XX-XX', '090-1234-5678', 'yasutami.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (9, '侍 章緒', 'サムライ アキオ', '739-2103', '広島県東広島市高屋町宮領X-XX-XX', '090-1234-5678', 'akio.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (10, '侍 祐子', 'サムライ ユウコ', '601-0761', '京都府南丹市美山町高野X-XX-XX', '090-1234-5678', 'yuko.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (11, '侍 秋美', 'サムライ アキミ', '606-8235', '京都府京都市左京区田中西春菜町X-XX-XX', '090-1234-5678', 'akimi.samurai@example.com', 'password', 1, 0);
INSERT IGNORE INTO users (id, name, furigana, postal_code, address, phone_number, email, password, role_id,  enabled) VALUES (12, '侍 信平', 'サムライ シンペイ', '673-1324', '兵庫県加東市新定X-XX-XX', '090-1234-5678', 'shinpei.samurai@example.com', 'password', 1, 0);

-- reservationsテーブル
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (1, 1, 1, '2023-04-01', '10:00', 2, 4000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (2, 2, 1, '2023-04-02', '10:00', 3, 6000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (3, 3, 1, '2023-04-03', '10:00', 4, 8000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (4, 4, 1, '2023-04-04', '10:00', 5, 12500);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (5, 5, 1, '2023-04-05', '10:00', 6, 60000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (6, 6, 1, '2023-04-06', '10:00', 2, 12000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (7, 7, 1, '2023-04-07', '10:00', 3, 21000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (8, 8, 1, '2023-04-08', '10:00', 4, 32000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (9, 9, 1, '2023-04-09', '10:00', 5, 45000);
INSERT IGNORE INTO reservations (id, restaurant_id, user_id, checkin_date, checkin_time,  number_of_people, amount) VALUES (10, 10, 1, '2023-04-10', '10:00', 6, 60000);

-- reviewsテーブル
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (1,1,1,1,'非常に楽しめました！また来たいと思います。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (2,1,2,4,'美味しい料理でした。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (3,1,3,5,'思い出になりました。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (4,1,4,3,'次は友達と来たいです。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (5,1,5,4,'料理がおいしくて大満足でした。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (6,1,6,3,'とてもリフレッシュ出来ました。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (7,1,7,5,'とても落ち着く環境でした。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (8,1,8,3,'また来ます。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (9,1,9,5,'初めて利用しましたが、とても快適でした。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (10,1,10,4,'子供がとても楽しんでいました。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (11,1,11,3,'お腹いっぱいでとても満足です。');
INSERT IGNORE INTO reviews (id, restaurant_id, user_id, rating, review) VALUES (12,1,12,4,'初めて利用しました。良かったです。');

-- favoriteテーブル
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (1,1,1);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (2,1,2);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (3,1,3);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (4,1,4);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (5,1,5);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (6,1,6);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (7,1,7);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (8,1,8);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (9,1,9);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (10,1,10);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (11,1,11);
INSERT IGNORE INTO favorites (id, restaurant_id, user_id) VALUES (12,1,12);