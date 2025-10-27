package com.ecar.ecarservice.cofig;

import com.ecar.ecarservice.enitiies.MaintenanceItem;
import com.ecar.ecarservice.enitiies.MaintenanceSchedule;
import com.ecar.ecarservice.enums.MaintenanceAction;
import com.ecar.ecarservice.repositories.MaintenanceItemRepository;
import com.ecar.ecarservice.repositories.MaintenanceScheduleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final MaintenanceItemRepository itemRepository;
    private final MaintenanceScheduleRepository scheduleRepository;

    public DataSeeder(MaintenanceItemRepository itemRepository, MaintenanceScheduleRepository scheduleRepository) {
        this.itemRepository = itemRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Chỉ chạy nếu cả hai bảng đều trống
        if (itemRepository.count() == 0 && scheduleRepository.count() == 0) {
            System.out.println("Seeding maintenance data based on the provided table...");
            seedData();
            System.out.println("Seeding complete.");
        }
    }

    private void seedData() {
        // --- TẠO TẤT CẢ CÁC HẠNG MỤC ---
        Map<String, MaintenanceItem> items = new HashMap<>();

        String cat1 = "HẠNG MỤC BẢO DƯỠNG";
        items.put("loc_gio_dieu_hoa", createItem("Lọc gió điều hòa", cat1));
        items.put("dau_phanh", createItem("Dầu phanh", cat1));
        items.put("bao_duong_dieu_hoa", createItem("Bảo dưỡng hệ thống điều hòa", cat1));
        items.put("pin_chia_khoa", createItem("Pin chìa khóa điều khiển", cat1));
        items.put("pin_tbox", createItem("Pin bộ T-Box", cat1));
        items.put("nuoc_lam_mat", createItem("Nước làm mát cho Pin/ động cơ điện", cat1));

        String cat2 = "HẠNG MỤC BẢO DƯỠNG CHUNG";
        items.put("lop", createItem("Lốp (áp suất, độ mòn, đảo và cân bằng lốp)", cat2));
        items.put("ma_phanh", createItem("Má phanh và đĩa phanh", cat2));
        items.put("duong_ong_phanh", createItem("Đường ống, đầu nối hệ thống phanh", cat2));
        items.put("bo_dan_dong", createItem("Bộ dẫn động (động cơ điện và hộp số)", cat2));
        items.put("he_thong_treo", createItem("Hệ thống treo", cat2));
        items.put("truc_truyen_dong", createItem("Trục truyền động", cat2));
        items.put("khop_cau", createItem("Khớp cầu", cat2));
        items.put("thuoc_lai", createItem("Thước lái và khớp nối cầu", cat2));
        items.put("duong_ong_lam_mat", createItem("Đường ống làm mát", cat2));
        items.put("pin", createItem("Pin", cat2));
        items.put("day_cap_dien_ap_cao", createItem("Dây cáp của hệ thống điện áp cao", cat2));
        items.put("cong_sac", createItem("Cổng sạc", cat2));
        items.put("ac_quy_12v", createItem("Ắc quy 12V", cat2));
        items.put("gat_nuoc", createItem("Gạt nước rửa kính / Nước rửa kính", cat2));

        // --- TẠO LỊCH TRÌNH DỰA TRÊN CÁC MỐC KM ---
        int[] kmMarks = {12000, 24000, 36000, 48000, 60000, 72000, 84000, 96000, 108000, 120000, 132000, 144000, 156000, 168000, 180000, 192000, 204000};

        for (int km : kmMarks) {
            // --- XỬ LÝ CÁC QUY LUẬT RIÊNG ---

            // Lọc gió điều hòa: Luôn thay mới
            createSchedule(items.get("loc_gio_dieu_hoa"), km, MaintenanceAction.REPLACE);

            // Dầu phanh: kiểm tra mỗi 12k, thay mới mỗi 24k
            if (km % 24000 == 0) {
                createSchedule(items.get("dau_phanh"), km, MaintenanceAction.REPLACE);
            } else if (km % 12000 == 0) {
                createSchedule(items.get("dau_phanh"), km, MaintenanceAction.INSPECT);
            }

            // Bảo dưỡng hệ thống điều hòa: Thay mới mỗi 60k
            if (km % 60000 == 0) {
                createSchedule(items.get("bao_duong_dieu_hoa"), km, MaintenanceAction.REPLACE);
            }

            // Pin chìa khóa: Thay mới mỗi 24k
            if (km % 24000 == 0) {
                createSchedule(items.get("pin_chia_khoa"), km, MaintenanceAction.REPLACE);
            }

            // Pin bộ T-Box: Thay mới mỗi 72k
            if (km % 72000 == 0) {
                createSchedule(items.get("pin_tbox"), km, MaintenanceAction.REPLACE);
            }

            // Nước làm mát: kiểm tra mỗi 12k, thay mới mỗi 120k
            if (km == 120000) {
                createSchedule(items.get("nuoc_lam_mat"), km, MaintenanceAction.REPLACE);
            } else {
                createSchedule(items.get("nuoc_lam_mat"), km, MaintenanceAction.INSPECT);
            }

            // --- XỬ LÝ CÁC HẠNG MỤC CHUNG ---
            createSchedule(items.get("lop"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("ma_phanh"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("duong_ong_phanh"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("bo_dan_dong"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("he_thong_treo"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("truc_truyen_dong"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("khop_cau"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("thuoc_lai"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("duong_ong_lam_mat"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("pin"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("day_cap_dien_ap_cao"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("cong_sac"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("ac_quy_12v"), km, MaintenanceAction.INSPECT);
            createSchedule(items.get("gat_nuoc"), km, MaintenanceAction.INSPECT);
        }
    }

    private MaintenanceItem createItem(String name, String category) {
        MaintenanceItem item = new MaintenanceItem();
        item.setName(name);
        item.setCategory(category);
        return itemRepository.save(item);
    }

    private void createSchedule(MaintenanceItem item, int km, MaintenanceAction action) {
        if (item == null) return;
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setItem(item);
        schedule.setKilometerMark(km);
        schedule.setAction(action);
        scheduleRepository.save(schedule);
    }
}