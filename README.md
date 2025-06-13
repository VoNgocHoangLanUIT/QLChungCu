# 🏢 Condominium Management System

**📅 Duration:** Feb 2025 – Present  
**🛠 Technologies:** Java Swing, JDBC, Oracle, StarUML, Role-Based Access Control (RBAC)

---

## 🏢 TalentHub - Hệ thống Quản lý Chung cư
TalentHub là một ứng dụng desktop toàn diện được phát triển để hợp lý hóa và số hóa các hoạt động thiết yếu của một khu chung cư, giúp ban quản lý và cư dân tương tác hiệu quả và minh bạch.

## 🎯 Mục tiêu dự án
Hệ thống được xây dựng nhằm mục đích đơn giản hóa và tự động hóa các quy trình quản lý vận hành chung cư:

* Đối với Ban Quản lý & Nhân viên:
  - Quản lý thông tin căn hộ và cư dân một cách tập trung.
  - Theo dõi các dịch vụ (điện, nước,...) và tự động hóa quy trình xuất hóa đơn hàng tháng.
  - Quản lý việc đặt các tiện ích chung (hồ bơi, phòng gym,...).
  - Tiếp nhận và xử lý các phản ánh, khiếu nại một cách có hệ thống.
* Đối với Cư dân:
  - Dễ dàng cập nhật thông tin cá nhân.
  - Xem và tra cứu hóa đơn dịch vụ hàng tháng.
  - Đặt trước các tiện ích chung một cách thuận tiện.
  - Gửi phản ánh, khiếu nại và theo dõi trạng thái xử lý.
## 🔧 Tính năng chính
  - ✅ Quản lý Căn hộ & Cư dân: Thêm, cập nhật, và quản lý thông tin chi tiết của các căn hộ và hồ sơ cư dân.
  - ✅ Quản lý Dịch vụ & Hóa đơn: Theo dõi việc sử dụng các dịch vụ tiện ích và tự động tạo hóa đơn hàng tháng.
  - ✅ Đặt tiện ích chung: Cho phép cư dân xem lịch và đặt các tiện ích như phòng gym, hồ bơi, phòng sinh hoạt chung.
  - ✅ Tiếp nhận & Xử lý Khiếu nại: Cư dân có thể gửi khiếu nại trực tuyến; nhân viên quản lý và giải quyết.
  - ✅ Báo cáo & Thống kê: Cung cấp các báo cáo cơ bản về tình hình thu phí, sử dụng tiện ích,... (dự kiến phát triển).
  - 🔐 Phân quyền người dùng (RBAC):
    - `ADMIN`
    - `STAFF`
    - `RESIDENT`

---
## 🧰 Công nghệ sử dụng
|      Thành phần       | Công nghệ |
| :-------------------- | :----------------------------------------- |
| **GUI Framework**     | `Java Swing`                               |
| **Kết nối CSDL**      | `JDBC (Java Database Connectivity)`        |
| **Cơ sở dữ liệu**     | `Oracle Database`                          |
| **Thiết kế hệ thống** | `StarUML` (Use Case, Sequence, Class, ERD) |
| **Bảo mật**           | `Role-Based Access Control (RBAC)`         |

## 🚀 Cài đặt và chạy hệ thống
### 1. Clone repository
```bash
git https://github.com/VoNgocHoangLanUIT/QLChungCu
cd QLChungCu
```
### 2. Cấu hình database Oracle
- Tạo một schema mới trong Oracle (ví dụ: CONDOMANAGEMENT).
- Import dữ liệu và cấu trúc bảng từ file .sql hoặc .dmp được cung cấp trong dự án.

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:orcl
spring.datasource.username=QLChungCu
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```
### 3. Cài đặt môi trường
- Cài đặt JDK 11+.
- Cấu hình biến môi trường JAVA_HOME.
- Sử dụng một IDE hỗ trợ Java như IntelliJ IDEA hoặc Eclipse để mở dự án.
### 4. Chạy ứng dụng
- Mở dự án bằng IDE của bạn.
- Tìm đến file chứa hàm main (ví dụ: MainApplication.java hoặc LoginFrame.java).
- Chạy file đó để khởi động ứng dụng.
- 📈 Cải tiến trong tương lai
- Tích hợp cổng thanh toán trực tuyến để cư dân thanh toán hóa đơn dễ dàng.
- Xây dựng module gửi thông báo qua Email/SMS cho các sự kiện quan trọng.
- Phát triển dashboard phân tích nâng cao cho ADMIN.
- Xây dựng phiên bản ứng dụng di động cho cư dân.

## ✍️ Đóng góp
Chúng tôi luôn hoan nghênh mọi đóng góp để cải thiện hệ thống. Bạn có thể:

- Tạo **Issue** nếu phát hiện lỗi hoặc cần đề xuất tính năng.
- Tạo **Pull Request** để gửi đóng góp mã nguồn.

---