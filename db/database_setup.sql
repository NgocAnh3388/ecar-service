
-- XÓA CÁC BẢNG CŨ NẾU TỒN TẠI ĐỂ TRÁNH XUNG ĐỘT
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS maintenance_schedules CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;
DROP TABLE IF EXISTS maintenance_items CASCADE;


-- =====================================================================
-- BẢNG 1: app_user (LƯU THÔNG TIN NGƯỜI DÙNG)
-- =====================================================================
CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          sub VARCHAR(255) UNIQUE,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE
);

COMMENT ON TABLE app_user IS 'Lưu trữ thông tin cơ bản của người dùng.';
COMMENT ON COLUMN app_user.sub IS 'Định danh duy nhất từ nhà cung cấp OAuth2 (Google Subject).';
COMMENT ON COLUMN app_user.email IS 'Email của người dùng, dùng để định danh.';
COMMENT ON COLUMN app_user.active IS 'Trạng thái tài khoản (true = hoạt động, false = bị khóa/xóa mềm).';


-- =====================================================================
-- BẢNG 2: user_roles (LƯU VAI TRÒ CỦA NGƯỜI DÙNG - Mối quan hệ Nhiều-Nhiều)
-- =====================================================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role VARCHAR(255) NOT NULL,
                            PRIMARY KEY (user_id, role),
                            CONSTRAINT fk_user_roles_app_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

COMMENT ON TABLE user_roles IS 'Lưu các vai trò (quyền) được gán cho mỗi người dùng.';
COMMENT ON COLUMN user_roles.user_id IS 'Khóa ngoại trỏ tới ID của người dùng trong bảng app_user.';


-- =====================================================================
-- BẢNG 3: bookings (LƯU THÔNG TIN ĐẶT LỊCH BẢO DƯỠNG)
-- =====================================================================
CREATE TABLE bookings (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          customer_phone_number VARCHAR(255) NOT NULL,
                          license_plate VARCHAR(255) NOT NULL,
                          car_model VARCHAR(255),
                          vin_number VARCHAR(255),
                          service_center VARCHAR(255) NOT NULL,
                          appointment_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          service_advisor VARCHAR(255),
                          notes TEXT,
                          status VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          created_by VARCHAR(255) NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE,
                          updated_by VARCHAR(255),
                          CONSTRAINT fk_bookings_app_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);

COMMENT ON TABLE bookings IS 'Lưu trữ thông tin các lịch hẹn bảo dưỡng xe.';
COMMENT ON COLUMN bookings.user_id IS 'Khóa ngoại trỏ tới người dùng đã tạo lịch hẹn.';



-- =====================================================================
-- BẢNG 4: service_records (LƯU LỊCH SỬ DỊCH VỤ) -- Bảng Mới
-- =====================================================================
CREATE TABLE service_records (
                                 id BIGSERIAL PRIMARY KEY,
                                 booking_id BIGINT UNIQUE,
                                 license_plate VARCHAR(255) NOT NULL,
                                 kilometer_reading INTEGER NOT NULL,
                                 service_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                 created_by VARCHAR(255) NOT NULL,
                                 CONSTRAINT fk_records_bookings FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

COMMENT ON TABLE service_records IS 'Phiếu dịch vụ, lưu lại lịch sử bảo dưỡng đã hoàn thành.';


-- =====================================================================
-- BẢNG 5: service_record_details (LƯU CHI TIẾT CÁC HẠNG MỤC ĐÃ LÀM) -- Bảng Mới
-- =====================================================================
CREATE TABLE service_record_details (
                                        id BIGSERIAL PRIMARY KEY,
                                        service_record_id BIGINT NOT NULL,
                                        item_name VARCHAR(255) NOT NULL,
                                        action VARCHAR(255) NOT NULL,
                                        notes TEXT,
                                        CONSTRAINT fk_details_records FOREIGN KEY (service_record_id) REFERENCES service_records(id)
);

COMMENT ON TABLE service_record_details IS 'Chi tiết từng hạng mục đã thực hiện trong một lần bảo dưỡng.';


-- =====================================================================
-- BẢNG 6: maintenance_items (LƯU DANH MỤC CÁC HẠNG MỤC BẢO DƯỠNG)
-- =====================================================================
CREATE TABLE maintenance_items (
                                   id BIGSERIAL PRIMARY KEY,
                                   name VARCHAR(255) NOT NULL UNIQUE,
                                   category VARCHAR(255) NOT NULL
);

COMMENT ON TABLE maintenance_items IS 'Danh sách tất cả các hạng mục bảo dưỡng có thể có.';
-- =====================================================================
-- BẢNG 7: maintenance_schedules (LƯU LỊCH TRÌNH BẢO DƯỠNG CHI TIẾT)
-- =====================================================================
CREATE TABLE maintenance_schedules (
                                       id BIGSERIAL PRIMARY KEY,
                                       item_id BIGINT NOT NULL,
                                       kilometer_mark INTEGER NOT NULL,
                                       action VARCHAR(255) NOT NULL,
                                       CONSTRAINT fk_schedules_items FOREIGN KEY (item_id) REFERENCES maintenance_items(id)
);

COMMENT ON TABLE maintenance_schedules IS 'Lịch trình chi tiết: hạng mục nào, làm gì, tại mốc km nào.';


-- =====================================================================
-- CHÈN DỮ LIỆU MẪU CHO CÁC BẢNG BẢO DƯỠNG
-- DataSeeder.java
-- =====================================================================

-- Chèn dữ liệu cho bảng maintenance_items
INSERT INTO maintenance_items (id, name, category) VALUES
                                                       (1, 'Lọc gió điều hòa', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (2, 'Dầu phanh', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (3, 'Bảo dưỡng hệ thống điều hòa', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (4, 'Pin chìa khóa điều khiển', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (5, 'Pin bộ T-Box', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (6, 'Nước làm mát cho Pin/ động cơ điện', 'HẠNG MỤC BẢO DƯỠNG'),
                                                       (7, 'Lốp (áp suất, độ mòn, đảo và cân bằng lốp)', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (8, 'Má phanh và đĩa phanh', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (9, 'Đường ống, đầu nối hệ thống phanh', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (10, 'Bộ dẫn động (động cơ điện và hộp số)', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (11, 'Hệ thống treo', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (12, 'Trục truyền động', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (13, 'Khớp cầu', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (14, 'Thước lái và khớp nối cầu', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (15, 'Đường ống làm mát', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (16, 'Pin', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (17, 'Dây cáp của hệ thống điện áp cao', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (18, 'Cổng sạc', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (19, 'Ắc quy 12V', 'HẠNG MỤC BẢO DƯỠNG CHUNG'),
                                                       (20, 'Gạt nước rửa kính / Nước rửa kính', 'HẠNG MỤC BẢO DƯỠNG CHUNG');

-- Chèn dữ liệu cho bảng maintenance_schedules (được sinh tự động theo logic)
DO $$
    DECLARE
km_marks INT[] := ARRAY[12000, 24000, 36000, 48000, 60000, 72000, 84000, 96000, 108000, 120000, 132000, 144000, 156000, 168000, 180000, 192000, 204000];
        km INT;
BEGIN
        FOREACH km IN ARRAY km_marks
            LOOP
                -- Hạng mục luôn REPLACE
                INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (1, km, 'REPLACE');

                -- Dầu phanh
                IF km % 24000 = 0 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (2, km, 'REPLACE');
                ELSIF km % 12000 = 0 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (2, km, 'INSPECT');
END IF;

                -- Bảo dưỡng điều hòa
                IF km % 60000 = 0 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (3, km, 'REPLACE');
END IF;

                -- Pin chìa khóa
                IF km % 24000 = 0 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (4, km, 'REPLACE');
END IF;

                -- Pin T-Box
                IF km % 72000 = 0 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (5, km, 'REPLACE');
END IF;

                -- Nước làm mát
                IF km = 120000 THEN
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (6, km, 'REPLACE');
ELSE
                    INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (6, km, 'INSPECT');
END IF;

                -- Các hạng mục chung luôn INSPECT
FOR i IN 7..20 LOOP
                        INSERT INTO maintenance_schedules (item_id, kilometer_mark, action) VALUES (i, km, 'INSPECT');
END LOOP;
END LOOP;
END $$;

-- Kết thúc script
SELECT 'Database script executed successfully.';
