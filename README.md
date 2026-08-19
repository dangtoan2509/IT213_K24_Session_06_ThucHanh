# KẾ HOẠCH DỰ ÁN & PHÂN CÔNG NHIỆM VỤ
## XÂY DỰNG CHATBOT TƯ VẤN & ĐẶT LỊCH KHÁM NHA KHOA "SMILECARE"
**Công nghệ:** Spring Boot • Spring AI (ChatClient, ChatMemory, @Tool)  
**Nhóm thực hiện:** Quang, Dương, Toàn (Leader), Hoàng

---

## I. Phân Tích Bài Toán & Kiến Trúc Kỹ Thuật

### 1. 5 Nghiệp Vụ Chính (Use Cases)

* **UC1 - Tra cứu bảng giá & dịch vụ nha khoa:** Khách hàng hỏi chi phí, quy trình thực hiện các dịch vụ như nhổ răng, niềng răng, bọc sứ, lấy cao răng...
* **UC2 - Tra cứu thông tin bác sĩ theo chuyên khoa:** Tìm bác sĩ theo chuyên môn (chỉnh nha, tiểu phẫu, nha chu...), xem năm kinh nghiệm và lịch trực.
* **UC3 - Tra cứu khung giờ khám còn trống:** Kiểm tra ngày $X$, bác sĩ $Y$ còn các slot trống nào để khách chọn.
* **UC4 - Đặt lịch hẹn mới:** Tự động thu thập đủ thông tin, kiểm tra tính hợp lệ (trùng giờ, ngoài giờ làm việc) và lưu lịch hẹn.
* **UC5 - Tra cứu, Đổi hoặc Hủy lịch hẹn đã đặt:** Tiếp nhận mã đặt hẹn/SĐT của khách để cập nhật sang khung giờ mới hoặc hủy lịch.

---

### 2. Thiết Kế Danh Sách `@Tool` (Function Calling)

Hệ thống sử dụng cơ chế Function Calling của LLM thông qua annotation `@Tool`. LLM tự phân tích ý định của người dùng và gọi hàm thích hợp kèm tham số đã trích xuất:

| Tên Tool (`@Tool`) | Mục Đích & Description Cho LLM | Tham Số Đầu Vào (`@ToolParam`) | Dữ Liệu Trả Về |
| :--- | :--- | :--- | :--- |
| `searchServices` | Tra cứu thông tin và bảng giá dịch vụ nha khoa | `keyword` *(tùy chọn: tên dịch vụ)* | `List<ServiceDto>` (Tên, giá, mô tả ngắn) |
| `getDoctorsBySpecialty` | Tìm kiếm bác sĩ theo chuyên khoa hoặc xem toàn bộ danh sách | `specialty` *(tùy chọn: chỉnh nha, nhổ răng...)* | `List<DoctorDto>` (Họ tên, chuyên khoa, kinh nghiệm) |
| `checkAvailableSlots` | Kiểm tra các khung giờ trống của bác sĩ trong một ngày cụ thể | `doctorName` *(bắt buộc)*, `date` *(YYYY-MM-DD)* | `List<String>` (Danh sách giờ trống: `09:00`, `10:30`...) |
| `bookAppointment` | Đặt lịch khám bệnh sau khi đã có đầy đủ thông tin khách hàng | `customerName`, `customerPhone`, `doctorName`, `serviceName`, `appointmentDateTime` | `BookingResultDto` (Mã đặt hẹn, trạng thái, thời gian) |
| `cancelOrReschedule` | Hủy hoặc chuyển đổi lịch hẹn sang thời gian khác | `bookingCode`, `action` *(`CANCEL` hoặc `RESCHEDULE`)*, `newDateTime` *(nếu đổi)* | `String` (Thông báo kết quả thực hiện chi tiết) |

---

### 3. Cơ Chế Xử Lý "Đủ Thông Tin" vs "Thiếu Thông Tin"

* **Nguyên tắc cốt lõi:** Tuyệt đối **không** dùng luồng `if-else` cứng để hỏi lại từng câu.
* **Cơ chế điều phối:**
    1. **System Prompt:** Quy định rõ vai trò và điều kiện tiên quyết trước khi đặt lịch:
       > *"Bạn là trợ lý ảo của Nha khoa SmileCare. Khi khách hàng muốn đặt lịch, bạn PHẢI thu thập đủ: Họ và tên, Số điện thoại, Tên dịch vụ hoặc Bác sĩ, Ngày và Giờ khám. Nếu thiếu bất kỳ thông tin nào, hãy tự nhiên hỏi lại khách. Chỉ gọi tool `bookAppointment` khi đã có đầy đủ toàn bộ các thông tin trên."*
    2. **ChatMemory:** Ghi nhớ toàn bộ ngữ cảnh qua `conversationId`. Khách hàng có thể cung cấp thông tin rải rác qua nhiều lượt chat; LLM sẽ tự tổng hợp ngữ cảnh lịch sử để truyền đầy đủ đối số vào `@Tool`.

---

## II. Phân Công Nhiệm Vụ Cho 4 Thành Viên

Mỗi thành viên phụ trách một mảng nghiệp vụ chuyên biệt trên một Git branch độc lập:

```
                      ┌──────────────────────────────────────┐
                      │            [Toàn - Leader]           │
                      │ Core Architecture, ChatClient/Memory │
                      └──────────────────┬───────────────────┘
                                         │
        ┌────────────────────────────────┼────────────────────────────────┐
        ▼                                ▼                                ▼
 ┌───────────────┐              ┌─────────────────┐              ┌─────────────────┐
 │    [Quang]    │              │     [Dương]     │              │     [Hoàng]     │
 │ Data Layer &  │              │Transaction Tools│              │ UI, Testing &   │
 │ Lookup Tools  │              │ (Booking/Cancel)│              │ Multi-turn QA   │
 └───────────────┘              └─────────────────┘              └─────────────────┘
```

### 1. Toàn (Leader) — Kiến Trúc Hệ Thống, ChatClient & ChatMemory
* **Nhánh Git:** `feature/toan-core-chatclient-memory`
* **Nhiệm vụ chi tiết:**
    * Khởi tạo dự án Spring Boot, cấu hình Spring AI (kết nối LLM model).
    * Xây dựng **System Prompt** chuẩn mực định hình phong cách, nghiệp vụ cho trợ lý SmileCare.
    * Cấu hình bean `ChatMemory` (sử dụng `MessageWindowChatMemory`) và gắn vào `ChatClient` thông qua `MessageChatMemoryAdvisor`.
    * Khởi tạo bean `ChatClient` dùng chung (`@Bean ChatClient`) gắn sẵn default system prompt và advisors.
    * Xây dựng REST API endpoint chính: `POST /api/chat` tiếp nhận `conversationId` và `message`.
    * Quản lý Git repository: review Pull Request, giải quyết merge conflict và chốt bản phát hành.

### 2. Quang — Tầng Dữ Liệu & Nhóm `@Tool` Tra Cứu (UC1, UC2, UC3)
* **Nhánh Git:** `feature/quang-data-lookup-tools`
* **Nhiệm vụ chi tiết:**
    * Thiết kế Model/Entity dữ liệu: `Doctor`, `DentalService`, `Appointment` (sử dụng In-memory Storage hoặc H2 Database).
    * Viết class `DataInitializer` để seed dữ liệu ban đầu (3-5 bác sĩ, 5-7 dịch vụ nha khoa, một số lịch hẹn mẫu).
    * Xây dựng 3 `@Tool` tra cứu:
        1. `searchServices(keyword)`
        2. `getDoctorsBySpecialty(specialty)`
        3. `checkAvailableSlots(doctorName, date)`
    * Khai báo đầy đủ annotation `@Tool` và `@ToolParam` với description chi tiết, rõ ràng để LLM nhận diện chính xác.

### 3. Dương — Nhóm `@Tool` Giao Dịch & Xử Lý Nghiệp Vụ (UC4, UC5)
* **Nhánh Git:** `feature/duong-booking-tools`
* **Nhiệm vụ chi tiết:**
    * Xây dựng 2 `@Tool` giao dịch:
        1. `bookAppointment(...)`: Kiểm tra trùng lịch của bác sĩ, lưu thông tin đặt lịch, sinh mã `BOOK-XXXX`.
        2. `cancelOrReschedule(...)`: Tra cứu mã lịch hẹn, cập nhật trạng thái hoặc đổi giờ khám.
    * Xử lý validation nghiệp vụ: cảnh báo lịch trùng, kiểm tra giờ khám hợp lệ trong khung giờ làm việc của phòng khám.
    * Chuẩn hóa dữ liệu trả về dạng JSON/DTO rõ ràng để LLM đọc và phản hồi tự nhiên cho khách.

### 4. Hoàng — Giao Diện Test, Kịch Bản Multi-turn & QA / README
* **Nhánh Git:** `feature/hoang-chat-ui-and-e2e-tests`
* **Nhiệm vụ chi tiết:**
    * Xây dựng giao diện chat Web đơn giản (HTML/CSS/JS) cho phép nhập tùy chỉnh `conversationId` để test nhiều phiên hội thoại độc lập.
    * Xây dựng **3 bộ kịch bản kiểm thử Multi-turn chi tiết** kèm log kết quả (Prompt $
      ightarrow$ Tool Call $
      ightarrow$ Model Response).
    * Thực hiện kiểm thử các trường hợp đặc biệt (Edge Cases): khách đổi ý giữa chừng, nhập sai định dạng ngày giờ, cung cấp thông tin lắt léo.
    * Viết tài liệu `README.md`: hướng dẫn cài đặt, chạy ứng dụng, cấu hình API key, bảng phân công vai trò và mô tả kịch bản test.

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