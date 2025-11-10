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
    public.expense
    RESTART IDENTITY CASCADE;


-- =================================================================================
-- STEP 1: INSERT DATA FOR BASE TABLES
-- =================================================================================

-- =================================================================================
-- app_user
-- =================================================================================
INSERT INTO public.app_user (active, email, sub, full_name, phone_no, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                          (true, 'lengochan090105@gmail.com', '117568473599883678495', 'Lê Ngọc Hân', '0373587001', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'dinhthingocanh0308@gmail.com', '105167307593551204911', 'Ngọc Anh', '0373587008', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'namhoai020505@gmail.com', '118080103497063505858', 'Nam Hoài', '0373587009', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'boyhayhaha12345@gmail.com', '10414838788924653426', 'Nguyễn Đăng Phú', '0373587010', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'kassassinrk@gmail.com', '101853864144089879263', 'Nguyễn Dũng', '0373587011', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staffrole001@gmail.com', '103635268146202778075', 'Staff Role', '0373587002', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'technicianrole01@gmail.com', '112040040855698268458 ', 'Technician Role','0373587003', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customerrole01@gmail.com', '110833741228031693365', 'Customer Role', '0373587004', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'kaitetsuya91@gmail.com', '101969093178465016620', 'Luân Hoàng', '0373587005', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'shadehygge@gmail.com', '115145529639894629785', 'Hygge Shade', '0373587006', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'wendyhimekawa@gmail.com', '115830350857850462621', 'Alvarez Wendy', '0373587007', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');

INSERT INTO public.app_user (active, email, sub, full_name, phone_no, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                          -- Sample Staff Accounts (user01x)
                                                                                                                          (true, 'staff010@example.com', 'sub-010', 'Võ Đức Anh', '0912345010','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff011@example.com', 'sub-011', 'Nguyễn Quốc Bảo', '0912345011', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff012@example.com', 'sub-012', 'Huỳnh Vũ Bằng', '0912345012', '2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff013@example.com', 'sub-013', 'Nguyễn Ngọc Minh Châu', '0912345013','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff014@example.com', 'sub-014', 'Nguyễn Ngọc Trân Châu', '0912345014','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff015@example.com', 'sub-015', 'Phạm Huy Cường', '0912345015','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff016@example.com', 'sub-016', 'Lê Thị Ngọc Hân', '0912345016','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff017@example.com', 'sub-017', 'Nguyễn Thị Thanh Hân', '0912345017','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff018@example.com', 'sub-018', 'Trần Công Hiệp', '0912345018','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'staff019@example.com', 'sub-019', 'Phan Sơn Hoàng', '0912345019','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');

INSERT INTO public.app_user (active, email, sub, full_name, phone_no, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                          -- Sample Technician Accounts (user02x)
                                                                                                                          (true, 'tech020@example.com', 'sub-020', 'Đặng Nguyễn Trung Huy', '0912345020','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech021@example.com', 'sub-021', 'Nguyễn Phan Minh Hưng', '0912345021','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech022@example.com', 'sub-022', 'Lê Quang Khải', '0912345022','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech023@example.com', 'sub-023', 'Lê Nguyên Khan', '0912345023','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech024@example.com', 'sub-024', 'Dương Hồng Khang', '0912345024','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech025@example.com', 'sub-025', 'Nguyễn Đăng Khoa', '0912345025','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech026@example.com', 'sub-026', 'Nguyễn Ngọc Kiều My', '0912345026','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech027@example.com', 'sub-027', 'Phan Cao Trọng Nghĩa', '0912345027','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech028@example.com', 'sub-028', 'Trần Thúy Ngọc', '0912345028','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'tech029@example.com', 'sub-029', 'Lê Hoàng Uyển Nhi', '0912345029','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');

INSERT INTO public.app_user (active, email, sub, full_name, phone_no, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                          -- Sample Customer Accounts (user03x, user04x)
                                                                                                                          (true, 'customer030@example.com', 'sub-030', 'Châu Hiệp Phát', '0912345030','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer031@example.com', 'sub-031', 'Nguyễn Hồng Phúc', '0912345031','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer032@example.com', 'sub-032', 'Phan Tâm Phương', '0912345032','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer033@example.com', 'sub-033', 'Võ Hồng Phương', '0912345033','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer034@example.com', 'sub-034', 'Lê Nguyễn Ngọc Quý', '0912345034','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer035@example.com', 'sub-035', 'Nguyễn Kim Quyên', '0912345035','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer036@example.com', 'sub-036', 'Ngô Thị Mỹ Quỳnh', '0912345036','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer037@example.com', 'sub-037', 'Huỳnh Trúc Tâm', '0912345037','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer038@example.com', 'sub-038', 'Trần Hưng Thịnh', '0912345038','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');

INSERT INTO public.app_user (active, email, sub, full_name, phone_no, created_at, created_by, updated_at, updated_by) VALUES
                                                                                                                          -- Sample Customer Accounts (user03x, user04x)
                                                                                                                          (true, 'customer039@example.com', 'sub-039', 'Bùi Ngọc Minh Thư', '0912345039','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer040@example.com', 'sub-040', 'Đoàn Thị Kiều Thư', '0912345040','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer041@example.com', 'sub-041', 'Lê Minh Thư', '0912345041','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer042@example.com', 'sub-042', 'Nguyễn Lý Mỹ Tiên', '0912345042','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer043@example.com', 'sub-043', 'Nguyễn Thị Cẩm Tiên', '0912345043','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer044@example.com', 'sub-044', 'Nguyễn Thị Cẩm Tiên', '0912345044','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer045@example.com', 'sub-045', 'Trần Ngọc Quế Trang', '0912345045','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer046@example.com', 'sub-046', 'Võ Thị Thùy Trinh', '0912345046','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer047@example.com', 'sub-047', 'Huỳnh Thị Thanh Trúc', '0912345047','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system'),
                                                                                                                          (true, 'customer048@example.com', 'sub-048', 'Nguyễn Thanh Trúc', '0912345048','2025-10-11 08:34:17.121222', 'system', '2025-10-11 08:34:17.121222', 'system');

-- =================================================================================
-- user_roles
-- =================================================================================
INSERT INTO public.user_roles (user_id, role)
SELECT id, CASE
               WHEN email IN ('lengochan090105@gmail.com', 'dinhthingocanh0308@gmail.com', 'namhoai020505@gmail.com', 'boyhayhaha12345@gmail.com', 'kassassinrk@gmail.com') THEN 'ADMIN'
               WHEN email IN ('kaitetsuya91@gmail.com', 'staffrole001@gmail.com') OR email LIKE 'staff%' THEN 'STAFF'
               WHEN email IN ('shadehygge@gmail.com', 'technicianrole01@gmail.com') OR email LIKE 'tech%' THEN 'TECHNICIAN'
               ELSE 'CUSTOMER'
    END FROM public.app_user;

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
-- STEP 2: INSERT DATA FOR TABLES WITH FOREIGN KEYS
-- =================================================================================

-- =================================================================================
-- maintenance_milestone (car_model)
-- =================================================================================
INSERT INTO public.maintenance_milestone (kilometer_at, year_at, car_model_id, created_by, created_at, updated_at, updated_by)
SELECT km, year_at, cm.id, 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system'
FROM public.car_model cm, (VALUES (12000, 1), (24000, 2), (36000, 3), (48000, 4), (60000, 5), (72000, 6), (84000, 7), (96000, 8), (108000, 9), (120000, 10)) AS milestones(km, year_at);


-- =================================================================================
-- vehicles. app_user (qua owner_id), car_model (qua car_model_id)
-- =================================================================================
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, next_km, next_date, old_km, old_date, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF3'), '29A-111.11', 'VIN001', (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), 24000, '2026-10-30 17:00:00', 10000, '2025-10-30 17:00:00', 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF5'), '30A-222.22', 'VIN002', (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF6'), '51G-111.13', 'VIN003', (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF7'), '51H-111.14', 'VIN004', (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');
INSERT INTO public.vehicles (active, car_model_id, license_plate, vin_number, owner_id, created_by, created_at, updated_at, updated_by)
VALUES (true, (SELECT id FROM car_model WHERE car_name = 'VF8'), '60B-222.15', 'VIN005', (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), 'system', '2025-10-11 08:34:17', '2025-10-11 08:34:17', 'system');


-- =================================================================================
-- subscription_info (Bảng gói dịch vụ). phụ thuộc vào app_user
-- =================================================================================
INSERT INTO public.subscription_info (owner_id, start_date, end_date, payment_date, created_by, created_at, updated_by, updated_at)
VALUES
    -- Scenario 1: Active subscription for user 'customerrole01@gmai.com'
    (
        (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'),
        '2025-01-15 10:00:00',
        '2026-01-15 10:00:00',
        '2025-01-15 10:00:00',
        'system', NOW(), 'system', NOW()
    ),
    -- Scenario 2: Subscription about to expire for user 'customer030@example.com'
    (
        (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'),
        '2024-12-01 14:30:00',
        '2025-12-01 14:30:00',
        '2024-12-01 14:30:00',
        'system', NOW(), 'system', NOW()
    ),
    -- Scenario 3: Expired subscription for user 'customer031@example.com'
    (
        (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'),
        '2024-09-01 11:00:00',
        '2025-09-01 11:00:00',
        '2024-09-01 11:00:00',
        'system', NOW(), 'system', NOW()
    ),
    -- Scenario 4: Active subscription for the admin 'lengochan090105@gmail.com'
    (
        (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'),
        '2025-10-29 09:18:13',
        '2026-10-29 09:18:13',
        '2025-10-29 09:18:13',
        'lengochan090105@gmail.com', NOW(), 'lengochan090105@gmail.com', NOW()
    );


-- =================================================================================
-- payment_history
-- =================================================================================
INSERT INTO public.payment_history (subscription_id, payment_method, payment_status, created_at, created_by, updated_at, updated_by, payment_id, num_of_years, amount)
VALUES
    -- Payment history for Subscription belonging to 'customerrole01@gmai.com'
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'APPROVED', '2025-01-15 09:55:00', 'system_seed', '2025-01-15 10:00:00', 'system_callback', 'PAYID-VALID-00001', 1, 1000.00),
    -- Payment history for Subscription belonging to 'customer030@example.com'
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'INIT', '2024-11-20 14:25:00', 'system_seed', NULL, NULL, 'PAYID-PENDING-00002', 1, 1000.00),
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'APPROVED', '2024-11-20 14:28:00', 'system_seed', '2024-11-20 14:30:00', 'system_callback', 'PAYID-VALID-00003', 1, 1000.00),
    -- Payment history for Subscription belonging to 'lengochan090105@gmail.com'
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'INIT', '2025-10-29 09:14:11', 'lengochan090105@gmail.com', NULL, NULL, 'PAYID-NEAXQWY79717451CM661680U', 1, 1000.00),
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'INIT', '2025-10-29 09:14:27', 'lengochan090105@gmail.com', NULL, NULL, 'PAYID-NEAXQ2Y0NK79660W9331753E', 1, 1000.00),
    ((SELECT id FROM subscription_info WHERE owner_id = (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com') ORDER BY id DESC LIMIT 1),'paypal', 'APPROVED', '2025-10-29 09:18:05', 'lengochan090105@gmail.com', '2025-10-29 09:18:13', 'system_callback', 'PAYID-NEAXSRI0K2341200L885441K', 1, 1000.00);



-- =================================================================================
-- spare_part), phụ thuộc vào car_model
-- =================================================================================
INSERT INTO public.spare_part (part_number, part_name, category, unit_price, stock_quantity, min_stock_level, car_model_id, created_at, created_by, updated_at, updated_by)
SELECT
    part_number, part_name, category, unit_price, stock_quantity, min_stock_level, car_model_id,'2025-10-11 08:34:17.121222', 'system','2025-10-11 08:34:17.121222', 'system'
FROM
    (VALUES
         -- ================== VF3 Parts (car_model_id = 1) ==================
         ('VF3-FLT-01', 'Cabin Air Filter', 'Filter', 180000, 50, 20, 1),
         ('VF3-BRK-01', 'Front Brake Pads', 'Brake', 800000, 25, 10, 1),
         ('VF3-BRK-02', 'Rear Brake Pads', 'Brake', 750000, 25, 10, 1),
         ('VF3-BRK-03', 'Front Brake Disc', 'Brake', 1200000, 15, 8, 1),
         ('VF3-WPR-01', 'Wiper Blade Kit', 'Wiper', 350000, 40, 15, 1),
         ('VF3-BAT-01', '12V Auxiliary Battery', 'Battery', 2800000, 10, 5, 1),
         ('VF3-TYR-01', 'Tire (1 piece)', 'Tire', 1500000, 20, 8, 1),
         ('VF3-SUS-01', 'Front Shock Absorber', 'Suspension', 1800000, 10, 4, 1),
         ('VF3-SUS-02', 'Rear Shock Absorber', 'Suspension', 1700000, 10, 4, 1),
         ('VF3-LGT-01', 'Headlight Bulb (LED)', 'Lighting', 900000, 15, 5, 1),
         ('VF3-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 850000, 20, 10, 1),
         -- ================== VF5 Parts (car_model_id = 2) ==================
         ('VF5-FLT-01', 'Cabin Air Filter', 'Filter', 220000, 45, 20, 2),
         ('VF5-BRK-01', 'Front Brake Pads', 'Brake', 950000, 30, 12, 2),
         ('VF5-BRK-02', 'Rear Brake Pads', 'Brake', 900000, 30, 12, 2),
         ('VF5-BRK-03', 'Front Brake Disc', 'Brake', 1400000, 18, 8, 2),
         ('VF5-WPR-01', 'Wiper Blade Kit', 'Wiper', 400000, 40, 15, 2),
         ('VF5-BAT-01', '12V Auxiliary Battery', 'Battery', 3100000, 12, 5, 2),
         ('VF5-TYR-01', 'Tire (1 piece)', 'Tire', 1800000, 25, 10, 2),
         ('VF5-SUS-01', 'Front Shock Absorber', 'Suspension', 2100000, 12, 5, 2),
         ('VF5-SUS-02', 'Rear Shock Absorber', 'Suspension', 2000000, 12, 5, 2),
         ('VF5-LGT-01', 'Headlight Bulb (LED)', 'Lighting', 1100000, 15, 5, 2),
         ('VF5-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 920000, 20, 10, 2),
         -- ================== VF6 Parts (car_model_id = 3) ==================
         ('VF6-FLT-01', 'Cabin Air Filter', 'Filter', 250000, 40, 15, 3),
         ('VF6-BRK-01', 'Front Brake Pads', 'Brake', 1100000, 28, 10, 3),
         ('VF6-BRK-02', 'Rear Brake Pads', 'Brake', 1050000, 28, 10, 3),
         ('VF6-BRK-03', 'Front Brake Disc', 'Brake', 1650000, 15, 7, 3),
         ('VF6-WPR-01', 'Wiper Blade Kit', 'Wiper', 450000, 35, 15, 3),
         ('VF6-BAT-01', '12V Auxiliary Battery', 'Battery', 3400000, 10, 5, 3),
         ('VF6-TYR-01', 'Tire (1 piece)', 'Tire', 2200000, 22, 8, 3),
         ('VF6-SUS-01', 'Front Shock Absorber', 'Suspension', 2400000, 10, 4, 3),
         ('VF6-SUS-02', 'Rear Shock Absorber', 'Suspension', 2300000, 10, 4, 3),
         ('VF6-LGT-01', 'Headlight Assembly (LED)', 'Lighting', 1400000, 12, 5, 3),
         ('VF6-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 980000, 18, 8, 3),
         -- ================== VF7 Parts (car_model_id = 4) ==================
         ('VF7-FLT-01', 'Cabin Air Filter', 'Filter', 280000, 35, 15, 4),
         ('VF7-BRK-01', 'Front Brake Pads', 'Brake', 1300000, 25, 10, 4),
         ('VF7-BRK-02', 'Rear Brake Pads', 'Brake', 1250000, 25, 10, 4),
         ('VF7-BRK-03', 'Front Brake Disc', 'Brake', 1900000, 15, 6, 4),
         ('VF7-WPR-01', 'Wiper Blade Kit', 'Wiper', 500000, 30, 10, 4),
         ('VF7-BAT-01', '12V Auxiliary Battery', 'Battery', 3700000, 10, 4, 4),
         ('VF7-TYR-01', 'Tire (1 piece)', 'Tire', 2600000, 20, 8, 4),
         ('VF7-SUS-01', 'Front Shock Absorber', 'Suspension', 2800000, 8, 3, 4),
         ('VF7-SUS-02', 'Rear Shock Absorber', 'Suspension', 2700000, 8, 3, 4),
         ('VF7-LGT-01', 'Headlight Assembly (Matrix LED)', 'Lighting', 1800000, 10, 4, 4),
         ('VF7-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1050000, 15, 7, 4),
         -- ================== VF8 Parts (car_model_id = 5) ==================
         ('VF8-FLT-01', 'Cabin Air Filter with HEPA', 'Filter', 450000, 30, 10, 5),
         ('VF8-BRK-01', 'Front Brake Pads (Performance)', 'Brake', 1800000, 20, 8, 5),
         ('VF8-BRK-02', 'Rear Brake Pads (Performance)', 'Brake', 1700000, 20, 8, 5),
         ('VF8-BRK-03', 'Front Brake Disc (Ventilated)', 'Brake', 2500000, 12, 5, 5),
         ('VF8-WPR-01', 'Wiper Blade Kit (Aero)', 'Wiper', 600000, 25, 10, 5),
         ('VF8-BAT-01', '12V Auxiliary Battery (AGM)', 'Battery', 4200000, 8, 3, 5),
         ('VF8-TYR-01', 'Tire (1 piece, 19-inch)', 'Tire', 3500000, 16, 6, 5),
         ('VF8-SUS-01', 'Front Air Suspension Strut', 'Suspension', 5500000, 6, 2, 5),
         ('VF8-SUS-02', 'Rear Air Suspension Strut', 'Suspension', 5200000, 6, 2, 5),
         ('VF8-LGT-01', 'Headlight Assembly (Laser)', 'Lighting', 2500000, 8, 3, 5),
         ('VF8-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1200000, 15, 5, 5),
         -- ================== VF9 Parts (car_model_id = 6) ==================
         ('VF9-FLT-01', 'Cabin Air Filter with HEPA', 'Filter', 500000, 25, 10, 6),
         ('VF9-BRK-01', 'Front Brake Pads (High-Performance)', 'Brake', 2200000, 18, 7, 6),
         ('VF9-BRK-02', 'Rear Brake Pads (High-Performance)', 'Brake', 2100000, 18, 7, 6),
         ('VF9-BRK-03', 'Front Brake Disc (Ventilated)', 'Brake', 3000000, 10, 4, 6),
         ('VF9-WPR-01', 'Wiper Blade Kit (Aero)', 'Wiper', 650000, 25, 10, 6),
         ('VF9-BAT-01', '12V Auxiliary Battery (AGM)', 'Battery', 4500000, 8, 3, 6),
         ('VF9-TYR-01', 'Tire (1 piece, 21-inch)', 'Tire', 4500000, 12, 4, 6),
         ('VF9-SUS-01', 'Front Air Suspension Strut', 'Suspension', 6500000, 5, 2, 6),
         ('VF9-SUS-02', 'Rear Air Suspension Strut', 'Suspension', 6200000, 5, 2, 6),
         ('VF9-LGT-01', 'Headlight Assembly (Laser)', 'Lighting', 3200000, 7, 3, 6),
         ('VF9-SNS-01', 'ABS Wheel Speed Sensor', 'Sensor', 1350000, 12, 5, 6)
    ) AS parts(part_number, part_name, category, unit_price, stock_quantity, min_stock_level, car_model_id);



-- =================================================================================
-- maintenance_schedule
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
    -- Rule 1: "general" check-up services are required at EVERY milestone.
    s.category = 'general'
        -- Rule 2: "Cabin Air Filter" (id=1) is replaced at EVERY milestone.
        OR s.id = 1
        -- Rule 3: "Brake Fluid" (id=2) and "Coolant" (id=5) are replaced in EVEN years (2, 4, 6, 8, 10).
        OR (s.id IN (2, 5) AND mm.year_at % 2 = 0)
        -- Rule 4: "T-BOX Battery" (id=4) is replaced only at the 6-year milestone.
        OR (s.id = 4 AND mm.year_at = 6)
    );



-- =================================================================================
-- maintenance_history
-- Status Enum: 0=CUSTOMER_SUBMITTED, 1=TECHNICIAN_RECEIVED, 2=TECHNICIAN_COMPLETED, 3=DONE
-- =================================================================================
-- Scenario 1: A NEW appointment, pending staff acceptance (status=0)
INSERT INTO public.maintenance_history (vehicle_id, owner_id, num_of_km, submitted_at, status, is_maintenance, is_repair, remark, center_id, schedule_time, schedule_date, created_by, created_at)
VALUES ((SELECT id FROM vehicles WHERE license_plate = '51G-111.13'), (SELECT id FROM app_user WHERE email = 'customerrole01@gmail.com'), 5000, '2025-12-09 09:00:00', 0, true, false, 'First periodic maintenance check.', 1, '10:00:00', '2025-12-15'::DATE, 'customerrole01@gmail.com', '2025-12-09 09:00:00');

-- Scenario 4: A fully completed record that will have a corresponding service_record
INSERT INTO public.maintenance_history (vehicle_id, owner_id, staff_id, technician_id, num_of_km, submitted_at, staff_receive_at, technician_receive_at, completed_at, hand_over_at, status, is_maintenance, is_repair, remark, center_id, schedule_time, schedule_date, created_by, created_at, updated_by, updated_at)
VALUES ((SELECT id FROM vehicles WHERE license_plate = '29A-111.11'), (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), (SELECT id FROM app_user WHERE email = 'staffrole001@gmail.com'), (SELECT id FROM app_user WHERE email = 'technicianrole01@gmail.com'), 10000, '2025-10-30 10:36:36', '2025-10-30 10:40:00', '2025-10-30 10:45:00', '2025-10-30 16:22:22', '2025-10-30 17:15:00', 3, true, true, 'General check-up and minor repairs.', 2, '12:00:00', '2025-10-30'::DATE, 'lengochan090105@gmail.com', '2025-10-30 10:36:36', 'staffrole001@gmail.com', '2025-10-30 17:15:00');

-- A booking record that corresponds to the completed maintenance history
INSERT INTO public.bookings (user_id, customer_phone_number, license_plate, car_model, vin_number, service_center, appointment_date_time, notes, status, created_by, created_at)
VALUES((SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'), '0373587006', '29A-111.11', 'VF3', 'VIN001', 'ECar Thu Duc', '2025-10-30 12:00:00', 'General check-up and minor repairs.', 'COMPLETED', 'lengochan090105@gmail.com', '2025-10-30 10:36:36');

-- service_records: Links to the BOOKING, not the maintenance_history
INSERT INTO public.service_records (booking_id, license_plate, kilometer_reading, service_date, created_by, created_at)
VALUES ((SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00'), '29A-111.11', 10000, '2025-10-30 17:00:00', 'staffrole001@gmail.com', '2025-10-30 17:00:00');

-- service_record_details
INSERT INTO public.service_record_details (service_record_id, item_name, action, notes) VALUES
                                                                                            (1, 'Level 1 Periodic Maintenance (12,000 km)', 'INSPECT', 'Completed as scheduled.'),
                                                                                            (1, 'ABS Sensor Replacement', 'REPLACE', 'Replaced front right wheel sensor.'),
                                                                                            (1, 'Brake Caliper Inspection', 'INSPECT', 'Wear is within acceptable limits.'),
                                                                                            (1, 'Electrical System Check', 'INSPECT', 'Minor fault detected and fixed.');


-- =================================================================================
-- maintenance_item
-- =================================================================================
INSERT INTO public.maintenance_item (maintenance_history_id, maintenance_milestone_id, service_id, created_by, created_at, updated_by, updated_at)
VALUES
    -- Link to the periodic maintenance milestone
    (
        (SELECT id FROM maintenance_history WHERE vehicle_id = (SELECT id FROM vehicles WHERE license_plate = '29A-111.11') AND submitted_at = '2025-10-30 10:36:36'),
        (SELECT id FROM maintenance_milestone WHERE car_model_id = 1 AND year_at = 1),
        NULL,
        'technicianrole01@gmail.com',
        '2025-10-30 16:22:22',
        NULL,
        NULL
    ),
    -- Link to the repair service: ABS Sensor (id=27)
    (
        (SELECT id FROM maintenance_history WHERE vehicle_id = (SELECT id FROM vehicles WHERE license_plate = '29A-111.11') AND submitted_at = '2025-10-30 10:36:36'),
        NULL,
        27,
        'technicianrole01@gmail.com',
        '2025-10-30 16:22:22',
        NULL,
        NULL
    ),
    -- Link to the repair service: Brake Caliper (id=30)
    (
        (SELECT id FROM maintenance_history WHERE vehicle_id = (SELECT id FROM vehicles WHERE license_plate = '29A-111.11') AND submitted_at = '2025-10-30 10:36:36'),
        NULL,
        30,
        'technicianrole01@gmail.com',
        '2025-10-30 16:22:22',
        NULL,
        NULL
    ),
    -- Link to the repair service: Other (id=32)
    (
        (SELECT id FROM maintenance_history WHERE vehicle_id = (SELECT id FROM vehicles WHERE license_plate = '29A-111.11') AND submitted_at = '2025-10-30 10:36:36'),
        NULL,
        32,
        'technicianrole01@gmail.com',
        '2025-10-30 16:22:22',
        NULL,
        NULL
    );



-- =================================================================================
-- STEP 5: COMPLETE THE CYCLE (Create a Booking and its corresponding Service Record)
-- =================================================================================
-- 5.1: First, create a sample 'Booking' record that corresponds to our completed 'MaintenanceHistory' Scenario 4.
INSERT INTO public.bookings (user_id, customer_phone_number, license_plate, car_model, vin_number, service_center, appointment_date_time, notes, status, created_by, created_at)
VALUES(
          (SELECT id FROM app_user WHERE email = 'lengochan090105@gmail.com'),
          '0373587006',
          '29A-111.11',
          'VF3',
          'VIN001',
          'ECar Thu Duc',
          '2025-10-30 12:00:00',
          'General check-up and minor repairs.',
          'COMPLETED',
          'lengochan090105@gmail.com',
          '2025-10-30 10:36:36'
      );

-- 5.2: Now, create the main service record, linking it to the 'Booking' we just created.
INSERT INTO public.service_records (booking_id, license_plate, kilometer_reading, service_date, created_by, created_at)
VALUES (
           (SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00' ORDER BY id DESC LIMIT 1),
    '29A-111.11',
    10000,
    '2025-10-30 17:00:00',
    'staffrole001@gmail.com',
    '2025-10-30 17:00:00'
    );

-- 5.3: Add the details for the service record created above.
INSERT INTO public.service_record_details (service_record_id, item_name, action, notes)
VALUES
    (
        (SELECT id FROM service_records WHERE booking_id = (SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00' ORDER BY id DESC LIMIT 1) ORDER BY id DESC LIMIT 1),
        'Level 1 Periodic Maintenance (12,000 km)',
        'INSPECT',
        'Completed as scheduled.'
    ),
    (
        (SELECT id FROM service_records WHERE booking_id = (SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00' ORDER BY id DESC LIMIT 1) ORDER BY id DESC LIMIT 1),
    'ABS Sensor Replacement',
    'REPLACE',
    'Replaced front right wheel sensor.'
    ),
    (
    (SELECT id FROM service_records WHERE booking_id = (SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00' ORDER BY id DESC LIMIT 1) ORDER BY id DESC LIMIT 1),
    'Brake Caliper Inspection',
    'INSPECT',
    'Wear is within acceptable limits.'
    ),
    (
    (SELECT id FROM service_records WHERE booking_id = (SELECT id FROM bookings WHERE license_plate = '29A-111.11' AND appointment_date_time = '2025-10-30 12:00:00' ORDER BY id DESC LIMIT 1) ORDER BY id DESC LIMIT 1),
    'Electrical System Check',
    'INSPECT',
    'Minor fault detected and fixed.'
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
