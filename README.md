# KẾ HOẠCH DỰ ÁN & PHÂN CÔNG NHIỆM VỤ
## XÂY DỰNG CHATBOT TƯ VẤN & ĐẶT LỊCH KHÁM NHA KHOA "SMILECARE"
**Công nghệ:** Spring Boot • Spring AI (ChatClient, ChatMemory, @Tool)  
**Nhóm thực hiện:** Quang, Dương, Toàn (Leader), Hoàng

---

## I. Mô Hình Chia Việc Code Song Song (4 File Độc Lập)

```text
[Phút 0 - 5] Toàn khởi tạo Base Project & Push Git
                     │
    ┌────────────────┼────────────────┬────────────────┐
    ▼                ▼                ▼                ▼
 [Toàn - Leader]   [Quang]          [Dương]          [Hoàng]
 ChatClient &      Lookup Tools     Booking Tools    Cancel/Reschedule
 ChatMemory        (@Tool tra cứu)  (@Tool đặt lịch) & Business Tools
 (AiConfig.java)   (LookupTools)    (BookingTools)   (ManageTools)
```

## II. Phân Công Chi Tiết 4 Thành Viên

### 1. Toàn (Leader) — Cấu hình Spring AI, ChatClient & ChatMemory
* **File phụ trách:** `AiConfig.java`, `ChatController.java`
* **Nhiệm vụ:**
    * Tạo base project (Spring Boot + Spring AI).
    * Viết System Prompt quy định vai trò SmileCare và yêu cầu LLM: Tự hỏi lại nếu thiếu Tên/SĐT/Giờ khám, chỉ gọi Tool khi đủ thông tin.
    * Cấu hình ChatMemory (in-memory) gắn qua Advisor vào ChatClient.
    * Viết Controller tiếp nhận `POST /api/chat` với param: `conversationId`, `userMessage`.

### 2. Quang — Nhóm `@Tool` Tra Cứu (Lookup Tools)
* **File phụ trách:** `LookupTools.java`, `MockData.java`
* **Nhiệm vụ:**
    * Tạo dữ liệu mẫu trong RAM: danh sách Bác sĩ (chuyên khoa, kinh nghiệm) và Dịch vụ (tên, giá).
    * Viết 2 `@Tool`:
        * `@Tool searchServices(keyword)`: Tra cứu dịch vụ và bảng giá.
        * `@Tool getDoctorsBySpecialty(specialty)`: Tìm bác sĩ theo chuyên môn.

### 3. Dương — Nhóm `@Tool` Đặt Lịch & Kiểm Tra Khung Giờ
* **File phụ trách:** `BookingTools.java`
* **Nhiệm vụ:**
    * Tạo danh sách `List<Appointment>` lưu lịch hẹn tạm thời trên RAM.
    * Viết 2 `@Tool`:
        * `@Tool checkAvailableSlots(doctorName, date)`: Trả về các slot giờ còn trống (VD: 09:00, 14:00, 16:00).
        * `@Tool bookAppointment(customerName, customerPhone, doctorName, serviceName, dateTime)`: Kiểm tra không trùng giờ, lưu lịch, trả về mã đặt lịch (VD: BOOK-101).

### 4. Hoàng — Nhóm `@Tool` Quản Lý & Đổi / Hủy Lịch
* **File phụ trách:** `AppointmentManagementTools.java`
* **Nhiệm vụ:**
    * Phối hợp cùng kho dữ liệu của Dương để xử lý các nghiệp vụ cập nhật lịch.
    * Viết 2 `@Tool`:
        * `@Tool getAppointmentDetails(bookingCode)`: Tra cứu lại thông tin lịch đã đặt theo mã.
        * `@Tool cancelOrReschedule(bookingCode, action, newDateTime)`: Hủy lịch hoặc cập nhật sang giờ khám mới nếu còn trống.

---

## III. Quy Trình Phối Hợp Nhóm & Git Workflow

Nhóm áp dụng mô hình Git Flow tiêu chuẩn để đảm bảo minh bạch lịch sử đóng góp:

```
[main] ────────────────────────────────────────── (Bản nộp cuối cùng) ──────────────────────────►
  │                                                      ▲
[develop] ──────────┬──────────────┬──────────────┬──────┴────── (Nhánh tích hợp chung) ────────►
                    ▲              ▲              ▲
                    │              │              │
          (PR Toàn) │   (PR Quang) │   (PR Dương) │   (PR Hoàng)
                    │              │              │
[feature/...]───────┴──────────────┴──────────────┴─────────────────────────────────────────────►
```

1. **Giai đoạn khởi tạo:** Toàn tạo repository, đẩy code khung ban đầu lên nhánh `main` và nhánh `develop`.
2. **Giai đoạn thực thi:** Các thành viên pull từ `develop` về và checkout nhánh riêng (`feature/...`) để code độc lập.
3. **Quy chuẩn commit message:**
    * `feat: add lookup tools for doctors and services`
    * `feat: implement booking appointment tool with conflict validation`
    * `test: add multi-turn conversation test scripts`
4. **Giai đoạn tích hợp (Code Review):** Tạo Pull Request vào nhánh `develop`, cả nhóm cùng review chéo code trước khi merge.
5. **Giai đoạn đóng gói:** Merge từ `develop` vào `main`, kiểm tra toàn diện và nộp bài.

---

## IV. 3 Kịch Bản Kiểm Thử Multi-turn Mẫu

### Kịch bản 1: Tư vấn & Đặt lịch khi cung cấp thiếu thông tin (Multi-turn Slot Filling)
* **Lượt 1 (Khách):** *"Chào phòng khám, tôi muốn đặt lịch nhổ răng khôn."*
    * **Bot:** Gọi `getDoctorsBySpecialty(specialty="tiểu phẫu")` $
      ightarrow$ Giới thiệu Bác sĩ Nam và hỏi khách muốn khám vào ngày nào.
* **Lượt 2 (Khách):** *"Tôi muốn khám với Bác sĩ Nam vào ngày 21/08/2026."*
    * **Bot:** Gọi `checkAvailableSlots(doctorName="Bác sĩ Nam", date="2026-08-21")` $
      ightarrow$ Liệt kê các giờ trống (`09:00`, `14:00`, `16:00`), đồng thời hỏi khách chọn giờ và cung cấp Họ tên, SĐT.
* **Lượt 3 (Khách):** *"Tôi chọn 9h sáng nhé. Tôi tên Hoàng, SĐT 0912345678."*
    * **Bot:** Gọi `bookAppointment(customerName="Hoàng", customerPhone="0912345678", doctorName="Bác sĩ Nam", serviceName="Nhổ răng khôn", appointmentDateTime="2026-08-21 09:00")` $
      ightarrow$ Trả về thông báo thành công và mã đặt hẹn `BOOK-102`.

### Kịch bản 2: Đặt lịch trực tiếp với đầy đủ thông tin (Single-turn Execution)
* **Lượt 1 (Khách):** *"Tôi là Quang, SĐT 0987654321, muốn đặt lịch lấy cao răng với Bác sĩ Lan lúc 10h00 ngày 2026-08-22."*
    * **Bot:** Gọi trực tiếp `bookAppointment(...)` không qua bước hỏi lại $
      ightarrow$ Xác nhận thành công ngay lập tức.

### Kịch bản 3: Tra cứu & Đổi lịch hẹn đã có (Context Management & Update)
* **Lượt 1 (Khách):** *"Tôi có mã đặt lịch BOOK-102 nhưng bận, muốn đổi sang 15h00 cùng ngày có được không?"*
    * **Bot:** Gọi `cancelOrReschedule(bookingCode="BOOK-102", action="RESCHEDULE", newDateTime="2026-08-21 15:00")` $
      ightarrow$ Xác nhận đổi lịch thành công.

---

## V. Cấu Trúc Thư Mục / Package Dự Án Chuẩn

```text
src/main/java/com/smilecare/bot/
├── config/
│   ├── AiConfig.java                 // Toàn: Cấu hình ChatClient, ChatMemory, Advisor
│   └── SystemPromptConstant.java     // Toàn: System prompt chi tiết
├── controller/
│   └── ChatController.java           // Toàn: REST API (/api/chat)
├── model/
│   ├── Doctor.java                   // Quang
│   ├── DentalService.java            // Quang
│   └── Appointment.java              // Dương
├── repository/
│   ├── ClinicRepository.java         // Quang: Quản lý dữ liệu in-memory / JPA
│   └── DataInitializer.java          // Quang: Seed dữ liệu mẫu
├── tools/
│   ├── LookupTools.java              // Quang: @Tool tra cứu dịch vụ, bác sĩ, lịch trống
│   └── BookingTools.java             // Dương: @Tool đặt, đổi, hủy lịch khám
└── dto/
    ├── ChatRequest.java              // DTO nhận conversationId và message
    ├── ChatResponse.java             // DTO trả kết quả chat
    └── BookingResultDto.java         // DTO kết quả giao dịch lịch hẹn
```