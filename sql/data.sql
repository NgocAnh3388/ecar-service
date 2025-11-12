-- Kích hoạt extension để tìm kiếm không dấu (nếu chưa có)
CREATE EXTENSION IF NOT EXISTS unaccent;

-- RUN THIS COMMAND FIRST TO CLEAR OLD DATA IF NEEDED
TRUNCATE TABLE
    public.app_user,
    public.user_roles,
    public.car_model,
    public.center,
    public.service,
    public.maintenance_milestone,
    public.vehicles,
    public.subscription_info,
    public.payment_history,
    public.maintenance_history,
    public.maintenance_item,
    public.maintenance_schedule,
    public.spare_part,
    public.service_records,
    public.service_record_details,
    public.bookings,
    public.expense,
    public.service_part_usage,
    public.inventory,
    public.service_spare_parts_map,
    public.maintenance_item_parts
    RESTART IDENTITY CASCADE;

-- =================================================================================
-- STEP 1: INSERT DATA FOR BASE TABLES (KHÔNG CÓ KHÓA NGOẠI)
-- =================================================================================

-- =================================================================================
-- app_user
-- =================================================================================
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, center_id, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                                     (true, 'lengochan090105@gmail.com', '117568473599883678495', 'Lê Ngọc Hân', '0373587001', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'dinhthingocanh0308@gmail.com', '105167307593551204911', 'Ngọc Anh', '0373587008', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'namhoai020505@gmail.com', '118080103497063505858', 'Nam Hoài', '0373587009', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'boyhayhaha12345@gmail.com', '10414838788924653426', 'Nguyễn Đăng Phú', '0373587010', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'kassassinrk@gmail.com', '101853864144089879263', 'Nguyễn Dũng', '0373587011', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, center_id, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                                     -- Sample Staff Accounts (user01x)
                                                                                                                                     -- Center 1: ECar Binh Duong
                                                                                                                                     (true, 'staffrole001@gmail.com', '103635268146202778075', 'Staff Role', '0373587002', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'kaitetsuya91@gmail.com', '101969093178465016620', 'Luân Hoàng', '0373587005', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff010@example.com', 'sub-010', 'Võ Đức Anh', '0912345010', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff011@example.com', 'sub-011', 'Nguyễn Quốc Bảo', '0912345011', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     -- Center 2: ECar Thu Duc
                                                                                                                                     (true, 'staff012@example.com', 'sub-012', 'Huỳnh Vũ Bằng', '0912345012', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff013@example.com', 'sub-013', 'Nguyễn Ngọc Minh Châu', '0912345013', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff014@example.com', 'sub-014', 'Nguyễn Ngọc Trân Châu', '0912345014', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff015@example.com', 'sub-015', 'Phạm Huy Cường', '0912345015', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     -- Center 3: ECar District 1
                                                                                                                                     (true, 'staff016@example.com', 'sub-016', 'Lê Thị Ngọc Hân', '0912345016', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff017@example.com', 'sub-017', 'Nguyễn Thị Thanh Hân', '0912345017', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff018@example.com', 'sub-018', 'Trần Công Hiệp', '0912345018', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff019@example.com', 'sub-019', 'Phan Sơn Hoàng', '0912345019', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, center_id, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                                     -- Sample Technician Accounts (user02x)
                                                                                                                                     -- Center 1: ECar Binh Duong
                                                                                                                                     (true, 'technicianrole01@gmail.com', '112040040855698268458', 'Technician Role', '0373587003', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'shadehygge@gmail.com', '115145529639894629785', 'Hygge Shade', '0373587006', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'staff020@example.com', 'sub-020', 'Đặng Nguyễn Trung Huy', '0912345020',1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech021@example.com', 'sub-021', 'Nguyễn Phan Minh Hưng', '0912345021', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech022@example.com', 'sub-022', 'Lê Quang Khải', '0912345022', 1, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     -- Center 2: ECar Thu Duc
                                                                                                                                     (true, 'tech023@example.com', 'sub-023', 'Lê Nguyên Khan', '0912345023', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech024@example.com', 'sub-024', 'Dương Hồng Khang', '0912345024', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech025@example.com', 'sub-025', 'Nguyễn Đăng Khoa', '0912345025', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech026@example.com', 'sub-026', 'Nguyễn Ngọc Kiều My', '0912345026', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech027@example.com', 'sub-027', 'Phan Cao Trọng Nghĩa', '0912345027', 2, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     -- Center 3: ECar District 1
                                                                                                                                     (true, 'tech028@example.com', 'sub-028', 'Trần Thúy Ngọc', '0912345028', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech029@example.com', 'sub-029', 'Lê Hoàng Uyển Nhi', '0912345029', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech030@example.com', 'sub-030', 'Châu Hiệp Phát', '0912345030', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech031@example.com', 'sub-031', 'Nguyễn Hồng Phúc', '0912345031', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'tech032@example.com', 'sub-032', 'Phan Tâm Phương', '0912345032', 3, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, center_id, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                                     -- Sample Customer Accounts (user03x, user04x)
                                                                                                                                     (true, 'customerrole01@gmail.com', '110833741228031693365', 'Customer Role', '0373587004', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'wendyhimekawa@gmail.com', '115830350857850462621', 'Alvarez Wendy', '0373587007', null, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer033@example.com', 'sub-033', 'Võ Hồng Phương', '0912345033', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer034@example.com', 'sub-034', 'Lê Nguyễn Ngọc Quý', '0912345034', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer035@example.com', 'sub-035', 'Nguyễn Kim Quyên', '0912345035', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer036@example.com', 'sub-036', 'Ngô Thị Mỹ Quỳnh', '0912345036', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer037@example.com', 'sub-037', 'Huỳnh Trúc Tâm', '0912345037', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer038@example.com', 'sub-038', 'Trần Hưng Thịnh', '0912345038', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer039@example.com', 'sub-039', 'Bùi Ngọc Minh Thư', '0912345039', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer040@example.com', 'sub-040', 'Đoàn Thị Kiều Thư', '0912345040', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer041@example.com', 'sub-041', 'Lê Minh Thư', '0912345041', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, center_id, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                                     -- Sample Customer Accounts (user03x, user04x)
                                                                                                                                     (true, 'customer042@example.com', 'sub-042', 'Nguyễn Lý Mỹ Tiên', '0912345042', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer043@example.com', 'sub-043', 'Nguyễn Thị Cẩm Tiên', '0912345043', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer044@example.com', 'sub-044', 'Nguyễn Thị Cẩm Tiên', '0912345044', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer045@example.com', 'sub-045', 'Trần Ngọc Quế Trang', '0912345045', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer046@example.com', 'sub-046', 'Võ Thị Thùy Trinh', '0912345046', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer047@example.com', 'sub-047', 'Huỳnh Thị Thanh Trúc', '0912345047', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer048@example.com', 'sub-048', 'Nguyễn Thanh Trúc', '0912345048', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer049@example.com', 'sub-049', 'Phạm Thanh Trường', '0912345049', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer050@example.com', 'sub-050', 'Trần Cẩm Tú', '0912345050', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer051@example.com', 'sub-051', 'Đỗ Trần Tường Vy', '0912345051', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                                     (true, 'customer052@example.com', 'sub-052', 'Kiều Lê Thảo Vy', '0912345052', null,'2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');


-- =================================================================================
-- car_model
-- =================================================================================
INSERT INTO public.car_model (car_name, car_type, created_by, created_at, active, updated_at, updated_by) VALUES
                                                                                                              ('VF3', 'Mini SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system'),
                                                                                                              ('VF5', 'A-Segment SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system'),
                                                                                                              ('VF6', 'B-Segment SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system'),
                                                                                                              ('VF7', 'C-Segment SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system'),
                                                                                                              ('VF8', 'D-Segment SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system'),
                                                                                                              ('VF9', 'E-Segment SUV', 'system', '2025-10-11 08:34:17', true, '2025-10-11 08:34:17', 'system');

-- =================================================================================
-- center
-- =================================================================================
INSERT INTO public.center (center_name, phone_no, address, created_by, created_at, updated_at, updated_by) VALUES
                                                                                                               ('ECar Binh Duong', '0987654321', '1 Binh Duong Avenue, Thu Dau Mot City', 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system'),
                                                                                                               ('ECar Thu Duc', '0987654322', '1 Vo Van Ngan, Thu Duc City, HCMC', 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system'),
                                                                                                               ('ECar District 1', '0987654323', '123 Le Loi, District 1, HCMC', 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');

-- =================================================================================
-- service
-- =================================================================================
INSERT INTO public.service (id, service_type, service_name, category, created_by, created_at, updated_at, updated_by) VALUES
                                                                                                                          -- Nhóm 1: Dịch vụ bảo dưỡng định kỳ (service_type = 'M')
                                                                                                                          (1, 'M', 'Cabin Air Filter', 'replace', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (2, 'M', 'Brake Fluid', 'replace', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (3, 'M', 'Air Conditioning System', 'replace', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (4, 'M', 'T-BOX Battery', 'replace', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (5, 'M', 'Coolant for High-Voltage Battery/Electric Motor', 'replace', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (6, 'M', 'Tires (tread wear, pressure, rotation, and balancing)', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (7, 'M', 'Wheels (damage, deformation, and cracks)', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (8, 'M', 'Brake Pads and Discs', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (9, 'M', 'Brake Fluid Lines and Connections', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (10, 'M', 'Electric Powertrain (motor-gearbox)', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (11, 'M', 'Axle and Suspension System', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (12, 'M', 'Drive Shaft and Dust Boots', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (13, 'M', 'Suspension Ball Joints', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (14, 'M', 'Steering Gear and Ball Joints', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (15, 'M', 'Wiper Blades / Windshield Washer Fluid', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (16, 'M', 'Coolant Hoses Inspection', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (17, 'M', 'High-Voltage Battery', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (18, 'M', 'High-Voltage Cables and Related Wires', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (19, 'M', 'Charging Port', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (20, 'M', '12V Battery', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (21, 'M', 'Check for Underbody Corrosion', 'general', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          -- Nhóm 2: Dịch vụ sửa chữa (service_type = 'F')
                                                                                                                          (26, 'F', 'Brake Pad Replacement', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (27, 'F', 'ABS Sensor Replacement', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (28, 'F', 'Brake Disc Repair/Replacement', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (29, 'F', 'Parking Brake Service', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (30, 'F', 'Brake Caliper Service', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (31, 'F', 'Brake Line Leak Inspection/Repair', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (32, 'F', 'Other Brake Services', 'brake', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (33, 'F', 'Steering Wheel Service', 'steering', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (34, 'F', 'Power Steering Service', 'steering', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (35, 'F', 'Steering Rack Service', 'steering', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (36, 'F', 'Steering Column Service', 'steering', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (37, 'F', 'Power Steering Hose Service', 'steering', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (38, 'F', 'Horn Repair', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (39, 'F', 'Battery Service/Replacement', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (40, 'F', 'AC Evaporator Temperature Sensor', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (41, 'F', 'Condenser Service', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (42, 'F', 'High-Pressure Hose Service', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (43, 'F', 'AC Vent Repair', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (44, 'F', 'Blower Motor Repair', 'electric', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (45, 'F', 'Radiator Service', 'cooling', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (46, 'F', 'Radiator Fan Motor', 'cooling', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (47, 'F', 'High-Pressure Coolant Hose', 'cooling', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (48, 'F', 'Thermostat Service', 'cooling', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (49, 'F', 'Tire Puncture Repair', 'suspension', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (50, 'F', 'Tire/Wheel Replacement', 'suspension', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (51, 'F', 'Spare Tire Replacement', 'suspension', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (52, 'F', 'Tire Pressure Monitoring System (TPMS) Sensor', 'suspension', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (53, 'F', 'Shock Absorber Replacement', 'suspension', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (54, 'F', 'Interior Plastic Trim Replacement', 'interior', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (55, 'F', 'Error Light Diagnosis and Software Update', 'other', 'system', '2025-10-11 08:34:17.121222', '2025-10-11 08:34:17.121222', 'system');


-- =================================================================================
-- STEP 2: INSERT DATA FOR DEPENDENT TABLES user_roles, vehicles, spare_part, inventory, service_spare_parts_map
-- =================================================================================
-- =================================================================================
-- user_roles (phụ thuộc vào app_user)
-- =================================================================================
INSERT INTO public.user_roles (user_id, role)
SELECT id, CASE
               WHEN email IN ('lengochan090105@gmail.com', 'dinhthingocanh030805@gmail.com', 'namhoai020505@gmail.com', 'boyhayhaha12345@gmail.com', 'kassassinrk@gmail.com') THEN 'ADMIN'
               WHEN email IN ('kaitetsuya91@gmail.com', 'staffrole001@gmail.com') OR email LIKE 'staff%' THEN 'STAFF'
               WHEN email IN ('shadehygge@gmail.com', 'technicianrole01@gmail.com') OR email LIKE 'tech%' THEN 'TECHNICIAN'
               ELSE 'CUSTOMER'
    END FROM public.app_user;

-- =================================================================================
-- maintenance_milestone (phụ thuộc vào car_model)
-- =================================================================================
INSERT INTO public.maintenance_milestone (kilometer_at, year_at, car_model_id, created_by, created_at, updated_at, updated_by)
SELECT km, year_at, cm.id, 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system'
FROM public.car_model cm, (VALUES (12000, 1), (24000, 2), (36000, 3), (48000, 4), (60000, 5), (72000, 6), (84000, 7), (96000, 8), (108000, 9), (120000, 10)) AS milestones(km, year_at);

-- =================================================================================
-- vehicles (phụ thuộc vào app_user, car_model)
-- =================================================================================
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, next_km, next_date, old_km, old_date, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF3'), '29A-111.11', 'VIN00000000000001', (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), 24000, '2026-10-30 17:00:00', 10000, '2025-10-30 17:00:00', 'system', NOW(), NOW(), 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF5'), '30A-222.22', 'VIN00000000000002', (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), 'system', NOW(), NOW(), 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF6'), '51G-111.13', 'VIN00000000000003', (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), 'system', NOW(), NOW(), 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF9'), '51A-CUS-159', 'VIN00000000000004', (SELECT id FROM app_user WHERE email = 'wendyhimekawa@gmail.com'), 'system', NOW(), NOW(), 'system');

-- =================================================================================
-- spare_part (phụ thuộc vào car_model)
-- =================================================================================
INSERT INTO public.spare_part (part_number, part_name, category, unit_price, car_model_id, created_at, created_by, updated_at, updated_by)
SELECT part_number, part_name, category, unit_price, car_model_id, '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222','system'
FROM ( VALUES
           -- ================== VF3 Parts (car_model_id = 1) ==================
           ('VF3-FLT-01', 'Cabin Air Filter', 'Filter', 180000, 1),
           ('VF3-BRK-01', 'Front Brake Pads', 'Brake', 800000, 1),
           ('VF3-BRK-02', 'Rear Brake Pads', 'Brake', 750000, 1),
           ('VF3-BRK-03', 'Front Brake Disc', 'Brake', 1200000, 1),
           ('VF3-WPR-01', 'Wiper Blade Kit', 'Wiper', 350000, 1),
           ('VF3-BAT-01', '12V Auxiliary Battery', 'Battery', 2800000, 1),
           ('VF3-TYR-01', 'Tire (1 piece)', 'Tire', 1500000, 1),
           ('VF3-SUS-01', 'Front Shock Absorber', 'Suspension', 1800000, 1),
           ('VF3-SUS-02', 'Rear Shock Absorber', 'Suspension', 1700000, 1),
           ('VF3-LGT-01', 'Headlight Bulb (LED)', 'Lighting', 900000, 1),
           ('VF3-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 850000, 1),
           -- ================== VF5 Parts (car_model_id = 2) ==================
           ('VF5-FLT-01', 'Cabin Air Filter', 'Filter', 220000, 2),
           ('VF5-BRK-01', 'Front Brake Pads', 'Brake', 950000, 2),
           ('VF5-BRK-02', 'Rear Brake Pads', 'Brake', 900000, 2),
           ('VF5-BRK-03', 'Front Brake Disc', 'Brake', 1400000, 2),
           ('VF5-WPR-01', 'Wiper Blade Kit', 'Wiper', 400000, 2),
           ('VF5-BAT-01', '12V Auxiliary Battery', 'Battery', 3100000, 2),
           ('VF5-TYR-01', 'Tire (1 piece)', 'Tire', 1800000, 2),
           ('VF5-SUS-01', 'Front Shock Absorber', 'Suspension', 2100000, 2),
           ('VF5-SUS-02', 'Rear Shock Absorber', 'Suspension', 2000000, 2),
           ('VF5-LGT-01', 'Headlight Bulb (LED)', 'Lighting', 1100000, 2),
           ('VF5-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 920000, 2),
           -- ================== VF6 Parts (car_model_id = 3) ==================
           ('VF6-FLT-01', 'Cabin Air Filter', 'Filter', 250000, 3),
           ('VF6-BRK-01', 'Front Brake Pads', 'Brake', 1100000, 3),
           ('VF6-BRK-02', 'Rear Brake Pads', 'Brake', 1050000, 3),
           ('VF6-BRK-03', 'Front Brake Disc', 'Brake', 1650000, 3),
           ('VF6-WPR-01', 'Wiper Blade Kit', 'Wiper', 450000, 3),
           ('VF6-BAT-01', '12V Auxiliary Battery', 'Battery', 3400000, 3),
           ('VF6-TYR-01', 'Tire (1 piece)', 'Tire', 2200000, 3),
           ('VF6-SUS-01', 'Front Shock Absorber', 'Suspension', 2400000, 3),
           ('VF6-SUS-02', 'Rear Shock Absorber', 'Suspension', 2300000, 3),
           ('VF6-LGT-01', 'Headlight Assembly (LED)', 'Lighting', 1400000, 3),
           ('VF6-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 980000, 3),
           -- ================== VF7 Parts (car_model_id = 4) ==================
           ('VF7-FLT-01', 'Cabin Air Filter', 'Filter', 280000, 4),
           ('VF7-BRK-01', 'Front Brake Pads', 'Brake', 1300000, 4),
           ('VF7-BRK-02', 'Rear Brake Pads', 'Brake', 1250000, 4),
           ('VF7-BRK-03', 'Front Brake Disc', 'Brake', 1900000, 4),
           ('VF7-WPR-01', 'Wiper Blade Kit', 'Wiper', 500000, 4),
           ('VF7-BAT-01', '12V Auxiliary Battery', 'Battery', 3700000, 4),
           ('VF7-TYR-01', 'Tire (1 piece)', 'Tire', 2600000, 4),
           ('VF7-SUS-01', 'Front Shock Absorber', 'Suspension', 2800000, 4),
           ('VF7-SUS-02', 'Rear Shock Absorber', 'Suspension', 2700000, 4),
           ('VF7-LGT-01', 'Headlight Assembly (Matrix LED)', 'Lighting', 1800000, 4),
           ('VF7-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1050000, 4),
           -- ================== VF8 Parts (car_model_id = 5) ==================
           ('VF8-FLT-01', 'Cabin Air Filter with HEPA', 'Filter', 450000, 5),
           ('VF8-BRK-01', 'Front Brake Pads (Performance)', 'Brake', 1800000, 5),
           ('VF8-BRK-02', 'Rear Brake Pads (Performance)', 'Brake', 1700000, 5),
           ('VF8-BRK-03', 'Front Brake Disc (Ventilated)', 'Brake', 2500000, 5),
           ('VF8-WPR-01', 'Wiper Blade Kit (Aero)', 'Wiper', 600000, 5),
           ('VF8-BAT-01', '12V Auxiliary Battery (AGM)', 'Battery', 4200000, 5),
           ('VF8-TYR-01', 'Tire (1 piece, 19-inch)', 'Tire', 3500000, 5),
           ('VF8-SUS-01', 'Front Air Suspension Strut', 'Suspension', 5500000, 5),
           ('VF8-SUS-02', 'Rear Air Suspension Strut', 'Suspension', 5200000, 5),
           ('VF8-LGT-01', 'Headlight Assembly (Laser)', 'Lighting', 2500000, 5),
           ('VF8-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1200000, 5),
           -- ================== VF9 Parts (car_model_id = 6) ==================
           ('VF9-FLT-01', 'Cabin Air Filter with HEPA', 'Filter', 500000, 6),
           ('VF9-BRK-01', 'Front Brake Pads (High-Performance)', 'Brake', 2200000, 6),
           ('VF9-BRK-02', 'Rear Brake Pads (High-Performance)', 'Brake', 2100000, 6),
           ('VF9-BRK-03', 'Front Brake Disc (Ventilated)', 'Brake', 3000000, 6),
           ('VF9-WPR-01', 'Wiper Blade Kit (Aero)', 'Wiper', 650000, 6),
           ('VF9-BAT-01', '12V Auxiliary Battery (AGM)', 'Battery', 4500000, 6),
           ('VF9-TYR-01', 'Tire (1 piece, 21-inch)', 'Tire', 4500000, 6),
           ('VF9-SUS-01', 'Front Air Suspension Strut', 'Suspension', 6500000, 6),
           ('VF9-SUS-02', 'Rear Air Suspension Strut', 'Suspension', 6200000, 6),
           ('VF9-LGT-01', 'Headlight Assembly (Laser)', 'Lighting', 3200000, 6),
           ('VF9-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1350000, 6)
     ) AS parts(part_number, part_name, category, unit_price, car_model_id);

-- =================================================================================
-- inventory (QUẢN LÝ TỒN KHO THEO TỪNG CENTER)
-- Liên kết đến spare_part thông qua part_number để đảm bảo tính chính xác.
-- =================================================================================
INSERT INTO public.inventory (center_id, spare_part_id, stock_quantity, min_stock_level)
SELECT inv.center_id,sp.id, inv.stock_quantity, inv.min_stock_level
FROM ( VALUES
           -- ================== Center 1: ECar Binh Duong ==================
           -- VF3 Parts
           (1, 'VF3-FLT-01', 50, 20), (1, 'VF3-BRK-01', 25, 10), (1, 'VF3-BRK-02', 25, 10), (1, 'VF3-BRK-03', 15, 8),
           (1, 'VF3-WPR-01', 40, 15), (1, 'VF3-BAT-01', 10, 5), (1, 'VF3-TYR-01', 20, 8), (1, 'VF3-SUS-01', 10, 4),
           (1, 'VF3-SUS-02', 10, 4), (1, 'VF3-LGT-01', 15, 5), (1, 'VF3-SNS-01', 20, 10),
           -- VF5 Parts
           (1, 'VF5-FLT-01', 45, 20), (1, 'VF5-BRK-01', 30, 12), (1, 'VF5-SNS-01', 20, 10),
           -- VF6 Parts
           (1, 'VF6-FLT-01', 40, 15), (1, 'VF6-BRK-01', 28, 10), (1, 'VF6-SNS-01', 18, 8),
           -- VF7 Parts
           (1, 'VF7-FLT-01', 35, 15), (1, 'VF7-BRK-01', 25, 10), (1, 'VF7-SNS-01', 15, 7),
           -- VF8 Parts
           (1, 'VF8-FLT-01', 30, 10), (1, 'VF8-BRK-01', 20, 8), (1, 'VF8-TYR-01', 16, 6),
           -- VF9 Parts
           (1, 'VF9-FLT-01', 25, 10), (1, 'VF9-BRK-01', 18, 7), (1, 'VF9-TYR-01', 12, 4),

           -- ================== Center 2: ECar Thu Duc ==================
           -- VF3 Parts
           (2, 'VF3-FLT-01', 40, 20), (2, 'VF3-BRK-01', 20, 10), (2, 'VF3-SNS-01', 15, 10),
           -- VF5 Parts
           (2, 'VF5-FLT-01', 50, 20), (2, 'VF5-BRK-01', 35, 12), (2, 'VF5-BRK-02', 30, 12), (2, 'VF5-BRK-03', 20, 8),
           (2, 'VF5-WPR-01', 40, 15), (2, 'VF5-BAT-01', 15, 5), (2, 'VF5-TYR-01', 25, 10), (2, 'VF5-SNS-01', 22, 10),
           -- VF6 Parts
           (2, 'VF6-FLT-01', 45, 15), (2, 'VF6-BRK-01', 30, 10), (2, 'VF6-LGT-01', 12, 5),
           -- VF7 Parts
           (2, 'VF7-FLT-01', 30, 15), (2, 'VF7-BRK-01', 20, 10), (2, 'VF7-WPR-01', 30, 10),
           -- VF8 Parts
           (2, 'VF8-FLT-01', 35, 10), (2, 'VF8-BRK-01', 25, 8), (2, 'VF8-SUS-01', 6, 2),
           -- VF9 Parts
           (2, 'VF9-FLT-01', 20, 10), (2, 'VF9-BRK-01', 15, 7), (2, 'VF9-LGT-01', 7, 3),

           -- ================== Center 3: ECar District 1 ==================
           -- (Giả sử center này nhỏ hơn, tồn kho ít hơn)
           -- VF3 Parts
           (3, 'VF3-FLT-01', 20, 10), (3, 'VF3-BRK-01', 10, 5), (3, 'VF3-SNS-01', 5, 5),
           -- VF5 Parts
           (3, 'VF5-FLT-01', 25, 10), (3, 'VF5-BRK-01', 15, 8), (3, 'VF5-TYR-01', 10, 5),
           -- VF6 Parts
           (3, 'VF6-FLT-01', 20, 10), (3, 'VF6-BRK-01', 10, 5), (3, 'VF6-TYR-01', 8, 4),
           -- VF8 Parts
           (3, 'VF8-FLT-01', 15, 5), (3, 'VF8-BRK-01', 8, 4), (3, 'VF8-TYR-01', 5, 3),
           -- VF9 Parts
           (3, 'VF9-FLT-01', 10, 5), (3, 'VF9-BRK-01', 5, 3), (3, 'VF9-TYR-01', 2, 2) -- Sắp hết hàng

     ) AS inv(center_id, part_number, stock_quantity, min_stock_level)
         JOIN public.spare_part sp ON inv.part_number = sp.part_number;


-- =================================================================================
-- service_spare_parts_map (QUY TẮC: Dịch vụ nào cần Phụ tùng nào)
-- Liên kết đến service và spare_part, phải được chèn sau chúng.
-- =================================================================================
INSERT INTO public.service_spare_parts_map (service_id, spare_part_id, default_quantity)
SELECT mapping.service_id,sp.id, mapping.default_quantity
FROM ( VALUES
           -- Ánh xạ các dịch vụ thay thế (F) tới các phụ tùng tương ứng

           -- Dịch vụ liên quan đến Phanh (Brake)
           (26, 'VF3-BRK-01', 1), -- Brake Pad Replacement -> VF3 Front Brake Pads
           (26, 'VF5-BRK-01', 1), -- Brake Pad Replacement -> VF5 Front Brake Pads
           (26, 'VF6-BRK-01', 1), -- Brake Pad Replacement -> VF6 Front Brake Pads
           (26, 'VF7-BRK-01', 1), -- Brake Pad Replacement -> VF7 Front Brake Pads
           (26, 'VF8-BRK-01', 1), -- Brake Pad Replacement -> VF8 Front Brake Pads
           (26, 'VF9-BRK-01', 1), -- Brake Pad Replacement -> VF9 Front Brake Pads

           (27, 'VF3-SNS-01', 1), -- ABS Sensor Replacement -> VF3 ABS Sensor
           (27, 'VF5-SNS-01', 1), -- ABS Sensor Replacement -> VF5 ABS Sensor
           (27, 'VF6-SNS-01', 1), -- ABS Sensor Replacement -> VF6 ABS Sensor
           (27, 'VF7-SNS-01', 1), -- ABS Sensor Replacement -> VF7 ABS Sensor
           (27, 'VF8-SNS-01', 1), -- ABS Sensor Replacement -> VF8 ABS Sensor
           (27, 'VF9-SNS-01', 1), -- ABS Sensor Replacement -> VF9 ABS Sensor

           (28, 'VF3-BRK-03', 2), -- Brake Disc Replacement -> VF3 Front Brake Disc (thường thay theo cặp)
           (28, 'VF5-BRK-03', 2), -- Brake Disc Replacement -> VF5 Front Brake Disc
           (28, 'VF6-BRK-03', 2), -- Brake Disc Replacement -> VF6 Front Brake Disc

           -- Dịch vụ liên quan đến Lọc (Filter) và Pin (Battery)
           (1, 'VF3-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF3 Filter
           (1, 'VF5-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF5 Filter
           (1, 'VF6-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF6 Filter
           (1, 'VF7-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF7 Filter
           (1, 'VF8-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF8 Filter
           (1, 'VF9-FLT-01', 1),  -- Maintenance Cabin Air Filter -> VF9 Filter

           (39, 'VF3-BAT-01', 1), -- Battery Service/Replacement -> VF3 12V Battery
           (39, 'VF5-BAT-01', 1), -- Battery Service/Replacement -> VF5 12V Battery
           (39, 'VF6-BAT-01', 1), -- Battery Service/Replacement -> VF6 12V Battery

           -- Dịch vụ liên quan đến Gầm và Lốp (Suspension & Tire)
           (53, 'VF3-SUS-01', 2), -- Shock Absorber Replacement -> VF3 Front Shocks (thường thay theo cặp)
           (53, 'VF5-SUS-01', 2), -- Shock Absorber Replacement -> VF5 Front Shocks
           (53, 'VF6-SUS-01', 2), -- Shock Absorber Replacement -> VF6 Front Shocks
           (53, 'VF8-SUS-01', 2), -- Shock Absorber Replacement -> VF8 Air Struts

           (50, 'VF3-TYR-01', 4), -- Tire/Wheel Replacement -> VF3 Tire (giả sử thay cả bộ 4 bánh)
           (50, 'VF5-TYR-01', 4), -- Tire/Wheel Replacement -> VF5 Tire
           (50, 'VF6-TYR-01', 4), -- Tire/Wheel Replacement -> VF6 Tire

           -- Dịch vụ liên quan đến Gạt mưa và Đèn (Wiper & Lighting)
           (15, 'VF3-WPR-01', 1), -- Wiper Blades Replacement -> VF3 Wiper Kit
           (15, 'VF5-WPR-01', 1), -- Wiper Blades Replacement -> VF5 Wiper Kit
           (15, 'VF6-WPR-01', 1), -- Wiper Blades Replacement -> VF6 Wiper Kit
           (15, 'VF8-WPR-01', 1), -- Wiper Blades Replacement -> VF8 Wiper Kit

           -- Một số dịch vụ không có trong danh sách (id > 55) nhưng có thể cần
           (56, 'VF3-LGT-01', 2)  -- Headlight Bulb Replacement -> VF3 Headlight (thường thay theo cặp)
     ) AS mapping(service_id, part_number, default_quantity)
         JOIN public.spare_part sp ON mapping.part_number = sp.part_number;

-- =================================================================================
-- maintenance_schedule (phụ thuộc vào car_model, maintenance_milestone, service)
-- =================================================================================
INSERT INTO public.maintenance_schedule (car_model_id, maintenance_milestone_id, service_id, is_default, created_by, created_at, updated_by, updated_at)
SELECT
    mm.car_model_id,
    mm.id,
    s.id,
    true AS is_default,
    'system_logic' AS created_by,
    NOW() AS created_at,
    NULL AS updated_by,
    NULL AS updated_at
FROM
    public.maintenance_milestone mm
        CROSS JOIN
    public.service s
WHERE
    s.service_type = 'M'
  AND (
    s.category = 'general'
        OR s.id = 1
        OR (s.id IN (2, 5) AND mm.year_at % 2 = 0)
        OR (s.id = 4 AND mm.year_at = 6)
    );

-- =================================================================================
-- STEP 3: INSERT DATA FOR TRANSACTIONAL TABLES (THỨ TỰ QUAN TRỌNG)
-- =================================================================================

-- =================================================================================
-- subscription_info (phụ thuộc vào app_user)
-- =================================================================================
INSERT INTO public.subscription_info (owner_id, start_date, end_date, payment_date, created_by, created_at, updated_by, updated_at)
VALUES
    ((SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), '2025-01-15 10:00:00', '2026-01-15 10:00:00', '2025-01-15 10:00:00', 'system', NOW(), 'system', NOW()),
    ((SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), '2024-12-01 14:30:00', '2025-12-01 14:30:00', '2024-12-01 14:30:00', 'system', NOW(), 'system', NOW()),
    ((SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), '2024-09-01 11:00:00', '2025-09-01 11:00:00', '2024-09-01 11:00:00', 'system', NOW(), 'system', NOW()),
    ((SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), '2025-10-29 09:18:13', '2026-10-29 09:18:13', '2025-10-29 09:18:13', 'lengochan090105@gmail.com', NOW(), 'lengochan090105@gmail.com', NOW());

-- =================================================================================
-- payment_history (Lịch sử thanh toán cho các gói dịch vụ)
-- Phụ thuộc vào subscription_info.
-- =================================================================================
INSERT INTO public.payment_history (subscription_id, payment_method, payment_status, created_at, created_by, updated_at, updated_by, payment_id, num_of_years, amount)
VALUES
    -- Lịch sử thanh toán cho gói dịch vụ id=2 của 'customerrole01@gmail.com'
    (2, 'paypal', 'APPROVED', '2025-01-15 09:55:00', 'system_seed', '2025-01-15 10:00:00', 'system_callback', 'PAYID-VALID-00001', 1, 1000.00),
    (2, 'paypal', 'INIT', '2024-11-20 14:25:00', 'system_seed', NULL, NULL, 'PAYID-PENDING-00002', 1, 1000.00),
    (2, 'paypal', 'APPROVED', '2024-11-20 14:28:00', 'system_seed', '2024-11-20 14:30:00', 'system_callback', 'PAYID-VALID-00003', 1, 1000.00),

    -- Lịch sử thanh toán cho gói dịch vụ id=3 của 'lengochan090105@gmail.com'
    (3, 'paypal', 'INIT', '2025-10-29 09:14:11', 'lengochan090105@gmail.com', NULL, NULL, 'PAYID-NEAXQWY79717451CM661680U', 1, 1000.00),
    (3, 'paypal', 'INIT', '2025-10-29 09:14:27', 'lengochan090105@gmail.com', NULL, NULL, 'PAYID-NEAXQ2Y0NK79660W9331753E', 1, 1000.00),
    (3, 'paypal', 'APPROVED', '2025-10-29 09:18:05', 'lengochan090105@gmail.com', '2025-10-29 09:18:13', 'system_callback', 'PAYID-NEAXSRI0K2341200L885441K', 1, 1000.00);


-- =================================================================================
-- bookings (phải được insert trước service_records và maintenance_history)
-- =================================================================================
INSERT INTO public.bookings (user_id, center_id, customer_phone_number, license_plate, car_model, vin_number, service_center, appointment_date_time, notes, status, created_by, created_at) VALUES
                                                                                                                                                                                                -- Booking này sẽ tự động có id = 1 (Đã hoàn thành, để tạo hóa đơn mẫu)
                                                                                                                                                                                                (
                                                                                                                                                                                                    (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'),
                                                                                                                                                                                                    (SELECT id FROM center WHERE center_name = 'ECar Thu Duc'),
                                                                                                                                                                                                    '0373587001',
                                                                                                                                                                                                    '29A-111.11',
                                                                                                                                                                                                    'VF3',
                                                                                                                                                                                                    'VIN00000000000001',
                                                                                                                                                                                                    'ECar Thu Duc',
                                                                                                                                                                                                    '2025-10-30 12:00:00',
                                                                                                                                                                                                    'Completed Service Example',
                                                                                                                                                                                                    'COMPLETED',
                                                                                                                                                                                                    'lengochan090105@gmail.com',
                                                                                                                                                                                                    '2025-10-30 10:36:36'
                                                                                                                                                                                                ),
                                                                                                                                                                                                -- Booking này sẽ tự động có id = 2 (Đang chờ, cho phiếu dịch vụ mới)
                                                                                                                                                                                                (
                                                                                                                                                                                                    (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'),
                                                                                                                                                                                                    (SELECT id FROM center WHERE center_name = 'ECar Binh Duong'),
                                                                                                                                                                                                    '0373587004',
                                                                                                                                                                                                    '51G-111.13',
                                                                                                                                                                                                    'VF6',
                                                                                                                                                                                                    'VIN00000000000003',
                                                                                                                                                                                                    'ECar Binh Duong',
                                                                                                                                                                                                    '2025-12-15 10:00:00',
                                                                                                                                                                                                    'Brake noise check',
                                                                                                                                                                                                    'PENDING',
                                                                                                                                                                                                    'customerrole01@gmail.com',
                                                                                                                                                                                                    NOW()
                                                                                                                                                                                                );
-- =================================================================================
-- maintenance_history (Phiếu dịch vụ nội bộ)
-- Tạo ra các kịch bản khác nhau để kiểm thử
-- =================================================================================
INSERT INTO public.maintenance_history (
    -- Thông tin chính
    vehicle_id, owner_id, center_id, status,
    -- Chi tiết công việc
    is_maintenance, is_repair, remark, num_of_km,
    -- Chi phí phát sinh
    has_additional_cost, additional_cost_amount, additional_cost_reason,
    -- Thông tin lịch hẹn & thời gian
    schedule_date, schedule_time, submitted_at, staff_receive_at, technician_receive_at, completed_at, hand_over_at,
    -- Nhân sự liên quan
    staff_id, technician_id
)
VALUES
    -- Kịch bản 1: Phiếu mới, đang chờ nhân viên tiếp nhận (status = CUSTOMER_SUBMITTED)
    -- Phiếu này sẽ tự động có id = 1
    (
        (SELECT id FROM vehicles WHERE license_plate = '51G-111.13'),
        (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'),
        1, -- ECar Binh Duong
        'CUSTOMER_SUBMITTED',
        true, true, 'Brake noise check', 20000,
        false, 0, null,
        '2025-12-15', '10:00:00', NOW(), null, null, null, null,
        null, null
    ),
    -- Kịch bản 2: Phiếu đang chờ khách hàng duyệt chi phí phát sinh (status = CUSTOMER_APPROVAL_PENDING)
    -- Phiếu này sẽ tự động có id = 2
    (
        (SELECT id FROM vehicles WHERE license_plate = '29A-111.11'),
        (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'),
        2, -- ECar Thu Duc
        'CUSTOMER_APPROVAL_PENDING',
        true, true, 'Check engine light', 15000,
        true, 550000, 'Cần thay thêm cảm biến ABS.',
        '2025-11-20', '09:00:00', NOW(), null, null, null, null,
        null, null
    ),
    -- Kịch bản 3: Phiếu đã hoàn thành toàn bộ quy trình (status = DONE)
    -- Phiếu này sẽ tự động có id = 3
    (
        (SELECT id FROM vehicles WHERE license_plate = '29A-111.11'),
        (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'),
        2, -- ECar Thu Duc
        'DONE',
        true, true, 'General check-up and minor repairs.', 10000,
        true, 180000, 'Chi phí phát sinh đã được duyệt.',
        '2025-10-30', '12:00:00', '2025-10-30 10:36:36', '2025-10-30 10:40:00', '2025-10-30 10:45:00', '2025-10-30 16:22:22', '2025-10-30 17:15:00',
        (SELECT id FROM app_user WHERE email = 'staffrole001@gmail.com'),
        (SELECT id FROM app_user WHERE email = 'technicianrole01@gmail.com')
    );


-- =================================================================================
-- maintenance_item_parts (Phụ tùng DỰ KIẾN được gán cho một phiếu dịch vụ)
-- =================================================================================
INSERT INTO public.maintenance_item_parts (maintenance_history_id, spare_part_id, quantity)
SELECT
    map.maintenance_history_id,
    sp.id,
    map.quantity
FROM (
         VALUES
             -- Kịch bản 1: Phiếu dịch vụ số 1 (Brake noise check cho xe VF6)
             -- Nhân viên dự kiến cần kiểm tra và có thể thay thế má phanh.
             (1, 'VF6-BRK-01', 1),

             -- Kịch bản 2: Phiếu dịch vụ số 2 (Chờ duyệt chi phí cho xe VF3)
             -- Nhân viên đã xác định cần thay 1 cảm biến ABS và đã báo giá cho khách.
             (2, 'VF3-SNS-01', 1),

             -- (Tùy chọn) Có thể dự kiến thêm các mục khác cho phiếu số 2
             (2, 'VF3-FLT-01', 1)

     ) AS map(maintenance_history_id, part_number, quantity)
         JOIN public.spare_part sp ON map.part_number = sp.part_number;


-- =================================================================================
-- service_records (Hóa đơn dịch vụ - Phụ thuộc vào bookings)
-- Tạo một bản ghi hóa đơn mẫu cho booking đã hoàn thành (booking_id=1).
-- =================================================================================
INSERT INTO public.service_records (
    booking_id,
    license_plate,
    kilometer_reading,
    service_date,
    -- Các trường tài chính đã thêm
    total_parts_cost,
    labor_cost,
    total_actual_cost,
    covered_by_package,
    additional_cost,
    -- Các trường auditing
    created_by,
    created_at
) VALUES (
             1, -- Tham chiếu đến booking có id=1 đã được tạo ở trên
             '29A-111.11',
             10000,
             '2025-10-30 17:00:00',

             -- Dữ liệu tài chính cho hóa đơn này
             980000,  -- total_parts_cost: Tổng tiền phụ tùng (180,000 cho lọc gió + 800,000 cho má phanh)
             200000,  -- labor_cost: Tiền công (ví dụ)
             1180000, -- total_actual_cost: Tổng chi phí thực tế (980,000 + 200,000)
             1000000, -- covered_by_package: Số tiền được gói bảo dưỡng chi trả (ví dụ)
             180000,  -- additional_cost: Chi phí phát sinh khách hàng phải trả thêm (1,180,000 - 1,000,000)

             'staffrole001@gmail.com',
             '2025-10-30 17:00:00'
         );

-- =================================================================================
-- service_record_details (Chi tiết công việc cho hóa đơn có id=1)
-- Mô tả lại những công việc đã được thực hiện, khớp với các phụ tùng đã dùng.
-- =================================================================================
INSERT INTO public.service_record_details (service_record_id, item_name, action, notes) VALUES
                                                                                            -- Giả sử lần dịch vụ này bao gồm gói bảo dưỡng cấp 1
                                                                                            (1, 'Level 1 Periodic Maintenance', 'INSPECT', 'All general checks completed as per schedule.'),

                                                                                            -- Công việc thay thế Lọc gió điều hòa, tương ứng với phụ tùng đã dùng
                                                                                            (1, 'Cabin Air Filter Replacement', 'REPLACE', 'Replaced with new OEM filter, part no: VF3-FLT-01.'),

                                                                                            -- Công việc thay thế Má phanh, tương ứng với phụ tùng đã dùng
                                                                                            (1, 'Front Brake Pads Replacement', 'REPLACE', 'Replaced worn pads with new set, part no: VF3-BRK-01.'),

                                                                                            -- Một công việc kiểm tra khác
                                                                                            (1, 'Brake System Check', 'INSPECT', 'Checked brake fluid level and lines. All OK.');

-- =================================================================================
-- service_part_usage (Phụ tùng ĐÃ THỰC SỰ dùng cho hóa đơn có id=1)
-- Bảng này ghi lại vật tư đã tiêu thụ để trừ kho và tính tiền.
-- Phải được chèn sau khi service_records và spare_part đã có dữ liệu.
-- =================================================================================
INSERT INTO public.service_part_usage (service_record_id, spare_part_id, quantity_used, price_at_time_of_use) VALUES
                                                                                                                  -- Đã dùng 1 'Cabin Air Filter' (VF3-FLT-01)
                                                                                                                  (1, (SELECT id FROM spare_part WHERE part_number = 'VF3-FLT-01'), 1, 180000),

                                                                                                                  -- Đã dùng 1 'Front Brake Pads' (VF3-BRK-01)
                                                                                                                  (1, (SELECT id FROM spare_part WHERE part_number = 'VF3-BRK-01'), 1, 800000);

-- =================================================================================
-- maintenance_item (Công việc cần làm cho một phiếu dịch vụ - Phụ thuộc vào maintenance_history)
-- =================================================================================
INSERT INTO public.maintenance_item (maintenance_history_id, maintenance_milestone_id, service_id, created_by, created_at)
VALUES
    -- Các công việc cho Phiếu dịch vụ số 1 (id=1: Kiểm tra phanh xe VF6)
    -- Giả sử nhân viên chọn gói bảo dưỡng cấp 2 (24,000km) và một dịch vụ sửa phanh
    (
        1, -- maintenance_history_id
        (SELECT id FROM maintenance_milestone WHERE car_model_id = 3 AND year_at = 2), -- Gói bảo dưỡng cấp 2 cho VF6
        NULL, -- Đây là bản ghi cho milestone, không có service_id
        'staffrole001@gmail.com',
        NOW()
    ),
    (
        1, -- maintenance_history_id
        NULL, -- Đây là bản ghi cho service, không có milestone_id
        (SELECT id FROM service WHERE id = 26), -- Dịch vụ "Brake Pad Replacement"
        'staffrole001@gmail.com',
        NOW()
    ),

    -- Các công việc cho Phiếu dịch vụ số 2 (id=2: Chờ duyệt chi phí thay cảm biến ABS cho VF3)
    -- Giả sử nhân viên chọn gói bảo dưỡng cấp 1 (12,000km) và dịch vụ sửa cảm biến ABS
    (
        2, -- maintenance_history_id
        (SELECT id FROM maintenance_milestone WHERE car_model_id = 1 AND year_at = 1), -- Gói bảo dưỡng cấp 1 cho VF3
        NULL,
        'kaitetsuya91@gmail.com',
        NOW()
    ),
    (
        2, -- maintenance_history_id
        NULL,
        (SELECT id FROM service WHERE id = 27), -- Dịch vụ "ABS Sensor Replacement"
        'kaitetsuya91@gmail.com',
        NOW()
    );
-- =================================================================================
-- STEP 4: UPDATE ALL SEQUENCES TO ENSURE INTEGRITY
-- =================================================================================
SELECT setval(pg_get_serial_sequence('public.app_user', 'id'), COALESCE(MAX(id), 1)) FROM public.app_user;
SELECT setval(pg_get_serial_sequence('public.car_model', 'id'), COALESCE(MAX(id), 1)) FROM public.car_model;
SELECT setval(pg_get_serial_sequence('public.center', 'id'), COALESCE(MAX(id), 1)) FROM public.center;
SELECT setval(pg_get_serial_sequence('public.service', 'id'), COALESCE(MAX(id), 1)) FROM public.service;
SELECT setval(pg_get_serial_sequence('public.maintenance_milestone', 'id'), COALESCE(MAX(id), 1)) FROM public.maintenance_milestone;
SELECT setval(pg_get_serial_sequence('public.vehicles', 'id'), COALESCE(MAX(id), 1)) FROM public.vehicles;
SELECT setval(pg_get_serial_sequence('public.subscription_info', 'id'), COALESCE(MAX(id), 1)) FROM public.subscription_info;
SELECT setval(pg_get_serial_sequence('public.payment_history', 'id'), COALESCE(MAX(id), 1)) FROM public.payment_history;
SELECT setval(pg_get_serial_sequence('public.maintenance_history', 'id'), COALESCE(MAX(id), 1)) FROM public.maintenance_history;
SELECT setval(pg_get_serial_sequence('public.maintenance_item', 'id'), COALESCE(MAX(id), 1)) FROM public.maintenance_item;
SELECT setval(pg_get_serial_sequence('public.maintenance_schedule', 'id'), COALESCE(MAX(id), 1)) FROM public.maintenance_schedule;
SELECT setval(pg_get_serial_sequence('public.spare_part', 'id'), COALESCE(MAX(id), 1)) FROM public.spare_part;
SELECT setval(pg_get_serial_sequence('public.service_records', 'id'), COALESCE(MAX(id), 1)) FROM public.service_records;
SELECT setval(pg_get_serial_sequence('public.service_record_details', 'id'), COALESCE(MAX(id), 1)) FROM public.service_record_details;
SELECT setval(pg_get_serial_sequence('public.bookings', 'id'), COALESCE(MAX(id), 1)) FROM public.bookings;
SELECT setval(pg_get_serial_sequence('public.expense', 'id'), COALESCE(MAX(id), 1)) FROM public.expense;
SELECT setval(pg_get_serial_sequence('public.service_part_usage', 'id'), COALESCE(MAX(id), 1)) FROM public.service_part_usage;
SELECT setval(pg_get_serial_sequence('public.inventory', 'id'), COALESCE(MAX(id), 1)) FROM public.inventory;
SELECT setval(pg_get_serial_sequence('public.service_spare_parts_map', 'id'), COALESCE(MAX(id), 1)) FROM public.service_spare_parts_map;
SELECT setval(pg_get_serial_sequence('public.maintenance_item_parts', 'id'), COALESCE(MAX(id), 1)) FROM public.maintenance_item_parts;
